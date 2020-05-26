package com.pylons.loud.fragments.SettingsScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.activities.LoginActivity
import com.pylons.loud.constants.Location.SETTINGS
import com.pylons.loud.constants.Recipe.RCP_GET_TEST_ITEMS
import kotlinx.android.synthetic.main.fragment_settings_screen.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsScreenFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.setPlayerLocation(SETTINGS)

        text_switch_account.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        text_get_dev_items.setOnClickListener {
            listener?.onGetDevItems()
        }

        text_get_pylons.setOnClickListener {
            listener?.onGetPylons()
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

    interface OnFragmentInteractionListener {
        fun onGetDevItems()
        fun onGetPylons()
    }
}
