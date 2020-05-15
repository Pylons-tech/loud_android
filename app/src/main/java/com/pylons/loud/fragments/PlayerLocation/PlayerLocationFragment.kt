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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.LocationConstants
import com.pylons.loud.models.PlayerLocation

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PlayerLocationFragment.OnListFragmentInteractionListener] interface.
 */
class PlayerLocationFragment : Fragment() {
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
                LocationConstants.HOME, getString(R.string.home)
            ),
            PlayerLocation(
                LocationConstants.FOREST, getString(R.string.forest)
            ),
            PlayerLocation(
                LocationConstants.SHOP, getString(R.string.shop)
            ),
            PlayerLocation(
                LocationConstants.PYLONS_CENTRAL, getString(R.string.pylons_central)
            ),
            PlayerLocation(
                LocationConstants.SETTINGS, getString(R.string.settings)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.getPlayerLocation().observe(viewLifecycleOwner, Observer<Int> { location ->
            val view = getView() as RecyclerView
            val adapter = view.adapter as MyPlayerLocationRecyclerViewAdapter
            if (adapter.selectedPos != location) {
                adapter.notifyItemChanged(adapter.selectedPos)
                adapter.selectedPos = location
                adapter.notifyItemChanged(location)
            }
        })
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
