package com.pylons.loud.fragments.PlayerAction

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

import com.pylons.loud.models.PlayerAction
import java.util.logging.Logger

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PlayerActionFragment.OnListFragmentInteractionListener] interface.
 */
class PlayerActionFragment : Fragment() {
    private val Log = Logger.getLogger(PlayerActionFragment::class.java.name)
    private lateinit var myview: RecyclerView
    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.info("OnCreate")

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.info("OnCreateView")

        val view = inflater.inflate(R.layout.fragment_player_action_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            myview = view
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                adapter =
                    MyPlayerActionRecyclerViewAdapter(
                        listOf(),
                        listener
                    )
            }
        }
        Log.info("Return view")

        return view
    }

    fun setAdapter(items: List<PlayerAction>) {
        myview.adapter =
            MyPlayerActionRecyclerViewAdapter(
                items,
                listener
            )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.info("onAttach")
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
        fun onAction(action: PlayerAction?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PlayerActionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
