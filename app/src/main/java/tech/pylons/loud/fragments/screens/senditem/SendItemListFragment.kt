package tech.pylons.loud.fragments.screens.senditem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.fragments.lists.item.ItemFragment
import tech.pylons.loud.fragments.lists.item.MyItemRecyclerViewAdapter

/**
 * A simple [Fragment] subclass.
 */
class SendItemListFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val player = model.getPlayer().value

        if (player != null) {
            val frag = childFragmentManager.findFragmentById(R.id.fragment_item)
            val view = frag?.view as RecyclerView
            val c = context
            if (c is ItemFragment.OnListFragmentInteractionListener) {
                view.adapter = MyItemRecyclerViewAdapter(player.getItems(), c, 7)
            }
        }
    }
}