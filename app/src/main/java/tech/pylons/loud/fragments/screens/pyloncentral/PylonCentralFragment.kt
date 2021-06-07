package tech.pylons.loud.fragments.screens.pyloncentral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.constants.Location.PYLONS_CENTRAL
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class PylonCentralFragment : Fragment() {
    private val Log = Logger.getLogger(PylonCentralFragment::class.java.name)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pylon_central, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.setPlayerLocation(PYLONS_CENTRAL)
    }
}
