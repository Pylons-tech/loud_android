package com.pylons.loud.fragments.ForestScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.FightId.ID_ACID_DRAGON
import com.pylons.loud.constants.FightId.ID_FIRE_DRAGON
import com.pylons.loud.constants.FightId.ID_GIANT
import com.pylons.loud.constants.FightId.ID_GOBLIN
import com.pylons.loud.constants.FightId.ID_ICE_DRAGON
import com.pylons.loud.constants.FightId.ID_RABBIT
import com.pylons.loud.constants.FightId.ID_TROLL
import com.pylons.loud.constants.FightId.ID_UNDEAD_DRAGON
import com.pylons.loud.constants.FightId.ID_WOLF
import com.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGONACID
import com.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGONFIRE
import com.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGONICE
import com.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGONUNDEAD
import com.pylons.loud.constants.Recipe.RCP_FIGHT_GIANT
import com.pylons.loud.constants.Recipe.RCP_FIGHT_GOBLIN
import com.pylons.loud.constants.Recipe.RCP_FIGHT_TROLL
import com.pylons.loud.constants.Recipe.RCP_FIGHT_WOLF
import com.pylons.loud.constants.Recipe.RCP_HUNT_RABBITS
import com.pylons.loud.models.Fight
import kotlinx.android.synthetic.main.fragment_forest_fight_preview.*
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class ForestFightPreviewFragment : Fragment() {
    private val Log = Logger.getLogger(ForestFightPreviewFragment::class.java.name)
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forest_fight_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.getFightPreview().observe(viewLifecycleOwner, Observer<Fight> { fight ->

            val activeWeapon = model.getPlayer().value?.getActiveWeapon()

            text_header.text = fight.name

            val textView2 = TextView(context)
            textView2.text = "Reward ${fight.reward}"
            layout_fight_preview.addView(textView2)

            if (fight.id != 1) {
                val textView3 = TextView(context)
                textView3.text = "Enemy info (HP: ${fight.hp}, Attack: ${fight.attack})"
                layout_fight_preview.addView(textView3)
            }

            fight.conditions.forEach {
                val textView = TextView(context)
                textView.text = it
                layout_fight_preview.addView(textView)
            }

            if (activeWeapon != null) {
                val textView5 = TextView(context)
                textView5.text =
                    "Carry: ${activeWeapon.name} Lv${activeWeapon.level} attack=${activeWeapon.attack}"
                layout_fight_preview.addView(textView5)
            }

            button_fight.setOnClickListener {
                val recipeId = when (fight.id) {
                    ID_RABBIT -> RCP_HUNT_RABBITS
                    ID_GOBLIN -> RCP_FIGHT_GOBLIN
                    ID_WOLF -> RCP_FIGHT_WOLF
                    ID_TROLL -> RCP_FIGHT_TROLL
                    ID_GIANT -> RCP_FIGHT_GIANT
                    ID_FIRE_DRAGON -> RCP_FIGHT_DRAGONFIRE
                    ID_ICE_DRAGON -> RCP_FIGHT_DRAGONICE
                    ID_ACID_DRAGON -> RCP_FIGHT_DRAGONACID
                    ID_UNDEAD_DRAGON -> RCP_FIGHT_DRAGONUNDEAD
                    else -> ""
                }

                val player = model.getPlayer().value

                if (player != null && recipeId != "") {
                    val itemIds = mutableListOf<String>()
                    val activeCharacter = player.getActiveCharacter()
                    if (activeCharacter != null) {
                        itemIds.add(activeCharacter.id)
                    }

                    val activeWeapon = player.getActiveWeapon()
                    if (activeWeapon != null) {
                        // Rabbit does not use weapon
                        if (fight.id != ID_RABBIT) {
                            itemIds.add(activeWeapon.id)
                        }
                    }
                    listener?.onEngageFight(fight, recipeId, itemIds.toTypedArray())
                }
            }
        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    interface OnFragmentInteractionListener {
        fun onEngageFight(fight: Fight, recipeId: String, itemIds: Array<String>)
    }
}
