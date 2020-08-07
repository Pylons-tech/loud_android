package com.pylons.loud.fragments.lists.item

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.pylons.loud.R

import com.pylons.loud.fragments.lists.item.ItemFragment.OnListFragmentInteractionListener
import com.pylons.loud.models.Character
import com.pylons.loud.models.Item
import com.pylons.loud.models.Weapon

import kotlinx.android.synthetic.main.fragment_item.view.*
import kotlinx.android.synthetic.main.fragment_item.view.content
import kotlinx.android.synthetic.main.fragment_item_inventory_character.view.*
import java.util.logging.Logger

/**
 * [RecyclerView.Adapter] that can display a [Item] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyItemRecyclerViewAdapter(
    private val mValues: List<Item>,
    private val mListener: OnListFragmentInteractionListener?,
    private val mode: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val Log = Logger.getLogger(MyItemRecyclerViewAdapter::class.java.name)

    private val mOnClickListener: View.OnClickListener
    var activeCharacterPos = RecyclerView.NO_POSITION
    var activeWeaponPos = RecyclerView.NO_POSITION

    companion object {
        private const val TYPE_INVENTORY_CHARACTER = 1
    }

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
                7 -> mListener?.onItemSend(item)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = mValues[position]
        return if (mode == 1 && item is Character) {
            TYPE_INVENTORY_CHARACTER
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_INVENTORY_CHARACTER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item_inventory_character, parent, false)
            InventoryCharacterViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mValues[position]
        if (getItemViewType(position) == TYPE_INVENTORY_CHARACTER) {
            (holder as InventoryCharacterViewHolder).setContent(item as Character)
        } else {
            holder as ViewHolder
            holder.mContentView.text = "${item.name} Lv${item.level}"

            if (activeCharacterPos == position) {
                holder.mContentView.append(" [Active Character]")
            }

            if (activeWeaponPos == position) {
                holder.mContentView.append(" [Active Weapon]")
            }

            if (item.lockedTo.isNotBlank()) {
                with(mListener as Context) {
                    holder.mContentView.append(" ${getString(R.string.lock_icon)}")
                }
            }

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

    inner class InventoryCharacterViewHolder(private val mView: View) :
        RecyclerView.ViewHolder(mView) {
        private val mContentView: TextView = mView.content
        val mBadgesLayout: LinearLayout = mView.layout_badges

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }

        fun setContent(item: Character) {
            mContentView.text = "${item.name} Lv${item.level}"
            if (activeCharacterPos == adapterPosition) {
                mContentView.append(" [Active Character]")
            }

            if (item.lockedTo.isNotBlank()) {
                with(mListener as Context) {
                    mContentView.append(" ${getString(R.string.lock_icon)}")
                }
            }

            render(item, this)

            with(mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }
    }

    private fun addBadge(context: Context, text: TextView, holder: InventoryCharacterViewHolder) {
        with(context) {
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(
                0, 0, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16F, resources
                        .displayMetrics
                ).toInt(), 0
            )
            text.layoutParams = params
            holder.mBadgesLayout.addView(text)
        }
    }

    private fun render(character: Character, holder: InventoryCharacterViewHolder) {
        val context = mListener as Context
        with(context) {
            holder.mBadgesLayout.removeAllViews()

            val text = TextView(context)
            text.text = when (character.special) {
                com.pylons.loud.constants.Character.FIRE_SPECIAL -> getString(R.string.fire_icon)
                com.pylons.loud.constants.Character.ICE_SPECIAL -> getString(R.string.ice_icon)
                com.pylons.loud.constants.Character.ACID_SPECIAL -> getString(R.string.acid_icon)
                else -> getString(R.string.character_icon)
            }
            addBadge(this, text, holder)

            if (character.undeadDragonKill > 0) {
                val text = TextView(context)
                text.text =
                    "${getString(R.string.undead_dragon_icon)} x${character.undeadDragonKill}"
                addBadge(this, text, holder)
            }

            if (character.specialDragonKill > 0) {
                val icon = when (character.special) {
                    com.pylons.loud.constants.Character.FIRE_SPECIAL -> getString(R.string.fire_dragon_icon)
                    com.pylons.loud.constants.Character.ICE_SPECIAL -> getString(R.string.ice_dragon_icon)
                    com.pylons.loud.constants.Character.ACID_SPECIAL -> getString(R.string.acid_dragon_icon)
                    else -> ""
                }
                val text = TextView(context)
                text.text = "$icon x${character.specialDragonKill}"
                addBadge(this, text, holder)
            }

            if (character.giantKill > 0) {
                val text = TextView(context)
                text.text = "${getString(R.string.giant_icon)} x${character.giantKill}"
                addBadge(this, text, holder)
            }
        }

    }
}
