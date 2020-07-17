package com.pylons.loud.fragments.screens.pyloncentral

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import kotlinx.android.synthetic.main.fragment_pylon_central_home.*

/**
 * A simple [Fragment] subclass.
 */
class PylonCentralHomeFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pylon_central_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_purchase_pylon.setOnClickListener {
            findNavController().navigate(R.id.purchasePylonFragment)
        }

        button_buy_5000_with_100_pylons.setOnClickListener {
            listener?.onBuyGoldWithPylons()
        }

        button_trade.setOnClickListener {
            findNavController().navigate(R.id.pylonCentralTradeFragment)
        }

        button_update_character.setOnClickListener {
            findNavController().navigate(R.id.updateCharacterFragment)
        }

        button_send_items.setOnClickListener {
            val player = model.getPlayer().value
            if (player != null && player.getItems().isNotEmpty()) {
                findNavController().navigate((R.id.sendItemScreenFragment))
            } else {
                Toast.makeText(context, getString(R.string.send_items_no_items), Toast.LENGTH_SHORT)
                    .show()
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onBuyGoldWithPylons()
    }

}
