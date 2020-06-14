package com.pylons.loud.fragments.InventoryScreen

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
import com.pylons.loud.fragments.Item.ItemFragment
import com.pylons.loud.fragments.Item.MyItemRecyclerViewAdapter
import com.pylons.loud.models.User
import kotlinx.android.synthetic.main.fragment_inventory.*

/**
 * A simple [Fragment] subclass.
 */
class InventoryScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_inventory_character.setText(R.string.inventory_desc)
        text_inventory_item.setText(R.string.inventory_desc_item)

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.setPlayerLocation(-1)
        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
            val adapter = MyItemRecyclerViewAdapter(player.weapons, frag.getListener(), 1)

            adapter.selectedPos = player.activeWeapon
            val view1 = frag.view as RecyclerView
            view1.adapter = adapter

            val frag2 =
                childFragmentManager.findFragmentById(R.id.fragment_character) as CharacterFragment
            val adapter2 = MyCharacterRecyclerViewAdapter(player.characters, frag2.getListener(), 1)
            adapter2.selectedPos = player.activeCharacter
            val view2 = frag2.view as RecyclerView
            view2.adapter = adapter2
        })
    }

}
