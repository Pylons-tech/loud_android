package com.pylons.loud.fragments.PylonCentralScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.fragments.Character.CharacterFragment
import com.pylons.loud.fragments.Character.MyCharacterRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_update_character.*

/**
 * A simple [Fragment] subclass.
 */
class UpdateCharacterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_character, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frag =
            childFragmentManager.findFragmentById(R.id.fragment_character_list) as CharacterFragment
        childFragmentManager.beginTransaction().hide(frag).commit()

        val model: GameScreenActivity.SharedViewModel by activityViewModels()

        model.getPlayer().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val list = it.characters
                if (list.isNotEmpty()) {
                    val adapter = MyCharacterRecyclerViewAdapter(
                        list,
                        frag.getListener(),
                        4
                    )
                    val view1 = frag.view as RecyclerView
                    view1.adapter = adapter

                    text_update_character_desc.text = getString(R.string.update_character_desc)
                    childFragmentManager.beginTransaction().show(frag).commit()
                } else {
                    text_update_character_desc.text =
                        getString(R.string.update_character_desc_no_character)
                }
            }
        })
    }

}
