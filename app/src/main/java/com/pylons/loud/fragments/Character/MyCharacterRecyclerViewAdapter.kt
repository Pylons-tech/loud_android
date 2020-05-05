package com.pylons.loud.fragments.Character

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pylons.loud.R


import com.pylons.loud.fragments.Character.CharacterFragment.OnListFragmentInteractionListener

import com.pylons.loud.models.Character
import kotlinx.android.synthetic.main.fragment_character.view.*

/**
 * [RecyclerView.Adapter] that can display a [Character] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyCharacterRecyclerViewAdapter(
    private val mValues: List<Character>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyCharacterRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    var selectedCharacterPostion = RecyclerView.NO_POSITION

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Character
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onCharacter(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_character, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.name

        if (selectedCharacterPostion == position) {
            holder.itemView.content.setTextColor( Color.GREEN)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
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
