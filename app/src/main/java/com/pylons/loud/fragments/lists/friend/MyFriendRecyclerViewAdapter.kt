package com.pylons.loud.fragments.lists.friend

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pylons.loud.R
import com.pylons.loud.fragments.lists.character.CharacterFragment

import com.pylons.loud.models.Friend
import com.pylons.loud.models.fight.Fight

/**
 * [RecyclerView.Adapter] that can display a [Friend].
 */
class MyFriendRecyclerViewAdapter(
    private val values: List<Friend>,
    private val mListener: FriendFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyFriendRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Friend
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onFriend(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.addressView.text = item.address

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val nameView: TextView = mView.findViewById(R.id.name)
        val addressView: TextView = mView.findViewById(R.id.address)

        override fun toString(): String {
            return "ViewHolder(nameView=$nameView, addressView=$addressView)"
        }
    }
}