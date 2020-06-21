package com.pylons.loud.fragments.screens.pyloncentral.trade

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.fragments.lists.item.ItemFragment
import com.pylons.loud.fragments.lists.item.MyItemRecyclerViewAdapter

/**
 * A simple [Fragment] subclass.
 */
class ItemSelectFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val frag = childFragmentManager.findFragmentById(R.id.fragment_item_select) as ItemFragment
        val fragView = frag.view as RecyclerView
        val c = context as ItemFragment.OnListFragmentInteractionListener
        fragView.adapter = MyItemRecyclerViewAdapter(model.tradeBuyMatchingItems, c, 6)
    }
}