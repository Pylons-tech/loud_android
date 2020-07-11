package com.pylons.loud.fragments.screens.senditem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.fragments.lists.friend.FriendFragment
import com.pylons.loud.fragments.lists.friend.MyFriendRecyclerViewAdapter

/**
 * A simple [Fragment] subclass.
 */
class SendItemScreenFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_item_screen, container, false)
    }
}