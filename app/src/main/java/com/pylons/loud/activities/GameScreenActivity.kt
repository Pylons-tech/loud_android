package com.pylons.loud.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.pylons.loud.R
import com.pylons.loud.constants.FightId.ID_RABBIT
import com.pylons.loud.constants.Item.COPPER_SWORD
import com.pylons.loud.constants.Item.DROP_DRAGONACID
import com.pylons.loud.constants.Item.DROP_DRAGONFIRE
import com.pylons.loud.constants.Item.DROP_DRAGONICE
import com.pylons.loud.constants.Item.GOBLIN_EAR
import com.pylons.loud.constants.Item.TROLL_TOES
import com.pylons.loud.constants.Item.WOLF_TAIL
import com.pylons.loud.constants.Item.WOODEN_SWORD
import com.pylons.loud.constants.ItemID.ID_ANGEL_SWORD
import com.pylons.loud.constants.ItemID.ID_BRONZE_SWORD
import com.pylons.loud.constants.ItemID.ID_COPPER_SWORD
import com.pylons.loud.constants.ItemID.ID_IRON_SWORD
import com.pylons.loud.constants.ItemID.ID_SILVER_SWORD
import com.pylons.loud.constants.ItemID.ID_WOODEN_SWORD
import com.pylons.loud.constants.LocationConstants
import com.pylons.loud.constants.Recipe.RCP_BUY_ANGEL_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_BRONZE_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_CHARACTER
import com.pylons.loud.constants.Recipe.RCP_BUY_COPPER_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_IRON_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_SILVER_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_WOODEN_SWORD
import com.pylons.loud.constants.Recipe.RCP_COPPER_SWORD_UPG
import com.pylons.loud.constants.Recipe.RCP_SELL_SWORD
import com.pylons.loud.constants.Recipe.RCP_WOODEN_SWORD_UPG
import com.pylons.loud.fragments.Character.CharacterFragment
import com.pylons.loud.fragments.Fight.FightFragment
import com.pylons.loud.fragments.ForestScreen.ForestFightPreviewFragment
import com.pylons.loud.fragments.Item.ItemFragment
import com.pylons.loud.fragments.PlayerLocation.PlayerLocationFragment
import com.pylons.loud.models.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Transaction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

import kotlinx.android.synthetic.main.content_game_screen.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.logging.Logger

class GameScreenActivity : AppCompatActivity(),
    PlayerLocationFragment.OnListFragmentInteractionListener,
    FightFragment.OnListFragmentInteractionListener,
    ItemFragment.OnListFragmentInteractionListener,
    CharacterFragment.OnListFragmentInteractionListener,
    ForestFightPreviewFragment.OnFragmentInteractionListener {
    private val Log = Logger.getLogger(GameScreenActivity::class.java.name)

    class SharedViewModel : ViewModel() {
        private val player = MutableLiveData<User>()
        private val playerLocation = MutableLiveData<Int>()
        private val fightPreview = MutableLiveData<Fight>()
        private val playerAction = MutableLiveData<String>()

        fun getPlayer(): LiveData<User> {
            return player
        }

        fun setPlayer(user: User) {
            player.value = user
        }

        fun getPlayerLocation(): LiveData<Int> {
            return playerLocation
        }

        fun setPlayerLocation(location: Int) {
            playerLocation.value = location
        }

        fun getFightPreview(): LiveData<Fight> {
            return fightPreview
        }

        fun setFightPreview(fight: Fight) {
            fightPreview.value = fight
        }

        fun getPlayerAction(): LiveData<String> {
            return playerAction
        }

        fun setPlayerAction(action: String) {
            playerAction.value = action
        }
    }

    private val model: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_account), Context.MODE_PRIVATE
        )

        val currentUsername = sharedPref.getString(getString(R.string.key_current_account), "");

        if (currentUsername.equals("")) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val playerJSON = sharedPref.getString(currentUsername, "");

        if (playerJSON.equals("")) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<User> =
            moshi.adapter<User>(User::class.java)

        val currentPlayer = jsonAdapter.fromJson(playerJSON)
        if (currentPlayer != null) {
            val model: SharedViewModel by viewModels()
            model.setPlayer(currentPlayer)
        }

        model.getPlayerAction().observe(this, Observer<String> {
            Log.info(it)

            val player = model.getPlayer().value
            if (player != null) {
                layout_loading.visibility = View.VISIBLE
                CoroutineScope(IO).launch {
                    val tx = executeRecipe(it, arrayOf())
                    syncProfile()

                    withContext(Main) {
                        layout_loading.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@GameScreenActivity,
                            "Success: $it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    override fun onFight(fight: Fight?) {
        if (fight != null) {
            model.getPlayer().value?.let {
                if (fight.meetsRequirements(it)) {
                    val frag =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
                        .navigate(R.id.forestFightPreviewFragment)
                    model.setFightPreview(fight)
                } else {
                    Toast.makeText(
                        this,
                        "Need ${fight.requirements.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onLocation(location: PlayerLocation?) {
        if (location != null) {
            when (location.id) {
                LocationConstants.HOME -> {
                    nav_host_fragment.findNavController().navigate(R.id.homeScreenFragment)
                }
                LocationConstants.FOREST -> {
                    if (model.getPlayer().value?.activeCharacter == -1) {
                        Toast.makeText(
                            this,
                            R.string.you_cant_go_to_forest_without_character, Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    nav_host_fragment.findNavController().navigate(R.id.forestScreenFragment)
                }
                LocationConstants.SHOP -> {
                    nav_host_fragment.findNavController().navigate(R.id.shopScreenFragment)
                }
                LocationConstants.PYLONS_CENTRAL -> {
                    nav_host_fragment.findNavController().navigate(R.id.pylonCentralFragment)
                }
                LocationConstants.SETTINGS -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Log.warning("Not exist")
                }
            }
        }
    }

    override fun onItemSelect(item: Item?) {
        val name = item?.name
        val player = model.getPlayer().value

        if (player != null) {
            var prompt = "Set $name as active weapon?"
            if (player.getActiveWeapon() == item) {
                prompt = "Unset $name as active weapon?"
            }
            val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage(prompt)
                .setCancelable(false)
                .setPositiveButton("Proceed") { _, _ ->
                    if (player.getActiveWeapon() == item) {
                        player.activeWeapon = -1
                    } else {
                        player.setActiveWeapon(item as Weapon)
                    }
                    model.setPlayer(player)
                    player.saveAsync(this)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Confirm")
            alert.show()
        }
    }

    override fun onItemBuy(item: Item?) {
        val name = item?.name
        val price = (item as Weapon).price
        val goldIcon = getString(R.string.gold_icon)
        val player = model.getPlayer().value ?: return

        Log.info(item.toString())
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage("Buy $name for $goldIcon $price?")
            .setCancelable(false)
            .setPositiveButton("Buy") { _, _ ->
                val itemIds = mutableListOf<String>()
                var recipeId = ""

                when (item?.id) {
                    ID_WOODEN_SWORD -> {
                        recipeId = RCP_BUY_WOODEN_SWORD
                    }
                    ID_COPPER_SWORD -> {
                        recipeId = RCP_BUY_COPPER_SWORD
                    }
                    ID_SILVER_SWORD -> {
                        recipeId = RCP_BUY_SILVER_SWORD
                        itemIds.add(player.getItemIdByName(GOBLIN_EAR))
                    }
                    ID_BRONZE_SWORD -> {
                        recipeId = RCP_BUY_BRONZE_SWORD
                        itemIds.add(player.getItemIdByName(WOLF_TAIL))
                    }
                    ID_IRON_SWORD -> {
                        recipeId = RCP_BUY_IRON_SWORD
                        itemIds.add(player.getItemIdByName(TROLL_TOES))
                    }
                    ID_ANGEL_SWORD -> {
                        recipeId = RCP_BUY_ANGEL_SWORD
                        itemIds.add(player.getItemIdByName(DROP_DRAGONFIRE))
                        itemIds.add(player.getItemIdByName(DROP_DRAGONICE))
                        itemIds.add(player.getItemIdByName(DROP_DRAGONACID))
                    }
                }

                if (player.gold < item.price) {
                    Toast.makeText(
                        this@GameScreenActivity,
                        getString(R.string.you_dont_have_enough_gold),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (itemIds.contains("")) {
                    Toast.makeText(
                        this@GameScreenActivity,
                        getString(R.string.you_dont_have_enough_resources),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                layout_loading.visibility = View.VISIBLE

                CoroutineScope(IO).launch {
                    val tx = executeRecipe(recipeId, itemIds.toTypedArray())
                    syncProfile()

                    Log.info(tx.toString())

                    withContext(Main) {
                        layout_loading.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@GameScreenActivity,
                            getString(R.string.you_have_bought_from_shop, name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Confirm")
        alert.show()
    }

    override fun onItemSell(item: Item?) {
        val player = model.getPlayer().value
        if (player != null && item != null) {
            val name = item.name
            val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage("Sell $name?")
                .setCancelable(false)
                .setPositiveButton("Sell") { _, _ ->
                    layout_loading.visibility = View.VISIBLE

                    CoroutineScope(IO).launch {
                        val tx = executeRecipe(RCP_SELL_SWORD, arrayOf(item.id))
                        syncProfile()

                        Log.info(tx.toString())

                        var amount = 0L

                        if (tx != null) {
                            val output = tx.txData.output
                            if (output.isNotEmpty()) {
                                amount = output[0].amount
                            }
                        }

                        withContext(Main) {
                            Toast.makeText(
                                this@GameScreenActivity,
                                getString(R.string.you_sold_item_for_gold, name, amount),
                                Toast.LENGTH_SHORT
                            ).show()
                            layout_loading.visibility = View.INVISIBLE
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Confirm")
            alert.show()
        }
    }

    override fun onItemUpgrade(item: Item?) {
        val name = item?.name
        val player = model.getPlayer().value

        if (item is Weapon && player != null) {
            if (player.gold > item.getUpgradePrice()) {
                val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                dialogBuilder.setMessage("Upgrade $name?")
                    .setCancelable(false)
                    .setPositiveButton("Upgrade") { _, _ ->
                        val recipeId = when (item.name) {
                            WOODEN_SWORD -> RCP_WOODEN_SWORD_UPG
                            COPPER_SWORD -> RCP_COPPER_SWORD_UPG
                            else -> ""
                        }

                        layout_loading.visibility = View.VISIBLE

                        CoroutineScope(IO).launch {
                            val tx = executeRecipe(recipeId, arrayOf(item.id))
                            syncProfile()

                            Log.info(tx.toString())

                            withContext(Main) {
                                layout_loading.visibility = View.INVISIBLE
                                Toast.makeText(
                                    this@GameScreenActivity,
                                    getString(R.string.you_have_upgraded_item, name),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle("Confirm")
                alert.show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.you_dont_have_enough_gold_to_upgrade, name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    override fun onCharacter(item: Character?) {
        val name = item?.name
        val player = model.getPlayer().value
        if (player != null) {
            var prompt = "Set ${name} as active character?"
            if (player.getActiveCharacter() == item) {
                prompt = "Unset $name as active character?"
            }
            val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage(prompt)
                .setCancelable(false)
                .setPositiveButton("Proceed") { _, _ ->
                    if (player.getActiveCharacter() == item) {
                        player.activeCharacter = -1
                    } else {
                        player.setActiveCharacter(item as Character)
                    }
                    model.setPlayer(player)
                    player.saveAsync(this)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Confirm")
            alert.show()
        }
    }

    private suspend fun executeRecipe(recipeId: String, itemIds: Array<String>): Transaction? {
        val tx = Core.engine.applyRecipe(
            recipeId,
            itemIds
        )
        tx.submit()
        delay(5000)
        Log.info(tx.toString())
        Log.info(tx.id)

        delay(5000)
        val txId = tx.id
        if (txId != null) {
            val tx = Core.engine.getTransaction(txId)
            Log.info(tx.toString())
            return tx
        }

        return null
    }

    private suspend fun syncProfile() {
        val player = model.getPlayer().value
        if (player != null) {
            val profile = Core.engine.getOwnBalances()
            if (profile != null) {
                player.syncProfile(profile)
                withContext(Main) {
                    model.setPlayer(player)
                }
                player.saveAsync(this@GameScreenActivity)
                Log.info("saved user")
            }
        }

        Log.info("Done syncProfile")
    }

    override fun onBuyCharacter(item: Character?) {
        val name = item?.name
        val price = item?.price
        val pylonIcon = getString(R.string.pylon_icon)
        val player = model.getPlayer().value

        if (player != null) {
            val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage("Buy $name for $pylonIcon $price?")
                .setCancelable(false)
                .setPositiveButton("Proceed") { _, _ ->
                    layout_loading.visibility = View.VISIBLE
                    CoroutineScope(IO).launch {
                        val tx = executeRecipe(RCP_BUY_CHARACTER, arrayOf())
                        syncProfile()
                        withContext(Main) {
                            layout_loading.visibility = View.INVISIBLE
                            Toast.makeText(
                                this@GameScreenActivity,
                                getString(R.string.you_have_bought_from_pylons_central, name),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Confirm")
            alert.show()
        }
    }

    override fun onEngageFight(fight: Fight, recipeId: String, itemIds: Array<String>) {
        Log.info(recipeId)

        itemIds.forEach {
            Log.info(it)
        }

        val player = model.getPlayer().value
        if (player != null) {
            layout_loading.visibility = View.VISIBLE

            CoroutineScope(IO).launch {
                val tx = executeRecipe(recipeId, itemIds)
                syncProfile()
                Log.info(tx?.txData.toString())

                var prompt = ""
                if (tx != null) {
                    val output = tx.txData.output
                    if (output.isEmpty()) {
                        prompt = getString(R.string.you_were_killed, fight.name)
                    } else {
                        prompt = getString(
                            R.string.you_did_fight_with_and_earned, fight.name,
                            tx.txData.output[0].amount
                        )

                        when (output.size) {
                            2 -> {
                                // Rabbit does not use weapon
                                if (fight.id != ID_RABBIT) {
                                    prompt += "\n ${getString(R.string.you_have_lost_your_weapon)}"
                                }
                            }
                            4 -> prompt += "\n ${getString(
                                R.string.you_got_bonus_item,
                                player.getItemNameByItemId(tx.txData.output[3].itemId)
                            )}"
                        }
                    }
                }

                withContext(Main) {
                    layout_loading.visibility = View.INVISIBLE
                    val dialogBuilder =
                        AlertDialog.Builder(this@GameScreenActivity, R.style.MyDialogTheme)
                    dialogBuilder.setMessage(
                        prompt
                    )
                        .setCancelable(false)
                        .setPositiveButton("OK") { _, _ ->
                        }
                    val alert = dialogBuilder.create()
                    alert.show()
                }
            }
        }
    }
}
