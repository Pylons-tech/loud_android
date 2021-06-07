package tech.pylons.loud.fragments.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.constants.Location.FOREST
import tech.pylons.loud.constants.Location.FRIENDS
import tech.pylons.loud.constants.Location.HOME
import tech.pylons.loud.constants.Location.PYLONS_CENTRAL
import tech.pylons.loud.constants.Location.SETTINGS
import tech.pylons.loud.constants.Location.SHOP
import kotlinx.android.synthetic.main.fragment_bottom_navigation.*

/**
 * A simple [Fragment] subclass.
 */
class BottomNavigationFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getPlayerLocation().observe(viewLifecycleOwner, Observer<Int> {
            text_home.isSelected = false
            text_forest.isSelected = false
            text_shop.isSelected = false
            text_pylons_central.isSelected = false
            text_friends.isSelected = false
            text_settings.isSelected = false

            when (it) {
                HOME -> text_home.isSelected = true
                FOREST -> text_forest.isSelected = true
                SHOP -> text_shop.isSelected = true
                PYLONS_CENTRAL -> text_pylons_central.isSelected = true
                FRIENDS -> text_friends.isSelected = true
                SETTINGS -> text_settings.isSelected = true
            }
        })

        text_home.setOnClickListener {
            listener?.onNavigation(HOME)
        }

        text_forest.setOnClickListener {
            listener?.onNavigation(FOREST)
        }

        text_shop.setOnClickListener {
            listener?.onNavigation(SHOP)
        }

        text_pylons_central.setOnClickListener {
            listener?.onNavigation(PYLONS_CENTRAL)
        }

        text_friends.setOnClickListener {
            listener?.onNavigation(FRIENDS)
        }

        text_settings.setOnClickListener {
            listener?.onNavigation(SETTINGS)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onNavigation(id: Int)
    }

}