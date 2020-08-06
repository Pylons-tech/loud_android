package com.pylons.loud.fragments.lists.character

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pylons.loud.R


import com.pylons.loud.fragments.lists.character.CharacterFragment.OnListFragmentInteractionListener

import com.pylons.loud.models.Character
import kotlinx.android.synthetic.main.fragment_character.view.*
import java.util.logging.Logger

/**
 * [RecyclerView.Adapter] that can display a [Character] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyCharacterRecyclerViewAdapter(
    private val mValues: List<Character>,
    private val mListener: OnListFragmentInteractionListener?,
    private val mode: Int
) : RecyclerView.Adapter<MyCharacterRecyclerViewAdapter.ViewHolder>() {
    private val Log = Logger.getLogger(MyCharacterRecyclerViewAdapter::class.java.name)

    private val mOnClickListener: View.OnClickListener
    var selectedPos = RecyclerView.NO_POSITION

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Character
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.

            when(mode) {
                1-> mListener?.onCharacter(item)
                2-> mListener?.onBuyCharacter(item)
                3 -> mListener?.onCharacterTradeSell(item)
                4 -> mListener?.onCharacterUpdate(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_character, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = "${item.name} Lv${item.level}"
        holder.itemView.isSelected = selectedPos == position;

        if (item.lockedTo.isNotBlank()) {
            with(mListener as Context) {
                holder.mContentView.append(" ${getString(R.string.lock_icon)}")
            }
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
