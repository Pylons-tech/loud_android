package com.pylons.loud.fragments.Character

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

import com.pylons.loud.models.Character
import com.pylons.loud.models.User

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [CharacterFragment.OnListFragmentInteractionListener] interface.
 */
class CharacterFragment : Fragment() {
    private lateinit var myview: RecyclerView

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                myview = view
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter =
                    MyCharacterRecyclerViewAdapter(
                        listOf(),
                        listener
                    )
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            val adapter = MyCharacterRecyclerViewAdapter(player.characters, listener)

            adapter.selectedCharacterPostion = player.characters.indexOf(player.activeCharacter)
            myview.adapter = adapter
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
        fun onCharacter(item: Character?)
    }

}
