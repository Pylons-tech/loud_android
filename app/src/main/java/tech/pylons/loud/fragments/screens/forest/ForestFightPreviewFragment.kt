package tech.pylons.loud.fragments.screens.forest

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels

import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.constants.FightId.ID_ACID_DRAGON
import tech.pylons.loud.constants.FightId.ID_ACID_GIANT
import tech.pylons.loud.constants.FightId.ID_FIRE_DRAGON
import tech.pylons.loud.constants.FightId.ID_FIRE_GIANT
import tech.pylons.loud.constants.FightId.ID_GIANT
import tech.pylons.loud.constants.FightId.ID_GOBLIN
import tech.pylons.loud.constants.FightId.ID_ICE_DRAGON
import tech.pylons.loud.constants.FightId.ID_ICE_GIANT
import tech.pylons.loud.constants.FightId.ID_RABBIT
import tech.pylons.loud.constants.FightId.ID_TROLL
import tech.pylons.loud.constants.FightId.ID_UNDEAD_DRAGON
import tech.pylons.loud.constants.FightId.ID_WOLF
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_ACID_GIANT
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGON_ACID
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGON_FIRE
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGON_ICE
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_DRAGON_UNDEAD
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_FIRE_GIANT
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_GIANT
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_GOBLIN
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_ICE_GIANT
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_TROLL
import tech.pylons.loud.constants.Recipe.RCP_FIGHT_WOLF
import tech.pylons.loud.constants.Recipe.RCP_HUNT_RABBITS
import tech.pylons.loud.models.fight.Fight
import tech.pylons.loud.models.fight.FightPremium
import kotlinx.android.synthetic.main.fragment_forest_fight_preview.*
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class ForestFightPreviewFragment : Fragment() {
    private val Log = Logger.getLogger(ForestFightPreviewFragment::class.java.name)
    private var listener: OnFragmentInteractionListener? = null
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forest_fight_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fight = model.fightPreview
        val activeWeapon = model.getPlayer().value?.getActiveWeapon()
        text_header.text = fight.name
        text_description.text = when (fight.id) {
            ID_RABBIT -> getString(R.string.fight_rabbit_description)
            ID_GOBLIN -> getString(R.string.fight_goblin_description)
            ID_WOLF -> getString(R.string.fight_wolf_description)
            ID_TROLL -> getString(R.string.fight_troll_description)
            ID_GIANT -> getString(R.string.fight_giant_description)
            ID_FIRE_GIANT -> getString(R.string.fight_fire_giant_description)
            ID_ICE_GIANT -> getString(R.string.fight_ice_giant_description)
            ID_ACID_GIANT -> getString(R.string.fight_acid_giant_description)
            ID_FIRE_DRAGON -> getString(R.string.fight_fire_dragon_description)
            ID_ICE_DRAGON -> getString(R.string.fight_ice_dragon_description)
            ID_ACID_DRAGON -> getString(R.string.fight_acid_dragon_description)
            ID_UNDEAD_DRAGON -> getString(R.string.fight_undead_dragon_description)
            else -> ""
        }

        if (fight is FightPremium) {
            val textView = TextView(context)
            textView.text = "Cost ${fight.cost} ${getString(R.string.pylon_icon)} to fight"
            layout_fight_preview.addView(textView)
        }

        val textView2 = TextView(context)
        textView2.text = "Reward ${fight.reward}"
        layout_fight_preview.addView(textView2)

        if (fight.id != ID_RABBIT) {
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
            val player = model.getPlayer().value
            if (player != null) {
                if (fight is FightPremium && fight.cost > player.unlockedPylon) {
                    Toast.makeText(
                        context,
                        getString(R.string.not_enough_pylons),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val recipeId = when (fight.id) {
                    ID_RABBIT -> RCP_HUNT_RABBITS
                    ID_GOBLIN -> RCP_FIGHT_GOBLIN
                    ID_WOLF -> RCP_FIGHT_WOLF
                    ID_TROLL -> RCP_FIGHT_TROLL
                    ID_GIANT -> RCP_FIGHT_GIANT
                    ID_FIRE_GIANT -> RCP_FIGHT_FIRE_GIANT
                    ID_ICE_GIANT -> RCP_FIGHT_ICE_GIANT
                    ID_ACID_GIANT -> RCP_FIGHT_ACID_GIANT
                    ID_FIRE_DRAGON -> RCP_FIGHT_DRAGON_FIRE
                    ID_ICE_DRAGON -> RCP_FIGHT_DRAGON_ICE
                    ID_ACID_DRAGON -> RCP_FIGHT_DRAGON_ACID
                    ID_UNDEAD_DRAGON -> RCP_FIGHT_DRAGON_UNDEAD
                    else -> ""
                }

                if (recipeId.isNotBlank()) {
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
        }
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
