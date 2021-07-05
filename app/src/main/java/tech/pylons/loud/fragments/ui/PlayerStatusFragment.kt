package tech.pylons.loud.fragments.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.constants.Character.ACID_SPECIAL
import tech.pylons.loud.constants.Character.FIRE_SPECIAL
import tech.pylons.loud.constants.Character.ICE_SPECIAL
import tech.pylons.loud.models.User
import kotlinx.android.synthetic.main.fragment_player_status.*
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class PlayerStatusFragment : Fragment() {
    private val Log = Logger.getLogger(PlayerStatusFragment::class.java.name)
    private var listener: OnFragmentInteractionListener? = null
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_refresh.setOnClickListener {
            listener?.onRefresh()
        }

        layout_pylon_count.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.pylonCentralFragment)
        }
        layout_gold_count.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.shopScreenFragment)
        }
        layout_active_character.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.inventoryFragment)
        }
        layout_active_weapon.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.inventoryFragment)
        }

        model.getPlayer().observe(viewLifecycleOwner, { player ->
            text_player_name.text = player.name
            text_player_gold.text = player.gold.toString()
            text_player_pylon.text = player.pylonAmount.toString()
            text_locked_gold.text = " (${getString(R.string.lock_icon)} ${player.lockedGold})"
            text_locked_pylon.text = " (${getString(R.string.lock_icon)} ${player.lockedPylonAmount})"

            val activeCharacter = player.getActiveCharacter()
            if (activeCharacter != null) {
                text_active_character_name.text = activeCharacter.name
                text_active_character_level.text = activeCharacter.level.toString()
                text_active_character_xp.text = activeCharacter.xp.toString()
                text_character_icon.text =
                    when (activeCharacter.special) {
                        FIRE_SPECIAL -> getString(R.string.fire_icon)
                        ICE_SPECIAL -> getString(R.string.ice_icon)
                        ACID_SPECIAL -> getString(R.string.acid_icon)
                        else -> getString(R.string.character_icon)
                    }

                layout_active_character_badges.removeAllViews()
                var addedBadge = false
                if (activeCharacter.undeadDragonKill > 0) {
                    val text = TextView(context)
                    text.text =
                        "${getString(R.string.undead_dragon_icon)} x${activeCharacter.undeadDragonKill}"
                    addBadge(text)
                    addedBadge = true
                }

                if (activeCharacter.specialDragonKill > 0) {
                    val icon = when (activeCharacter.special) {
                        FIRE_SPECIAL -> getString(R.string.fire_dragon_icon)
                        ICE_SPECIAL -> getString(R.string.ice_dragon_icon)
                        ACID_SPECIAL -> getString(R.string.acid_dragon_icon)
                        else -> ""
                    }
                    val text = TextView(context)
                    text.text = "$icon x${activeCharacter.specialDragonKill}"
                    addBadge(text)
                    addedBadge = true
                }

                if (activeCharacter.giantKill > 0) {
                    val text = TextView(context)
                    text.text = "${getString(R.string.giant_icon)} x${activeCharacter.giantKill}"
                    addBadge(text)
                    addedBadge = true
                }

                if (addedBadge) {
                    layout_active_character_badges.visibility = View.VISIBLE
                } else {
                    layout_active_character_badges.visibility = View.GONE
                }

                layout_active_character_details.visibility = View.VISIBLE
                text_no_active_character.visibility = View.GONE
            } else {
                text_character_icon.text = getString(R.string.character_icon)
                layout_active_character_details.visibility = View.GONE
                layout_active_character_badges.removeAllViews()
                layout_active_character_badges.visibility = View.GONE
                text_no_active_character.visibility = View.VISIBLE
            }

            val activeWeapon = player.getActiveWeapon()
            if (activeWeapon != null) {
                text_active_weapon_name.text = activeWeapon.name
                text_active_weapon_level.text = activeWeapon.level.toString()
                text_active_weapon_attack.text = activeWeapon.attack.toString()
                layout_active_weapon_details.visibility = View.VISIBLE
                text_no_active_weapon.visibility = View.GONE
            } else {
                layout_active_weapon_details.visibility = View.GONE
                text_no_active_weapon.visibility = View.VISIBLE
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onRefresh()
    }

    private fun addBadge(text: TextView) {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            0, 0, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16F, resources
                    .displayMetrics
            ).toInt(), 0
        )
        text.layoutParams = params
        layout_active_character_badges.addView(text)
    }
}
