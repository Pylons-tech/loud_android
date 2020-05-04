package com.pylons.loud.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.pylons.loud.R
import com.pylons.loud.constants.LocationConstants
import com.pylons.loud.fragments.Item.ItemFragment
import com.pylons.loud.fragments.PlayerAction.PlayerActionFragment
import com.pylons.loud.fragments.PlayerLocation.PlayerLocationFragment
import com.pylons.loud.models.*

import kotlinx.android.synthetic.main.content_game_screen.*
import java.util.logging.Logger

class GameScreenActivity : AppCompatActivity(),
    PlayerLocationFragment.OnListFragmentInteractionListener,
    PlayerActionFragment.OnListFragmentInteractionListener,
    ItemFragment.OnListFragmentInteractionListener {
    private val Log = Logger.getLogger(GameScreenActivity::class.java.name)

    private val activeCharacter = Character("1", "Tiger", 1, 1, 1.0, 100, 100, 0, 0)
    private val activeWeapon = Weapon("1", "Wooden Sword", 1, 3, 1, "no", 0)
    private val player = User(
        "cluo",
        5000,
        50000,
        listOf(activeCharacter),
        activeCharacter,
        listOf(activeWeapon),
        activeWeapon
    )

    class SharedViewModel : ViewModel() {
        private val player = MutableLiveData<User>()
        private val actions = MutableLiveData<List<PlayerAction>>()

        fun getPlayer(): LiveData<User> {
            return player
        }

        fun getActions(): LiveData<List<PlayerAction>> {
            return actions
        }

        fun setPlayer(user: User) {
            player.value = user
        }

        fun setActions(playerActions: List<PlayerAction>) {
            actions.value = playerActions
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
                    model.setActions(location.actions)
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
                    model.setActions(location.actions)
                }
                LocationConstants.SHOP -> {
                    nav_host_fragment.findNavController().navigate(R.id.shopScreenFragment)
                    model.setActions(location.actions)
                }
                LocationConstants.PYLONS_CENTRAL -> {
                    nav_host_fragment.findNavController().navigate(R.id.pylonCentralFragment)
                    model.setActions(location.actions)
                }
                else -> {
                    Log.warning("Not exist")
                }
            }
        }
    }

    override fun onItem(item: Item?) {
        TODO("Not yet implemented")
    }
}
