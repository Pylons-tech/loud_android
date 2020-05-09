package com.pylons.loud.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.pylons.loud.R
import com.pylons.loud.constants.LocationConstants
import com.pylons.loud.fragments.Character.CharacterFragment
import com.pylons.loud.fragments.Fight.FightFragment
import com.pylons.loud.fragments.Item.ItemFragment
import com.pylons.loud.fragments.PlayerLocation.PlayerLocationFragment
import com.pylons.loud.models.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

import kotlinx.android.synthetic.main.content_game_screen.*
import java.util.logging.Logger

class GameScreenActivity : AppCompatActivity(),
    PlayerLocationFragment.OnListFragmentInteractionListener,
    FightFragment.OnListFragmentInteractionListener,
    ItemFragment.OnListFragmentInteractionListener,
    CharacterFragment.OnListFragmentInteractionListener {
    private val Log = Logger.getLogger(GameScreenActivity::class.java.name)

    private lateinit var player: User

    class SharedViewModel : ViewModel() {
        private val player = MutableLiveData<User>()
        private val playerLocation = MutableLiveData<Int>()
        private val fightPreview = MutableLiveData<Fight>()

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
            player = currentPlayer
            model.setPlayer(currentPlayer)
        }
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
                    if (player.activeCharacter == -1) {
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

        var prompt = "Set ${name} as active weapon?"
        if (player.getActiveWeapon() == item) {
            prompt = "Unset ${name} as active weapon?"
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

    override fun onItemBuy(item: Item?) {
        val name = item?.name
        val price = item?.price
        val goldIcon = getString(R.string.gold_icon)

        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage("Buy $name for $goldIcon $price?")
            .setCancelable(false)
            .setPositiveButton("Buy") { _, _ ->
                player.weapons.add(item as Weapon)
                player.gold = player.gold - price!!
                player.setActiveWeapon(item)
                model.setPlayer(player)
                player.saveAsync(this)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Confirm")
        alert.show()
    }

    override fun onItemSell(item: Item?) {
        val name = item?.name

        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage("Sell $name?")
            .setCancelable(false)
            .setPositiveButton("Sell") { _, _ ->
                player.weapons.remove(item as Weapon)
                if (player.getActiveWeapon() == item) {
                    player.activeWeapon = -1
                }
                model.setPlayer(player)
                player.saveAsync(this)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Confirm")
        alert.show()
    }

    override fun onItemUpgrade(item: Item?) {
        val name = item?.name

        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage("Upgrade $name?")
            .setCancelable(false)
            .setPositiveButton("Upgrade") { _, _ ->
                TODO()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Confirm")
        alert.show()
    }

    override fun onCharacter(item: Character?) {
        val name = item?.name

        var prompt = "Set ${name} as active character?"
        if (player.getActiveCharacter() == item) {
            prompt = "Unset ${name} as active character?"
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

    override fun onBuyCharacter(item: Character?) {
        val name = item?.name
        val price = item?.price
        val pylonIcon = getString(R.string.pylon_icon)

        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage("Buy $name for $pylonIcon $price?")
            .setCancelable(false)
            .setPositiveButton("Proceed") { _, _ ->
                if (item != null) {
                    player.characters.add(item)
                }
                player.pylonAmount = player.pylonAmount - price!!
                player.setActiveCharacter(item)
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
