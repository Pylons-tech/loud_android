package com.pylons.loud.fragments.PylonCentralScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.Recipe.RCP_BUY_GOLD_WITH_PYLON
import kotlinx.android.synthetic.main.fragment_pylon_central_home.*

/**
 * A simple [Fragment] subclass.
 */
class PylonCentralHomeFragment : Fragment() {

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
            val model: GameScreenActivity.SharedViewModel by activityViewModels()
            model.setPlayerAction(RCP_BUY_GOLD_WITH_PYLON)
            // TODO("Need to display response message")
        }

    }

}
