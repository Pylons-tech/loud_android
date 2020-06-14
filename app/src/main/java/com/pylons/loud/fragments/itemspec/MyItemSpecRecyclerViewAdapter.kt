package com.pylons.loud.fragments.itemspec

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pylons.loud.R

import com.pylons.loud.fragments.itemspec.ItemSpecFragment.OnListFragmentInteractionListener
import com.pylons.loud.models.trade.CharacterSpec
import com.pylons.loud.models.trade.ItemSpec

import kotlinx.android.synthetic.main.fragment_item_spec.view.*

/**
 * [RecyclerView.Adapter] that can display a [ItemSpec] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyItemSpecRecyclerViewAdapter(
    private val mValues: List<ItemSpec>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyItemSpecRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ItemSpec
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onItemTradeBuy(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_spec, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.name

        when (item) {
            is CharacterSpec -> holder.mContentView.append(" Lv${item.level.min}-${item.level.max} XP=${item.xp.min}-${item.xp.max}")
            else -> holder.mContentView.append(" Lv${item.level.max}")
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
