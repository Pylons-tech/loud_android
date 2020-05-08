package com.pylons.loud.fragments.PlayerLocation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pylons.loud.R


import com.pylons.loud.fragments.PlayerLocation.PlayerLocationFragment.OnListFragmentInteractionListener
import com.pylons.loud.models.PlayerLocation

import kotlinx.android.synthetic.main.fragment_player_location.view.*

/**
 * [RecyclerView.Adapter] that can display a [PlayerLocation] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyPlayerLocationRecyclerViewAdapter(
    private val mValues: List<PlayerLocation>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyPlayerLocationRecyclerViewAdapter.ViewHolder>() {

    var selectedPos = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_player_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.name
        holder.itemView.isSelected = selectedPos == position;

        with(holder.mView) {
            setOnClickListener(View.OnClickListener {
                mListener?.onLocation(item)
            })
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
