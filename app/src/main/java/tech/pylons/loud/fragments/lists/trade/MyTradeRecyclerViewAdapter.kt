package tech.pylons.loud.fragments.lists.trade

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import tech.pylons.loud.R


import tech.pylons.loud.fragments.lists.trade.TradeFragment.OnListFragmentInteractionListener
import tech.pylons.loud.models.trade.*

import kotlinx.android.synthetic.main.fragment_trade_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [Trade] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyTradeRecyclerViewAdapter(
    private val mValues: List<Trade>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyTradeRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Trade
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            if (item.isMyTrade) {
                mListener?.onCancel(item)
            } else {
                mListener?.onTrade(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_trade_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        when (item) {
            is LoudTrade -> {
                holder.mTextOutput.text = "${item.output.amount} ${item.output.coin}"
                holder.mTextInput.text = "${item.input.amount} ${item.input.coin}"
            }
            is SellItemTrade -> {
                holder.mTextOutput.text = "${item.output.name} Lv${item.output.level}"
                holder.mTextInput.text = "${item.input.amount} ${item.input.coin}"
            }
            is BuyItemTrade -> {
                holder.mTextOutput.text = "${item.output.amount} ${item.output.coin}"
                holder.mTextInput.text =
                    when (item.input) {
                        is CharacterSpec -> "${item.input.name} Lv${item.input.level.min} - ${item.input.level.max} XP=${item.input.xp.min} - ${item.input.xp.max}"
                        else -> "${item.input.name} Lv${item.input.level.max}"
                    }
            }
        }

        with(holder.itemView.context) {
            holder.mTextInput.append(" ${getString(R.string.trade_wanted)}")
        }

        if (item.isMyTrade) {
            holder.mAction.text = "Cancel"
            holder.mTextSender.visibility = View.GONE
        } else {
            holder.mAction.text = "Trade"
            holder.mTextSender.text = item.sender
            holder.mTextSender.visibility = View.VISIBLE
        }

        with(holder.mAction) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTextSender: TextView = mView.text_sender
        val mTextOutput: TextView = mView.text_output
        val mTextInput: TextView = mView.text_input
        val mAction: Button = mView.action

        override fun toString(): String {
            return "ViewHolder(mView=$mView, mTextOutput=$mTextOutput, mTextInput=$mTextInput, mAction=$mAction)"
        }

    }
}
