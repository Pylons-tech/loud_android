package com.pylons.loud.fragments.ForestScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.FightId.ID_ACID_DRAGON
import com.pylons.loud.constants.FightId.ID_FIRE_DRAGON
import com.pylons.loud.constants.FightId.ID_GIANT
import com.pylons.loud.constants.FightId.ID_GOBLIN
import com.pylons.loud.constants.FightId.ID_ICE_DRAGON
import com.pylons.loud.constants.FightId.ID_RABBIT_NO_SWORD
import com.pylons.loud.constants.FightId.ID_RABBIT_SWORD
import com.pylons.loud.constants.FightId.ID_TROLL
import com.pylons.loud.constants.FightId.ID_UNDEAD_DRAGON
import com.pylons.loud.constants.FightId.ID_WOLF
import com.pylons.loud.constants.FightRequirements.ACID_SPECIAL
import com.pylons.loud.constants.FightRequirements.ANGEL_SWORD
import com.pylons.loud.constants.FightRequirements.FIRE_SPECIAL
import com.pylons.loud.constants.FightRequirements.ICE_SPECIAL
import com.pylons.loud.constants.FightRequirements.IRON_SWORD
import com.pylons.loud.constants.FightRequirements.NO_SPECIAL
import com.pylons.loud.constants.FightRequirements.SWORD
import com.pylons.loud.fragments.Fight.FightFragment
import com.pylons.loud.fragments.Fight.MyFightRecyclerViewAdapter
import com.pylons.loud.models.Fight
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class ForestHomeFragment : Fragment() {
    private val Log = Logger.getLogger(ForestHomeFragment::class.java.name)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forest_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GameScreenActivity.SharedViewModel by activityViewModels()

        val frag = childFragmentManager.findFragmentById(R.id.fragment_fight)
        val v = frag?.view as RecyclerView
        val c = context
        if (c is FightFragment.OnListFragmentInteractionListener) {
            val fightList = mutableListOf<Fight>()

            val player = model.getPlayer().value
            if (player != null) {
                if (player.getActiveWeapon() == null) {
                    fightList.add(
                        Fight(
                            ID_RABBIT_NO_SWORD,
                            getString(R.string.rabbit),
                            0,
                            0,
                            "${getString(R.string.gold_icon)} 1-2",
                            listOf(),
                            listOf("5% chance of character dying")
                        )
                    )
                } else {
                    fightList.add(
                        Fight(
                            ID_RABBIT_SWORD,
                            getString(R.string.rabbit),
                            0,
                            0,
                            "${getString(R.string.gold_icon)} 1-2",
                            listOf(SWORD),
                            listOf("5% chance of character dying", "5% chance of sword lose")
                        )
                    )
                }
            }

            fightList.addAll(
                listOf(
                    Fight(
                        ID_GOBLIN,
                        getString(R.string.goblin),
                        10,
                        1,
                        "${getString(R.string.gold_icon)} 50",
                        listOf(SWORD),
                        listOf("10% chance of sword lose", "10% chance of getting Goblin ear")
                    ),
                    Fight(
                        ID_WOLF,
                        getString(R.string.wolf),
                        15,
                        3,
                        "${getString(R.string.gold_icon)} 150",
                        listOf(SWORD),
                        listOf("10% chance of sword lose", "10% chance of getting Wolf tail")
                    ),
                    Fight(
                        ID_TROLL,
                        getString(R.string.troll),
                        20,
                        5,
                        "${getString(R.string.gold_icon)} 300",
                        listOf(SWORD),
                        listOf("10% chance of sword lose", "10% chance of getting Troll toes")
                    ),
                    Fight(
                        ID_GIANT,
                        getString(R.string.giant),
                        100,
                        10,
                        "${getString(R.string.gold_icon)} 3000",
                        listOf(IRON_SWORD, NO_SPECIAL),
                        listOf("10% chance of sword lose", "GiantKiller badget on character")
                    ),
                    Fight(
                        ID_FIRE_DRAGON,
                        getString(R.string.fire_dragon),
                        100,
                        10,
                        "${getString(R.string.gold_icon)} 3000",
                        listOf(IRON_SWORD, FIRE_SPECIAL),
                        listOf("10% chance of sword lose", "GiantKiller badget on character")
                    ),
                    Fight(
                        ID_ICE_DRAGON,
                        getString(R.string.ice_dragon),
                        100,
                        10,
                        "${getString(R.string.gold_icon)} 20000",
                        listOf(IRON_SWORD, ICE_SPECIAL),
                        listOf("10% chance of sword lose", "GiantKiller badget on character")
                    ),
                    Fight(
                        ID_ACID_DRAGON,
                        getString(R.string.acid_dragon),
                        100,
                        10,
                        "${getString(R.string.gold_icon)} 20000",
                        listOf(IRON_SWORD, ACID_SPECIAL),
                        listOf("10% chance of sword lose", "GiantKiller badget on character")
                    ),
                    Fight(
                        ID_UNDEAD_DRAGON,
                        getString(R.string.undead_dragon),
                        100,
                        10,
                        "${getString(R.string.gold_icon)} 50000",
                        listOf(ANGEL_SWORD),
                        listOf("10% chance of sword lose", "GiantKiller badget on character")
                    )
                )
            )

            v.adapter = MyFightRecyclerViewAdapter(
                fightList, c
            )
        }
    }

}
