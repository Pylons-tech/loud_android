package tech.pylons.loud.fragments.screens.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.fragments.lists.item.ItemFragment
import tech.pylons.loud.fragments.lists.item.MyItemRecyclerViewAdapter
import tech.pylons.loud.models.User
import kotlinx.android.synthetic.main.fragment_inventory.*

/**
 * A simple [Fragment] subclass.
 */
class InventoryScreenFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()
    private lateinit var itemFragment: ItemFragment
    private var mode: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemFragment = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment

        model.setPlayerLocation(-1)
        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            when (mode) {
                1 -> setAllItems(player)
                2 -> setCharacters(player)
                3 -> setWeapons(player)
            }
        })

        button_all.setOnClickListener {
            model.getPlayer().value?.let {
                setAllItems(it)
            }
        }

        button_characters.setOnClickListener {
            model.getPlayer().value?.let {
                setCharacters(it)
            }
        }

        button_weapons.setOnClickListener {
            model.getPlayer().value?.let {
                setWeapons(it)
            }
        }
    }

    private fun setAllItems(player: User) {
        mode = 1
        val items = player.getItems()
        val activeWeapon = player.getActiveWeapon()
        val activeCharacter = player.getActiveCharacter()

        val adapter = MyItemRecyclerViewAdapter(items, itemFragment.getListener(), 1)

        if (activeWeapon != null) {
            val activeWeaponIndex = items.indexOfFirst {
                it.id == activeWeapon.id
            }
            adapter.activeWeaponPos = activeWeaponIndex
        }

        if (activeCharacter != null) {
            val activeCharacterIndex = items.indexOfFirst {
                it.id == activeCharacter.id
            }
            adapter.activeCharacterPos = activeCharacterIndex
        }

        val view = itemFragment.view as RecyclerView
        view.adapter = adapter
    }

    private fun setCharacters(player: User) {
        mode = 2
        val adapter = MyItemRecyclerViewAdapter(player.characters, itemFragment.getListener(), 1)
        adapter.activeCharacterPos = player.activeCharacter
        val view = itemFragment.view as RecyclerView
        view.adapter = adapter
    }

    private fun setWeapons(player: User) {
        mode = 3
        val adapter = MyItemRecyclerViewAdapter(player.weapons, itemFragment.getListener(), 1)
        adapter.activeWeaponPos = player.activeWeapon
        val view = itemFragment.view as RecyclerView
        view.adapter = adapter
    }

}
