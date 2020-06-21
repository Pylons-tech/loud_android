package com.pylons.loud.fragments.Item

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.pylons.loud.R

import com.pylons.loud.fragments.Item.ItemFragment.OnListFragmentInteractionListener
import com.pylons.loud.models.Item
import com.pylons.loud.models.Weapon

import kotlinx.android.synthetic.main.fragment_item.view.*
import java.util.logging.Logger

/**
 * [RecyclerView.Adapter] that can display a [Item] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyItemRecyclerViewAdapter(
    private val mValues: List<Item>,
    private val mListener: OnListFragmentInteractionListener?,
    private val mode: Int
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    private val Log = Logger.getLogger(MyItemRecyclerViewAdapter::class.java.name)

    private val mOnClickListener: View.OnClickListener
    var selectedPos = RecyclerView.NO_POSITION

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Item
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            when (mode) {
                1 -> mListener?.onItemSelect(item)
                2 -> mListener?.onItemBuy(item)
                3 -> mListener?.onItemSell(item)
                4 -> mListener?.onItemUpgrade(item)
                5 -> mListener?.onItemTradeSell(item)
                6 -> mListener?.onItemTradeBuy(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = "${item.name} Lv${item.level}"
        holder.itemView.isSelected = selectedPos == position;

        when (mode) {
            2 -> {
                holder.mPriceLayout.visibility = View.VISIBLE
                if (item is Weapon) {
                    holder.mPriceView.text = item.price.toString()
                }
            }
            3 -> {
                holder.mPriceLayout.visibility = View.VISIBLE
                holder.mPriceView.text = item.getSellPriceRange()
            }
            4 -> {
                holder.mPriceLayout.visibility = View.VISIBLE
                if (item is Weapon) {
                    holder.mPriceView.text = item.getUpgradePrice().toString()
                }
            }
            else -> {
                holder.mPriceLayout.visibility = View.INVISIBLE
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
        val mPriceView: TextView = mView.price
        val mPriceLayout: LinearLayout = mView.layout_price

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
