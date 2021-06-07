package tech.pylons.loud.fragments.screens.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import tech.pylons.loud.BuildConfig
import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.activities.LoginActivity
import tech.pylons.loud.constants.Location.SETTINGS
import tech.pylons.loud.utils.Account.setCurrentAccountUserName
import kotlinx.android.synthetic.main.fragment_settings_screen.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsScreenFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.setPlayerLocation(SETTINGS)

        val versionName: String = tech.pylons.loud.BuildConfig.VERSION_NAME
        text_version.text = "Version $versionName"

        button_switch_account.setOnClickListener {
            context?.let { it1 -> setCurrentAccountUserName(it1, "") }
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        button_get_dev_items.setOnClickListener {
            listener?.onGetDevItems()
        }

        button_get_pylons.setOnClickListener {
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
