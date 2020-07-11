package com.pylons.loud.fragments.screens.forest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pylons.loud.R
import com.pylons.loud.constants.FightId
import com.pylons.loud.constants.FightRequirements
import com.pylons.loud.fragments.lists.fight.FightFragment
import com.pylons.loud.fragments.lists.fight.MyFightRecyclerViewAdapter
import com.pylons.loud.models.fight.FightPremium

/**
 * A simple [Fragment] subclass.
 */
class ForestSpecialFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forest_special, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frag = childFragmentManager.findFragmentById(R.id.fragment_fight)
        val v = frag?.view as RecyclerView
        val c = context
        if (c is FightFragment.OnListFragmentInteractionListener) {
            val fightList = listOf(
                FightPremium(
                    FightId.ID_FIRE_GIANT,
                    getString(R.string.fire_giant),
                    100,
                    10,
                    "${getString(R.string.gold_icon)} 3000",
                    listOf(FightRequirements.IRON_SWORD, FightRequirements.NO_SPECIAL),
                    listOf(
                        "5% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of bonus skill",
                        "10% chance of fire special",
                        "\uD83D\uDDFF (GiantKiller) badge on character"
                    ),
                    5
                ),
                FightPremium(
                    FightId.ID_ICE_GIANT,
                    getString(R.string.ice_giant),
                    100,
                    10,
                    "${getString(R.string.gold_icon)} 3000",
                    listOf(FightRequirements.IRON_SWORD, FightRequirements.NO_SPECIAL),
                    listOf(
                        "5% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of bonus skill",
                        "10% chance of ice special",
                        "\uD83D\uDDFF (GiantKiller) badge on character"
                    ),
                    5
                ),
                FightPremium(
                    FightId.ID_ACID_GIANT,
                    getString(R.string.acid_giant),
                    100,
                    10,
                    "${getString(R.string.gold_icon)} 3000",
                    listOf(FightRequirements.IRON_SWORD, FightRequirements.NO_SPECIAL),
                    listOf(
                        "5% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of bonus skill",
                        "10% chance of acid special",
                        "\uD83D\uDDFF (GiantKiller) badge on character"
                    ),
                    5
                )
            )

            v.adapter = MyFightRecyclerViewAdapter(
                fightList, c
            )
        }
    }
}