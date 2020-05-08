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
import androidx.navigation.fragment.findNavController
import com.pylons.loud.R
import com.pylons.loud.constants.LocationConstants
import com.pylons.loud.fragments.Character.CharacterFragment
import com.pylons.loud.fragments.Item.ItemFragment
import com.pylons.loud.fragments.PlayerAction.PlayerActionFragment
import com.pylons.loud.fragments.PlayerLocation.PlayerLocationFragment
import com.pylons.loud.models.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

import kotlinx.android.synthetic.main.content_game_screen.*
import java.util.logging.Logger

class GameScreenActivity : AppCompatActivity(),
    PlayerLocationFragment.OnListFragmentInteractionListener,
    PlayerActionFragment.OnListFragmentInteractionListener,
    ItemFragment.OnListFragmentInteractionListener,
    CharacterFragment.OnListFragmentInteractionListener {
    private val Log = Logger.getLogger(GameScreenActivity::class.java.name)

    private lateinit var player: User

    class SharedViewModel : ViewModel() {
        private val player = MutableLiveData<User>()
        private val playerLocation = MutableLiveData<Int>()

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
            model.setPlayerLocation(0);
        }
    }

    override fun onAction(action: PlayerAction?) {
        if (action != null) {
            when (action.id) {
                1 -> Log.info("1")
                2 -> Log.info("2")
                else -> {
                    Log.warning("3")
                }
            }
        }
    }

    override fun onLocation(location: PlayerLocation?) {
        if (location != null) {
            when (location.id) {
                LocationConstants.HOME -> {
                    nav_host_fragment.findNavController().navigate(R.id.homeScreenFragment)
                    model.setPlayerLocation(0);
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
                    model.setPlayerLocation(1);
                }
                LocationConstants.SHOP -> {
                    nav_host_fragment.findNavController().navigate(R.id.shopScreenFragment)
                    model.setPlayerLocation(2);
                }
                LocationConstants.PYLONS_CENTRAL -> {
                    nav_host_fragment.findNavController().navigate(R.id.pylonCentralFragment)
                    model.setPlayerLocation(3);
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
