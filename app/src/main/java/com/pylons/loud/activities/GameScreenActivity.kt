package com.pylons.loud.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pylons.loud.R
import com.pylons.loud.constants.FightId.ID_ACID_GIANT
import com.pylons.loud.constants.FightId.ID_FIRE_GIANT
import com.pylons.loud.constants.FightId.ID_GIANT
import com.pylons.loud.constants.FightId.ID_ICE_GIANT
import com.pylons.loud.constants.FightId.ID_RABBIT
import com.pylons.loud.constants.FightRequirements.ACID_SPECIAL
import com.pylons.loud.constants.FightRequirements.FIRE_SPECIAL
import com.pylons.loud.constants.FightRequirements.ICE_SPECIAL
import com.pylons.loud.constants.FightRequirements.NO_SPECIAL
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
import com.pylons.loud.constants.Location.FOREST
import com.pylons.loud.constants.Location.FRIENDS
import com.pylons.loud.constants.Location.HOME
import com.pylons.loud.constants.Location.INVENTORY
import com.pylons.loud.constants.Location.PYLONS_CENTRAL
import com.pylons.loud.constants.Location.SETTINGS
import com.pylons.loud.constants.Location.SHOP
import com.pylons.loud.constants.Recipe.RCP_BUY_ANGEL_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_BRONZE_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_CHARACTER
import com.pylons.loud.constants.Recipe.RCP_BUY_COPPER_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_GOLD_WITH_PYLON
import com.pylons.loud.constants.Recipe.RCP_BUY_IRON_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_SILVER_SWORD
import com.pylons.loud.constants.Recipe.RCP_BUY_WOODEN_SWORD
import com.pylons.loud.constants.Recipe.RCP_COPPER_SWORD_UPGRADE
import com.pylons.loud.constants.Recipe.RCP_GET_TEST_ITEMS
import com.pylons.loud.constants.Recipe.RCP_SELL_SWORD
import com.pylons.loud.constants.Recipe.RCP_WOODEN_SWORD_UPGRADE
import com.pylons.loud.fragments.lists.character.CharacterFragment
import com.pylons.loud.fragments.lists.fight.FightFragment
import com.pylons.loud.fragments.lists.friend.FriendFragment
import com.pylons.loud.fragments.screens.forest.ForestFightPreviewFragment
import com.pylons.loud.fragments.lists.item.ItemFragment
import com.pylons.loud.fragments.screens.pyloncentral.CreateTradeFragment
import com.pylons.loud.fragments.screens.pyloncentral.PylonCentralHomeFragment
import com.pylons.loud.fragments.screens.setting.SettingsScreenFragment
import com.pylons.loud.fragments.lists.itemspec.ItemSpecFragment
import com.pylons.loud.fragments.lists.trade.TradeFragment
import com.pylons.loud.fragments.screens.senditem.SendItemConfirmFragment
import com.pylons.loud.fragments.screens.senditem.SendItemViewModel
import com.pylons.loud.fragments.ui.BottomNavigationFragment
import com.pylons.loud.fragments.ui.blockchainstatus.BlockChainStatusViewModel
import com.pylons.loud.models.*
import com.pylons.loud.models.fight.Fight
import com.pylons.loud.models.trade.*
import com.pylons.loud.utils.Account.getCurrentUser
import com.pylons.loud.utils.CoreController.getItemById
import com.pylons.loud.utils.RenderText.getFightIcon
import com.pylons.loud.utils.UI.displayLoading
import com.pylons.loud.utils.UI.displayMessage
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.CoinOutput
import com.pylons.wallet.core.types.tx.trade.TradeItemInput
import kotlinx.android.synthetic.main.bottom_sheet_friend.view.*

import kotlinx.android.synthetic.main.content_game_screen.*
import kotlinx.android.synthetic.main.dialog_input_text.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.fixedRateTimer

class GameScreenActivity : AppCompatActivity(),
    BottomNavigationFragment.OnFragmentInteractionListener,
    FightFragment.OnListFragmentInteractionListener,
    ItemFragment.OnListFragmentInteractionListener,
    CharacterFragment.OnListFragmentInteractionListener,
    ForestFightPreviewFragment.OnFragmentInteractionListener,
    PylonCentralHomeFragment.OnFragmentInteractionListener,
    SettingsScreenFragment.OnFragmentInteractionListener,
    TradeFragment.OnListFragmentInteractionListener,
    CreateTradeFragment.OnFragmentInteractionListener,
    ItemSpecFragment.OnListFragmentInteractionListener,
    FriendFragment.OnListFragmentInteractionListener,
    SendItemConfirmFragment.OnFragmentInteractionListener {
    private val Log = Logger.getLogger(GameScreenActivity::class.java.name)

    class SharedViewModel : ViewModel() {
        private val player = MutableLiveData<User>()
        private val playerLocation = MutableLiveData<Int>()
        lateinit var fightPreview: Fight
        var shopAction = 0
        private val tradeInput = MutableLiveData<ItemSpec>()
        private val tradeOutput = MutableLiveData<com.pylons.wallet.core.types.tx.item.Item>()
        lateinit var trade: Trade
        lateinit var tradeBuyMatchingItems: List<Item>

        fun getPlayer(): MutableLiveData<User> {
            return player
        }

        fun setPlayer(user: User) {
            player.value = user
        }

        fun getPlayerLocation(): MutableLiveData<Int> {
            return playerLocation
        }

        fun setPlayerLocation(location: Int) {
            playerLocation.value = location
        }

        fun getTradeInput(): MutableLiveData<ItemSpec> {
            return tradeInput
        }

        fun setTradeInput(item: ItemSpec?) {
            tradeInput.value = item
        }

        fun getTradeOutput(): MutableLiveData<com.pylons.wallet.core.types.tx.item.Item> {
            return tradeOutput
        }

        fun setTradeOutput(item: com.pylons.wallet.core.types.tx.item.Item?) {
            tradeOutput.value = item
        }
    }

    private val model: SharedViewModel by viewModels()
    private val sendItemViewModel: SendItemViewModel by viewModels()
    private val blockChainStatusViewModel: BlockChainStatusViewModel by viewModels()
    private lateinit var getStatusBlockTimer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        val currentPlayer = getCurrentUser(this)
        if (currentPlayer != null) {
            model.setPlayer(currentPlayer)
            getStatusBlockTimer = fixedRateTimer("getStatusBlock", false, 0, 5000) {
                CoroutineScope(IO).launch {
                    blockChainStatusViewModel.getStatusBlock()
                }
            }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::getStatusBlockTimer.isInitialized) {
            getStatusBlockTimer.cancel()
        }
    }

    override fun onFight(fight: Fight) {
        model.getPlayer().value?.let {
            if (fight.meetsRequirements(it)) {
                model.fightPreview = fight
                val frag =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
                    .navigate(R.id.forestFightPreviewFragment)
            } else {
                var prompt = "Need ${fight.requirements.joinToString(", ")}"
                prompt = prompt.replace(NO_SPECIAL, "non-special character")
                prompt = prompt.replace(FIRE_SPECIAL, "fire character")
                prompt = prompt.replace(ICE_SPECIAL, "ice character")
                prompt = prompt.replace(ACID_SPECIAL, "acid character")
                Toast.makeText(
                    this,
                    prompt,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onNavigation(id: Int) {
        nav_host_fragment.findNavController().popBackStack()
        when (id) {
            HOME -> {
                nav_host_fragment.findNavController().navigate(R.id.homeScreenFragment)
            }
            FOREST -> {
                if (model.getPlayer().value?.activeCharacter == -1) {
                    Toast.makeText(
                        this,
                        R.string.you_cant_go_to_forest_without_character, Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                nav_host_fragment.findNavController().navigate(R.id.forestScreenFragment)
            }
            SHOP -> {
                nav_host_fragment.findNavController().navigate(R.id.shopScreenFragment)
            }
            PYLONS_CENTRAL -> {
                nav_host_fragment.findNavController().navigate(R.id.pylonCentralFragment)
            }
            FRIENDS -> {
                nav_host_fragment.findNavController().navigate(R.id.friendsScreenFragment)
            }
            SETTINGS -> {
                nav_host_fragment.findNavController().navigate(R.id.settingsScreenFragment)
            }
            INVENTORY -> {
                nav_host_fragment.findNavController().navigate(R.id.inventoryFragment)
            }
            else -> {
                Log.warning("Not exist")
            }
        }
    }

    override fun onItemSelect(item: Item) {
        val name = item.name
        val player = model.getPlayer().value

        if (player != null) {
            var prompt = "Set $name as active weapon?"
            if (player.getActiveWeapon() == item) {
                prompt = "Unset $name as active weapon?"
            }
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(prompt)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    if (player.getActiveWeapon() == item) {
                        player.activeWeapon = -1
                    } else {
                        player.setActiveWeapon(item as Weapon)
                    }
                    model.setPlayer(player)
                    player.saveAsync(this)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }
    }

    override fun onItemBuy(item: Item) {
        val name = item.name
        val price = (item as Weapon).price
        val goldIcon = getString(R.string.gold_icon)
        val player = model.getPlayer().value ?: return

        Log.info(item.toString())

        var prompt = "Buy $name for $goldIcon $price"
        if (item.preItem.isNotEmpty()) {
            val preItems = item.preItem.joinToString(", ")
            prompt += " and $preItems"
        }
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("$prompt?")
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val itemIds = mutableListOf<String>()
                var recipeId = ""

                when (item.id) {
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

                val loading = displayLoading(
                    this,
                    getString(
                        R.string.loading_buy_shop_item,
                        item.name
                    )
                )

                CoroutineScope(IO).launch {
                    val tx = executeRecipe(recipeId, itemIds.toTypedArray())
                    syncProfile()

                    if (tx?.txError != null) {
                        withContext(Main) {
                            loading.dismiss()
                            displayMessage(
                                this@GameScreenActivity,
                                getString(R.string.execute_recipe_error)
                            )
                        }
                    } else {
                        withContext(Main) {
                            loading.dismiss()
                            displayMessage(
                                this@GameScreenActivity,
                                getString(R.string.you_have_bought_from_shop, name)
                            )
                            tx?.id?.let { blockChainStatusViewModel.setTx(it) }

                            if (player.activeWeapon == -1) {
                                val index = player.weapons.indexOfFirst {
                                    it.id == tx?.txData?.output?.get(0)?.itemId
                                }
                                player.setActiveWeapon(player.weapons[index])
                                model.setPlayer(player)
                                player.saveAsync(this@GameScreenActivity)
                            }
                        }
                        onNavigation(INVENTORY)
                    }
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.show()
    }

    override fun onItemSell(item: Item) {
        val player = model.getPlayer().value
        if (player != null) {
            val name = item.name
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Sell $name for ${getString(R.string.gold_icon)} ${item.getSellPriceRange()}?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->

                    val loading = displayLoading(
                        this,
                        getString(
                            R.string.loading_sell_shop_item,
                            item.name,
                            "${getString(R.string.gold_icon)} ${item.getSellPriceRange()}"
                        )
                    )

                    CoroutineScope(IO).launch {
                        val tx = executeRecipe(RCP_SELL_SWORD, arrayOf(item.id))
                        syncProfile()

                        if (tx?.txError != null) {
                            withContext(Main) {
                                loading.dismiss()
                                displayMessage(
                                    this@GameScreenActivity,
                                    getString(R.string.execute_recipe_error)
                                )
                            }
                        } else {
                            var amount = 0L

                            if (tx != null) {
                                val output = tx.txData.output
                                if (output.isNotEmpty()) {
                                    amount = output[0].amount
                                }
                            }

                            withContext(Main) {
                                loading.dismiss()
                                displayMessage(
                                    this@GameScreenActivity,
                                    getString(R.string.you_sold_item_for_gold, name, amount)
                                )
                                tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                            }
                        }
                    }
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }
    }

    override fun onItemUpgrade(item: Item) {
        val name = item.name
        val player = model.getPlayer().value

        if (item is Weapon && player != null) {
            if (player.gold > item.getUpgradePrice()) {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Upgrade $name?")
                    .setCancelable(false)
                    .setPositiveButton("Upgrade") { _, _ ->
                        val recipeId = when (item.name) {
                            WOODEN_SWORD -> RCP_WOODEN_SWORD_UPGRADE
                            COPPER_SWORD -> RCP_COPPER_SWORD_UPGRADE
                            else -> ""
                        }

                        val loading =
                            displayLoading(
                                this,
                                getString(R.string.loading_upgrade_shop_item, item.name)
                            )
                        CoroutineScope(IO).launch {
                            val tx = executeRecipe(recipeId, arrayOf(item.id))
                            syncProfile()

                            if (tx?.txError != null) {
                                withContext(Main) {
                                    loading.dismiss()
                                    displayMessage(
                                        this@GameScreenActivity,
                                        getString(R.string.execute_recipe_error)
                                    )
                                }
                            } else {
                                withContext(Main) {
                                    loading.dismiss()
                                    displayMessage(
                                        this@GameScreenActivity,
                                        getString(R.string.you_have_upgraded_item, name)
                                    )
                                    tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                                }
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.show()
            } else {
                displayMessage(this, getString(R.string.you_dont_have_enough_gold_to_upgrade, name))
            }
        }


    }

    override fun onCharacter(item: Character) {
        val name = item.name
        val player = model.getPlayer().value
        if (player != null) {
            var prompt = "Set ${name} as active character?"
            if (player.getActiveCharacter() == item) {
                prompt = "Unset $name as active character?"
            }
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(prompt)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    if (player.getActiveCharacter() == item) {
                        player.activeCharacter = -1
                    } else {
                        player.setActiveCharacter(item)
                    }
                    model.setPlayer(player)
                    player.saveAsync(this)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }
    }

    private suspend fun executeRecipe(recipeId: String, itemIds: Array<String>): Transaction? {
        val tx = Core.engine.applyRecipe(
            recipeId,
            itemIds
        )
        tx.submit()
        Log.info(tx.toString())
        Log.info(tx.id)

        // TODO("Remove delay, walletcore should handle it")
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

    override fun onBuyCharacter(item: Character) {
        val player = model.getPlayer().value
        if (player != null) {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(getString(R.string.buy_character_prompt, item.name))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    val loading =
                        displayLoading(this, getString(R.string.loading_buy_character, item.name))
                    CoroutineScope(IO).launch {
                        val tx = executeRecipe(RCP_BUY_CHARACTER, arrayOf())
                        syncProfile()

                        if (tx?.txError != null) {
                            withContext(Main) {
                                loading.dismiss()
                                displayMessage(
                                    this@GameScreenActivity,
                                    getString(R.string.execute_recipe_error)
                                )
                            }
                        } else {
                            withContext(Main) {
                                loading.dismiss()
                                displayMessage(
                                    this@GameScreenActivity, getString(
                                        R.string.buy_character_complete,
                                        item.name
                                    )
                                )
                                tx?.id?.let { blockChainStatusViewModel.setTx(it) }

                                if (player.activeCharacter == -1) {
                                    val index = player.characters.indexOfFirst {
                                        it.id == tx?.txData?.output?.get(0)?.itemId
                                    }
                                    player.setActiveCharacter(player.characters[index])
                                    model.setPlayer(player)
                                    player.saveAsync(this@GameScreenActivity)
                                }
                            }

                            onNavigation(INVENTORY)
                        }
                    }

                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }
    }

    override fun onEngageFight(fight: Fight, recipeId: String, itemIds: Array<String>) {
        val player = model.getPlayer().value
        if (player != null) {
            val currentCharacterName = player.getActiveCharacter()?.name
            val currentCharacterLevel = player.getActiveCharacter()?.level
            val loading = displayLoading(
                this,
                getString(
                    R.string.loading_fight,
                    fight.name,
                    currentCharacterName,
                    currentCharacterLevel
                )
            )

            CoroutineScope(IO).launch {
                val tx = executeRecipe(recipeId, itemIds)
                syncProfile()

                if (tx?.txError != null) {
                    withContext(Main) {
                        loading.dismiss()
                        displayMessage(
                            this@GameScreenActivity,
                            getString(R.string.execute_recipe_error)
                        )
                    }
                } else {
                    Log.info(tx?.txData.toString())
                    var prompt = ""
                    if (tx != null) {
                        val output = tx.txData.output
                        if (output.isEmpty()) {
                            prompt =
                                getString(
                                    R.string.you_were_killed,
                                    currentCharacterName,
                                    "${getString(getFightIcon(fight.id))} ${fight.name}"
                                )
                            nav_host_fragment.findNavController().navigate(R.id.homeScreenFragment)
                        } else {
                            prompt = getString(
                                R.string.you_did_fight_with_and_earned,
                                "${getString(getFightIcon(fight.id))} ${fight.name}",
                                tx.txData.output[0].amount
                            )

                            when (output.size) {
                                2 -> {
                                    // Rabbit does not use weapon
                                    if (fight.id != ID_RABBIT) {
                                        prompt += "\n ${getString(R.string.you_have_lost_your_weapon)}"
                                        nav_host_fragment.findNavController()
                                            .navigate(R.id.forestScreenFragment)
                                    }
                                }
                                3 -> {
                                    if (fight.id == ID_GIANT || fight.id == ID_FIRE_GIANT || fight.id == ID_ICE_GIANT || fight.id == ID_ACID_GIANT) {
                                        val character = player.getActiveCharacter()
                                        if (character != null && character.special != NO_SPECIAL.toLong()) {
                                            val special = when (character.special) {
                                                1L -> getString(R.string.fire_icon)
                                                2L -> getString(R.string.ice_icon)
                                                3L -> getString(R.string.acid_icon)
                                                else -> ""
                                            }
                                            val dragon = when (character.special) {
                                                1L -> getString(R.string.fire_dragon)
                                                2L -> getString(R.string.ice_dragon)
                                                3L -> getString(R.string.acid_dragon)
                                                else -> ""
                                            }
                                            prompt += "\n${getString(
                                                R.string.fight_giant_special,
                                                special,
                                                dragon
                                            )}"

                                            nav_host_fragment.findNavController()
                                                .navigate(R.id.forestScreenFragment)
                                        }

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
                        loading.dismiss()
                        displayMessage(this@GameScreenActivity, prompt)
                        tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                    }
                }
            }
        }
    }

    override fun onBuyGoldWithPylons() {
        val player = model.getPlayer().value
        if (player != null) {
            if (player.pylonAmount < 100) {
                Toast.makeText(this, getString(R.string.not_enough_pylons), Toast.LENGTH_SHORT)
                    .show()
            } else {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage(
                    getString(
                        R.string.confirm_buy_gold_with_pylons,
                        100,
                        5000
                    )
                )
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                        val loading =
                            displayLoading(
                                this,
                                getString(R.string.loading_buy_gold_with_pylon, 100, 5000)
                            )
                        CoroutineScope(IO).launch {
                            val tx = executeRecipe(RCP_BUY_GOLD_WITH_PYLON, arrayOf())
                            syncProfile()

                            if (tx?.txError != null) {
                                withContext(Main) {
                                    loading.dismiss()
                                    displayMessage(
                                        this@GameScreenActivity,
                                        getString(R.string.execute_recipe_error)
                                    )
                                }
                            } else {
                                withContext(Main) {
                                    loading.dismiss()
                                    displayMessage(
                                        this@GameScreenActivity,
                                        getString(R.string.bought_gold_with_pylons, 5000, 100)
                                    )
                                    tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                                }
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.show()
            }
        }
    }

    override fun onGetDevItems() {
        val loading =
            displayLoading(this, getString(R.string.loading_get_dev_items))
        CoroutineScope(IO).launch {
            val tx = executeRecipe(RCP_GET_TEST_ITEMS, arrayOf())
            syncProfile()

            if (tx?.txError != null) {
                withContext(Main) {
                    loading.dismiss()
                    displayMessage(
                        this@GameScreenActivity,
                        getString(R.string.execute_recipe_error)
                    )
                }
            } else {
                withContext(Main) {
                    loading.dismiss()
                    displayMessage(
                        this@GameScreenActivity,
                        getString(R.string.got_dev_items)
                    )
                    tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                }
                onNavigation(INVENTORY)
            }
        }
    }

    override fun onGetPylons() {
        val loading =
            displayLoading(this, getString(R.string.loading_get_pylons))
        CoroutineScope(IO).launch {
            val tx = Core.engine.getPylons(500)
            tx.submit()
            Log.info(tx.id)
            // TODO("Remove delay, walletcore should handle it")
            delay(5000)

            syncProfile()
            withContext(Main) {
                loading.dismiss()
                displayMessage(
                    this@GameScreenActivity,
                    getString(R.string.got_pylons)
                )
                tx?.id?.let { blockChainStatusViewModel.setTx(it) }
            }
        }
    }

    private fun promptTrade(trade: Trade, itemIds: List<String>) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(
            getString(R.string.trade_fulfill)
        )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                val loading =
                    displayLoading(
                        this,
                        getString(R.string.trade_fulfill_loading)
                    )
                CoroutineScope(IO).launch {
                    val tx = executeTrade(trade, itemIds)
                    syncProfile()
                    if (tx?.txError != null) {
                        withContext(Main) {
                            loading.dismiss()
                            displayMessage(
                                this@GameScreenActivity,
                                getString(R.string.trade_error)
                            )
                        }
                    } else {
                        withContext(Main) {
                            loading.dismiss()
                            displayMessage(
                                this@GameScreenActivity,
                                getString(R.string.trade_fulfill_complete)
                            )
                            tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                        }

                        refreshTrade()
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.show()
    }

    override fun onTrade(trade: Trade) {
        val player = model.getPlayer().value ?: return

        if (!player.canFulfillTrade(trade)) {
            Toast.makeText(this, getString(R.string.trade_cannot_fulfill), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (trade is BuyItemTrade) {
            model.trade = trade
            model.tradeBuyMatchingItems = player.getMatchingTradeItems(trade)
            val frag =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
                .navigate(R.id.itemSelectFragment)
            return
        }

        promptTrade(trade, listOf())
    }

    private suspend fun executeTrade(trade: Trade, itemIds: List<String>): Transaction? {
        val tx = Core.engine.fulfillTrade(trade.id, itemIds)
        tx.submit()
        Log.info(tx.toString())
        Log.info(tx.id)

        // TODO("Remove delay, walletcore should handle it")
        delay(5000)
        val txId = tx.id
        if (txId != null) {
            val tx = Core.engine.getTransaction(txId)
            Log.info(tx.toString())
            return tx
        }

        return null
    }

    private suspend fun createTrade(
        coinInput: List<CoinInput>,
        itemInput: List<TradeItemInput>,
        coinOutput: List<CoinOutput>,
        itemOutput: List<com.pylons.wallet.core.types.tx.item.Item>,
        extraInfo: String
    ): Transaction? {
        val tx =
            Core.engine.createTrade(
                coinInput,
                itemInput,
                coinOutput,
                itemOutput,
                extraInfo
            )
        tx.submit()
        Log.info(tx.toString())

        if (tx.state == Transaction.State.TX_ACCEPTED) {
            Log.info(tx.id)
            // TODO("Remove delay, walletcore should handle it")
            delay(5000)
            val txId = tx.id
            if (txId != null) {
                val tx = Core.engine.getTransaction(txId)
                Log.info(tx.toString())
                return tx
            }
        }

        return null
    }

    override fun onCreateTrade(
        coinInput: List<CoinInput>,
        itemInput: List<TradeItemInput>,
        coinOutput: List<CoinOutput>,
        itemOutput: List<com.pylons.wallet.core.types.tx.item.Item>,
        extraInfo: String
    ) {
        val loading =
            displayLoading(
                this,
                getString(R.string.trade_create_loading)
            )
        CoroutineScope(IO).launch {
            val tx = createTrade(coinInput, itemInput, coinOutput, itemOutput, extraInfo)
            syncProfile()

            withContext(Main) {
                loading.dismiss()

                if (tx != null) {
                    displayMessage(
                        this@GameScreenActivity,
                        getString(R.string.trade_create_complete)
                    )
                    tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                    refreshTrade()
                } else {
                    displayMessage(
                        this@GameScreenActivity,
                        getString(R.string.trade_create_error)
                    )
                }
            }
        }
    }

    private fun refreshTrade() {
        val frag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
            .popBackStack()
        frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
            .navigate(R.id.pylonCentralTradeFragment)
    }

    override fun onItemTradeBuy(item: ItemSpec) {
        model.setTradeInput(item)
    }

    override fun onItemTradeSell(item: Item) {
        onTradeSell(item.id)
    }

    override fun onCharacterTradeSell(character: Character) {
        onTradeSell(character.id)
    }

    private fun onTradeSell(id: String) {
        CoroutineScope(IO).launch {
            val coreItem = getItemById(id)
            if (coreItem != null) {
                withContext(Main) {
                    model.setTradeOutput(
                        coreItem
                    )
                }
            } else {
                // TODO("handle error")
            }
        }
    }

    override fun onCancel(trade: Trade) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(
            getString(R.string.trade_cancel)
        )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                val loading =
                    displayLoading(
                        this,
                        getString(R.string.trade_cancel_loading)
                    )
                CoroutineScope(IO).launch {
                    val tx = cancelTrade(trade)
                    syncProfile()

                    withContext(Main) {
                        loading.dismiss()
                        displayMessage(
                            this@GameScreenActivity,
                            getString(R.string.trade_cancel_complete)
                        )
                        tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                    }

                    refreshTrade()
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.show()
    }


    private suspend fun cancelTrade(trade: Trade): Transaction? {
        val tx = Core.engine.cancelTrade(trade.id)
        tx.submit()
        Log.info(tx.toString())
        Log.info(tx.id)

        // TODO("Remove delay, walletcore should handle it")
        delay(5000)
        val txId = tx.id
        if (txId != null) {
            val tx = Core.engine.getTransaction(txId)
            Log.info(tx.toString())
            return tx
        }

        return null
    }

    override fun onCharacterUpdate(character: Character) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_text, null)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(
            getString(R.string.update_character_prompt)
        )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.setView(mDialogView)
        alert.show()

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = mDialogView.edit_text.text.toString()
            if (name.isNotBlank()) {
                onRenameCharacter(character, name)
                alert.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun onRenameCharacter(character: Character, name: String) {
        val loading =
            displayLoading(
                this,
                getString(R.string.update_character_loading, character.name, name)
            )
        CoroutineScope(IO).launch {
            val tx = character.rename(name)
            Log.info(tx.toString())
            syncProfile()

            withContext(Main) {
                loading.dismiss()
                displayMessage(
                    this@GameScreenActivity,
                    getString(R.string.update_character_complete, name)
                )
                tx?.id?.let { blockChainStatusViewModel.setTx(it) }
            }
        }

    }

    override fun onItemTradeBuy(item: Item) {
        promptTrade(model.trade, listOf(item.id))
    }

    override fun onFriend(friend: Friend) {
        val sheet = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_friend, null)
        view.text_view_delete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(
                getString(
                    R.string.delete_friend_prompt,
                    friend.name,
                    friend.address
                )
            )
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    val player = model.getPlayer().value
                    if (player != null) {
                        player.deleteFriend(friend)
                        model.setPlayer(player)
                        player.saveAsync(this)
                        sheet.dismiss()
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                    sheet.dismiss()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }

        sheet.setContentView(view)
        sheet.show()
    }

    override fun onSendItem(friend: Friend) {
        sendItemViewModel.friend = friend
        findNavController(R.id.nav_host_fragment_send_item).navigate(R.id.sendItemListFragment)
    }

    override fun onItemSend(item: Item) {
        sendItemViewModel.itemIds = listOf(item)
        findNavController(R.id.nav_host_fragment_send_item).navigate(R.id.sendItemConfirmFragment)
    }

    override fun onSendItems(friendAddress: String, itemIds: List<Item>) {
        val loading = displayLoading(this, getString(R.string.send_items_loading))
        CoroutineScope(IO).launch {
            val tx = sendItems(friendAddress, itemIds)
            syncProfile()

            withContext(Main) {
                loading.dismiss()

                if (tx != null) {
                    displayMessage(
                        this@GameScreenActivity,
                        getString(R.string.send_items_complete)
                    )
                    tx?.id?.let { blockChainStatusViewModel.setTx(it) }
                    onNavigation(PYLONS_CENTRAL)
                } else {
                    displayMessage(
                        this@GameScreenActivity,
                        getString(R.string.send_items_error)
                    )
                }
            }
        }
    }

    private suspend fun sendItems(friendAddress: String, itemIds: List<Item>): Transaction? {
        val player = model.getPlayer().value
        if (player != null) {
            val tx = Core.engine.sendItems(player.address, friendAddress, itemIds.map { it.id })
            tx.submit()
            Log.info(tx.toString())
            if (tx.state == Transaction.State.TX_ACCEPTED) {
                Log.info(tx.id)
                // TODO("Remove delay, walletcore should handle it")
                delay(5000)
                val txId = tx.id
                if (txId != null) {
                    val tx = Core.engine.getTransaction(txId)
                    Log.info(tx.toString())
                    return tx
                }
            }
        }

        return null
    }
}
