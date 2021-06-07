package tech.pylons.loud.fragments.screens.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.pylons.loud.R
import tech.pylons.loud.fragments.lists.character.CharacterFragment
import tech.pylons.loud.fragments.lists.character.MyCharacterRecyclerViewAdapter
import tech.pylons.loud.models.Character

/**
 * A simple [Fragment] subclass.
 */
class BuyCharacterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buy_character, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frag = childFragmentManager.findFragmentById(R.id.fragment_character_list)
        val view = frag?.view as RecyclerView
        val c = context
        if (c is CharacterFragment.OnListFragmentInteractionListener) {
            view.adapter = MyCharacterRecyclerViewAdapter(
                listOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        0,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        ""
                    )
                ), c, 2
            )
        }
    }
}