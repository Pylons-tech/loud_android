package com.pylons.loud.fragments.ForestScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.constants.FightRequirements.IRON_SWORD
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
        val frag = childFragmentManager.findFragmentById(R.id.fragment_fight)
        val v = frag?.view as RecyclerView
        val c = context
        if (c is FightFragment.OnListFragmentInteractionListener) {
            v.adapter = MyFightRecyclerViewAdapter(
                listOf(
                    Fight(
                        1,
                        getString(R.string.rabbit),
                        0,
                        0,
                        "${getString(R.string.gold_icon)} 1-2",
                        listOf(SWORD),
                        listOf("5% chance of character lose", "5% chance of sword lose")
                    ),
                    Fight(
                        2,
                        getString(R.string.goblin),
                        10,
                        1,
                        "${getString(R.string.gold_icon)} 50",
                        listOf(SWORD),
                        listOf("10% chance of sword lose", "10% chance of getting Goblin ear")
                    ),
                    Fight(
                        3,
                        getString(R.string.wolf),
                        15,
                        3,
                        "${getString(R.string.gold_icon)} 150",
                        listOf(SWORD),
                        listOf("10% chance of sword lose", "10% chance of getting Wolf tail")
                    ),
                    Fight(
                        4,
                        getString(R.string.troll),
                        20,
                        5,
                        "${getString(R.string.gold_icon)} 300",
                        listOf(SWORD),
                        listOf("10% chance of sword lose", "10% chance of getting Troll toes")
                    ),
                    Fight(
                        5,
                        getString(R.string.giant),
                        100,
                        10,
                        "${getString(R.string.gold_icon)} 3000",
                        listOf(IRON_SWORD),
                        listOf("10% chance of sword lose", "GiantKiller badget on character")
                    )
                ), c
            )
        }
    }

}
