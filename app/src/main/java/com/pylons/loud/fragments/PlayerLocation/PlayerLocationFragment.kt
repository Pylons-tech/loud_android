package com.pylons.loud.fragments.PlayerLocation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pylons.loud.R
import com.pylons.loud.constants.LocationConstants
import com.pylons.loud.models.PlayerAction
import com.pylons.loud.models.PlayerLocation

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PlayerLocationFragment.OnListFragmentInteractionListener] interface.
 */
class PlayerLocationFragment : Fragment() {
    private lateinit var myview: RecyclerView

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_location_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                myview = view
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter =
                    MyPlayerLocationRecyclerViewAdapter(
                        getLocations(),
                        listener
                    )
            }
        }
        return view
    }

    fun getLocations(): List<PlayerLocation> {
        return listOf(
            PlayerLocation(
                LocationConstants.HOME, getString(R.string.home), listOf(
                    PlayerAction(1, getString(R.string.select_active_character)),
                    PlayerAction(2, getString(R.string.select_active_weapon)),
                    PlayerAction(3, getString(R.string.restore_character_health))
                )
            ),
            PlayerLocation(
                LocationConstants.FOREST, getString(R.string.forest), listOf(
                    PlayerAction(1, getString(R.string.rabbit)),
                    PlayerAction(2, getString(R.string.goblin)),
                    PlayerAction(3, getString(R.string.wolf)),
                    PlayerAction(4, getString(R.string.troll)),
                    PlayerAction(5, getString(R.string.giant))
                )
            ),
            PlayerLocation(
                LocationConstants.SHOP, getString(R.string.shop), listOf(
                    PlayerAction(1, getString(R.string.buy_items)),
                    PlayerAction(2, getString(R.string.sell_items)),
                    PlayerAction(3, getString(R.string.upgrade_items))
                )
            ),
            PlayerLocation(
                LocationConstants.PYLONS_CENTRAL, getString(R.string.pylons_central), listOf(
                    PlayerAction(1, getString(R.string.buy_characters)),
                    PlayerAction(2, getString(R.string.buy_5000_with_100_pylons)),
                    PlayerAction(
                        3,
                        getString(R.string.sell_gold_from_orderbook_place_order_to_buy)
                    ),
                    PlayerAction(
                        4,
                        getString(R.string.buy_gold_from_orderbook_place_order_to_sell)
                    ),
                    PlayerAction(
                        5,
                        getString(R.string.sell_weapon_from_orderbook_place_order_to_buy)
                    ),
                    PlayerAction(
                        6,
                        getString(R.string.buy_weapon_from_orderbook_place_order_to_sell)
                    ),
                    PlayerAction(
                        7,
                        getString(R.string.sell_character_from_orderbook_place_order_to_buy)
                    ),
                    PlayerAction(
                        8,
                        getString(R.string.buy_character_from_orderbook_place_order_to_sell)
                    ),
                    PlayerAction(9, getString(R.string.update_character_name))
                )
            )
        )
    }

    fun setAdapter(items: List<PlayerLocation>) {
        myview.adapter =
            MyPlayerLocationRecyclerViewAdapter(
                items,
                listener
            )
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onLocation(location: PlayerLocation?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PlayerLocationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
