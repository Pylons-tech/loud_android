package com.pylons.loud.fragments.lists.friend

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pylons.loud.R

import com.pylons.loud.models.Friend

/**
 * [RecyclerView.Adapter] that can display a [Friend].
 */
class MyFriendRecyclerViewAdapter(
    private val values: List<Friend>
) : RecyclerView.Adapter<MyFriendRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.addressView.text = item.address
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.name)
        val addressView: TextView = view.findViewById(R.id.address)

        override fun toString(): String {
            return "ViewHolder(nameView=$nameView, addressView=$addressView)"
        }
    }
}