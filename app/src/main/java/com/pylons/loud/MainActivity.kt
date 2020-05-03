package com.pylons.loud

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pylons.loud.constants.LocationConstants.FOREST
import com.pylons.loud.constants.LocationConstants.HOME
import com.pylons.loud.constants.LocationConstants.PYLONS_CENTRAL
import com.pylons.loud.constants.LocationConstants.SHOP
import com.pylons.loud.fragments.PlayerAction.PlayerActionFragment
import com.pylons.loud.fragments.PlayerLocation.PlayerLocationFragment
import com.pylons.loud.models.Character
import com.pylons.loud.models.PlayerAction
import com.pylons.loud.models.PlayerLocation
import com.pylons.loud.models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_game_screen.*
import kotlinx.android.synthetic.main.fragment_player_status.*
import kotlinx.android.synthetic.main.fragment_player_status.view.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity(), PlayerActionFragment.OnListFragmentInteractionListener, PlayerLocationFragment.OnListFragmentInteractionListener {
    private val Log = Logger.getLogger(MainActivity::class.java.name)

    /**
     * An array of sample (dummy) items.
     */
//    val ITEMS: MutableList<PlayerAction> = ArrayList()

    private var LOCATIONS = listOf<PlayerLocation>()
    private val activeCharacter = Character("1", "Tiger", 1, 1, 1.0, 100, 100, 0, 0)
    private val player = User("cluo", 5000, 50000, listOf(activeCharacter), activeCharacter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        fragment_player_action.list_actions.adapter =
//            MyPlayerActionRecyclerViewAdapter(ITEMS, this)

        LOCATIONS = listOf(
            PlayerLocation(
                HOME, getString(R.string.home), listOf(
                    PlayerAction(1, getString(R.string.select_active_character)),
                    PlayerAction(2, getString(R.string.select_active_weapon)),
                    PlayerAction(3, getString(R.string.restore_character_health))
                )
            ),
            PlayerLocation(
                FOREST, getString(R.string.forest), listOf(
                    PlayerAction(1, getString(R.string.rabbit)),
                    PlayerAction(2, getString(R.string.goblin)),
                    PlayerAction(3, getString(R.string.wolf)),
                    PlayerAction(4, getString(R.string.troll)),
                    PlayerAction(5, getString(R.string.giant))
                )
            ),
            PlayerLocation(
                SHOP, getString(R.string.shop), listOf(
                    PlayerAction(1, getString(R.string.buy_items)),
                    PlayerAction(2, getString(R.string.sell_items)),
                    PlayerAction(3, getString(R.string.upgrade_items))
                )
            ),
            PlayerLocation(
                PYLONS_CENTRAL, getString(R.string.pylons_central), listOf(
                    PlayerAction(1, getString(R.string.buy_characters)),
                    PlayerAction(2, getString(R.string.buy_5000_with_100_pylons)),
                    PlayerAction(3, getString(R.string.sell_gold_from_orderbook_place_order_to_buy)),
                    PlayerAction(4, getString(R.string.buy_gold_from_orderbook_place_order_to_sell)),
                    PlayerAction(5, getString(R.string.sell_weapon_from_orderbook_place_order_to_buy)),
                    PlayerAction(6, getString(R.string.buy_weapon_from_orderbook_place_order_to_sell)),
                    PlayerAction(7, getString(R.string.sell_character_from_orderbook_place_order_to_buy)),
                    PlayerAction(8, getString(R.string.buy_character_from_orderbook_place_order_to_sell)),
                    PlayerAction(9, getString(R.string.update_character_name))
                )
            )
        )

        onLocation(LOCATIONS[0])

        val frag2 =
            supportFragmentManager.findFragmentById(R.id.fragment_player_location) as PlayerLocationFragment
        frag2.setAdapter(LOCATIONS)

        fragment_player_status.text_player_name.text = player.name
        fragment_player_status.text_player_gold.text = player.gold.toString()
        fragment_player_status.text_player_pylon.text = player.pylonAmount.toString()
        fragment_player_status.layout_pylon_count.setOnClickListener {
            onLocation(LOCATIONS[3])
        }
        fragment_player_status.layout_gold_count.setOnClickListener {
            onLocation(LOCATIONS[2])
        }

        if (player.activeCharacter != null) {
            fragment_player_status.layout_active_character.text_active_character_name.text = player.activeCharacter!!.name
            fragment_player_status.layout_active_character.text_active_character_level.text = player.activeCharacter!!.level.toString()
            fragment_player_status.layout_active_character.text_active_character_xp.text = player.activeCharacter!!.xp.toString()
            fragment_player_status.layout_active_character.text_active_character_hp.text = player.activeCharacter!!.hp.toString() + "/" + player.activeCharacter!!.maxHP.toString()

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
        val frag =
            supportFragmentManager.findFragmentById(R.id.fragment_player_action) as PlayerActionFragment

        if (location != null) {
            when (location.id) {
                HOME -> {
                    if (player.activeCharacter == null) {
                        fragment_game_screen.text_screen.setText(R.string.home_desc_without_character)
                    } else {
                        fragment_game_screen.text_screen.setText(R.string.home_desc)
                    }
                    frag.setAdapter(location.actions)
                }
                FOREST -> {
                    if (player.activeCharacter == null) {
                        Toast.makeText(this, R.string.you_cant_go_to_forest_without_character, Toast.LENGTH_SHORT).show()
                        return
                    }
                    fragment_game_screen.text_screen.setText(R.string.forest_desc)
                    frag.setAdapter(location.actions)
                }
                SHOP -> {
                    fragment_game_screen.text_screen.setText(R.string.shop_desc)
                    frag.setAdapter(location.actions)
                }
                PYLONS_CENTRAL -> {
                    fragment_game_screen.text_screen.setText(R.string.pylons_central_desc)
                    frag.setAdapter(location.actions)
                }
                else -> {
                    Log.warning("Not exist")
                }
            }
        }
    }
}
