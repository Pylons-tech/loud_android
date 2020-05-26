package com.pylons.loud.fragments.PylonCentralScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.pylons.loud.R
import kotlinx.android.synthetic.main.fragment_pylon_central_home.*
import kotlinx.android.synthetic.main.fragment_settings_screen.*

/**
 * A simple [Fragment] subclass.
 */
class PylonCentralHomeFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pylon_central_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_buy_character.setOnClickListener {
            findNavController().navigate(R.id.pylonCentralBuyCharacterFragment)
        }

        text_buy_5000_with_100_pylons.setOnClickListener {
            listener?.onBuyGoldWithPylons()
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
