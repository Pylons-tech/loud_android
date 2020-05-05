package com.pylons.loud.fragments.PlayerStatus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.models.PlayerAction
import com.pylons.loud.models.User
import kotlinx.android.synthetic.main.fragment_player_status.*
import java.util.logging.Logger

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayerStatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerStatusFragment : Fragment() {
    private val Log = Logger.getLogger(PlayerStatusFragment::class.java.name)

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GameScreenActivity.SharedViewModel by activityViewModels()

        layout_pylon_count.setOnClickListener {
            findNavController().navigate(R.id.pylonCentralFragment)
        }
        layout_gold_count.setOnClickListener {
            findNavController().navigate(R.id.shopScreenFragment)
        }
        text_character_icon.setOnClickListener {
            findNavController().navigate(R.id.inventoryFragment)
        }
        text_weapon_icon.setOnClickListener {
            findNavController().navigate(R.id.inventoryFragment)

        }

        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            text_player_name.text = player.name
            text_player_gold.text = player.gold.toString()
            text_player_pylon.text = player.pylonAmount.toString()

            val activeCharacter = player.activeCharacter
            if (activeCharacter != null) {
                text_active_character_name.text = activeCharacter.name
                text_active_character_level.text = activeCharacter.level.toString()
                text_active_character_xp.text = activeCharacter.xp.toString()
                text_active_character_hp.text =
                    activeCharacter.hp.toString() + "/" + activeCharacter.maxHP.toString()
                layout_active_character_details.visibility = View.VISIBLE
            } else {
                layout_active_character_details.visibility = View.INVISIBLE
            }

            val activeWeapon = player.activeWeapon
            if (activeWeapon != null) {
                text_active_weapon_name.text = activeWeapon.name
                text_active_weapon_level.text = activeWeapon.level.toString()
                text_active_weapon_attack.text = activeWeapon.attack.toString()
                layout_active_weapon_details.visibility = View.VISIBLE
            } else {
                layout_active_weapon_details.visibility = View.INVISIBLE
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerStatusFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerStatusFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
