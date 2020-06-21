package com.pylons.loud.fragments.screens.pyloncentral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.fragments.lists.character.CharacterFragment
import com.pylons.loud.fragments.lists.character.MyCharacterRecyclerViewAdapter
import com.pylons.loud.models.Character

/**
 * A simple [Fragment] subclass.
 */
class PylonCentralBuyCharacterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pylon_central_buy_character, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frag = childFragmentManager.findFragmentById(R.id.fragment_character_list)
        val v = frag?.view as RecyclerView
        val c = context
        if (c is CharacterFragment.OnListFragmentInteractionListener) {
            v.adapter = MyCharacterRecyclerViewAdapter(
                listOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        0,
                        0,
                        0
                    )
                ), c,
                2
            )
        }
    }

}
