package com.pylons.loud.activities

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

import kotlinx.android.synthetic.main.content_game_screen.*
import java.util.logging.Logger

class GameScreenActivity : AppCompatActivity(),
    PlayerLocationFragment.OnListFragmentInteractionListener,
    PlayerActionFragment.OnListFragmentInteractionListener,
    ItemFragment.OnListFragmentInteractionListener,
    CharacterFragment.OnListFragmentInteractionListener {
    private val Log = Logger.getLogger(GameScreenActivity::class.java.name)

    private val activeCharacter = Character("1", "Tiger", 1, 1, 1.0, 100, 100, 0, 0)
    private val activeWeapon = Weapon("1", "Wooden Sword", 1, 3, 1, "no", 0)
    private val player = User(
        "cluo",
        5000,
        50000,
        mutableListOf(activeCharacter, Character("2", "Lion", 2, 1, 1.0, 100, 100, 0, 0)),
        activeCharacter,
        mutableListOf(activeWeapon, Weapon("2", "Steel Sword", 1, 6, 1, "no", 0)),
        activeWeapon
    )

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

        val model: SharedViewModel by viewModels()
        model.setPlayer(player)
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
                }
                LocationConstants.FOREST -> {
                    if (player.activeCharacter == null) {
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
                else -> {
                    Log.warning("Not exist")
                }
            }
        }
    }

    override fun onItemSelect(item: Item?) {
        val name = item?.name

        var prompt = "Set ${name} as active weapon?"
        if (player.activeWeapon == item) {
            prompt = "Unset ${name} as active weapon?"
        }
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage(prompt)
            .setCancelable(false)
            .setPositiveButton("Proceed") { _, _ ->
                if (player.activeWeapon == item) {
                    player.activeWeapon = null
                } else {
                    player.activeWeapon = item as Weapon
                }
                model.setPlayer(player)
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
                player.inventory.add(item as Weapon)
                player.gold = player.gold - price!!
                player.activeWeapon = item
                model.setPlayer(player)
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
                player.inventory.remove(item as Weapon)
                if (player.activeWeapon == item) {
                    player.activeWeapon = null
                }
                model.setPlayer(player)
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
        if (player.activeCharacter == item) {
            prompt = "Unset ${name} as active character?"
        }
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage(prompt)
            .setCancelable(false)
            .setPositiveButton("Proceed") { _, _ ->
                if (player.activeCharacter == item) {
                    player.activeCharacter = null
                } else {
                    player.activeCharacter = item
                }
                model.setPlayer(player)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Confirm")
        alert.show()
    }
}
