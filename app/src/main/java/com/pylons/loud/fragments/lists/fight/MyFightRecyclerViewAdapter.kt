package com.pylons.loud.fragments.lists.fight

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pylons.loud.R


import com.pylons.loud.fragments.lists.fight.FightFragment.OnListFragmentInteractionListener
import com.pylons.loud.models.fight.Fight

import kotlinx.android.synthetic.main.fragment_fight.view.*

/**
 * [RecyclerView.Adapter] that can display a [Fight] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyFightRecyclerViewAdapter(
    private val mValues: List<Fight>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyFightRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Fight
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onFight(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_fight, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.name

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
