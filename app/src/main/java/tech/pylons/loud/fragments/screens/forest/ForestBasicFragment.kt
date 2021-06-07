package tech.pylons.loud.fragments.screens.forest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.pylons.loud.R
import tech.pylons.loud.constants.FightId
import tech.pylons.loud.constants.FightRequirements
import tech.pylons.loud.fragments.lists.fight.FightFragment
import tech.pylons.loud.fragments.lists.fight.MyFightRecyclerViewAdapter
import tech.pylons.loud.models.fight.Fight

/**
 * A simple [Fragment] subclass.
 */
class ForestBasicFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forest_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frag = childFragmentManager.findFragmentById(R.id.fragment_fight)
        val v = frag?.view as RecyclerView
        val c = context
        if (c is FightFragment.OnListFragmentInteractionListener) {
            val fightList = listOf<Fight>(
                Fight(
                    FightId.ID_RABBIT,
                    getString(R.string.rabbit),
                    0,
                    0,
                    "${getString(R.string.gold_icon)} 1-2",
                    listOf(),
                    listOf("1% chance of character dying")
                ),
                Fight(
                    FightId.ID_GOBLIN,
                    getString(R.string.goblin),
                    10,
                    1,
                    "${getString(R.string.gold_icon)} 50",
                    listOf(FightRequirements.SWORD),
                    listOf(
                        "2% chance of character dying",
                        "3% chance of sword lose",
                        "20% chance of getting Goblin ear",
                        "20% chance of getting Goblin boots"
                    )
                ),
                Fight(
                    FightId.ID_WOLF,
                    getString(R.string.wolf),
                    15,
                    3,
                    "${getString(R.string.gold_icon)} 1",
                    listOf(FightRequirements.SWORD),
                    listOf(
                        "3% chance of character dying",
                        "3% chance of sword lose",
                        "40% chance of getting Wolf tail",
                        "30% chance of Wolf fur"
                    )
                ),
                Fight(
                    FightId.ID_TROLL,
                    getString(R.string.troll),
                    20,
                    5,
                    "${getString(R.string.gold_icon)} 300",
                    listOf(FightRequirements.SWORD),
                    listOf(
                        "4% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of getting Troll toes",
                        "30% chance of getting Troll smelly bones"
                    )
                ),
                Fight(
                    FightId.ID_GIANT,
                    getString(R.string.giant),
                    100,
                    10,
                    "${getString(R.string.gold_icon)} 3000",
                    listOf(FightRequirements.IRON_SWORD, FightRequirements.NO_SPECIAL),
                    listOf(
                        "5% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of bonus skill",
                        "Hmm.. 4%, 3%, 3% for fire, ice, acid",
                        "\uD83D\uDDFF (GiantKiller) badge on character"
                    )
                ),
                Fight(
                    FightId.ID_FIRE_DRAGON,
                    getString(R.string.fire_dragon),
                    300,
                    30,
                    "${getString(R.string.gold_icon)} 10000",
                    listOf(FightRequirements.IRON_SWORD, FightRequirements.FIRE_SPECIAL),
                    listOf(
                        "4% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of getting fire scale",
                        "FireDragonKiller badge on character"
                    )
                ),
                Fight(
                    FightId.ID_ICE_DRAGON,
                    getString(R.string.ice_dragon),
                    300,
                    30,
                    "${getString(R.string.gold_icon)} 10000",
                    listOf(FightRequirements.IRON_SWORD, FightRequirements.ICE_SPECIAL),
                    listOf(
                        "4% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of getting icy shards",
                        "IceDragonKiller badge on character"
                    )
                ),
                Fight(
                    FightId.ID_ACID_DRAGON,
                    getString(R.string.acid_dragon),
                    300,
                    30,
                    "${getString(R.string.gold_icon)} 10000",
                    listOf(FightRequirements.IRON_SWORD, FightRequirements.ACID_SPECIAL),
                    listOf(
                        "4% chance of character dying",
                        "3% chance of sword lose",
                        "10% chance of getting poison claws",
                        "AcidDragonKiller badge on character"
                    )
                ),
                Fight(
                    FightId.ID_UNDEAD_DRAGON,
                    getString(R.string.undead_dragon),
                    300,
                    30,
                    "${getString(R.string.gold_icon)} 50000",
                    listOf(FightRequirements.ANGEL_SWORD),
                    listOf(
                        "7% chance of character dying",
                        "3% chance of sword lose",
                        "UndeadDragonKiller badge on character"
                    )
                )
            )

            v.adapter = MyFightRecyclerViewAdapter(
                fightList, c
            )
        }
    }

}