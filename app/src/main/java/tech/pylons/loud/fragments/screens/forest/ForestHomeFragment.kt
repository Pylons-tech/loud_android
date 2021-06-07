package tech.pylons.loud.fragments.screens.forest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import tech.pylons.loud.R
import kotlinx.android.synthetic.main.fragment_forest_home.*
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class ForestHomeFragment : Fragment() {
    private val Log = Logger.getLogger(ForestHomeFragment::class.java.name)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forest_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_forest_basic.setOnClickListener {
            findNavController().navigate(R.id.forestBasicFragment)
        }

        button_forest_special.setOnClickListener {
            findNavController().navigate(R.id.forestSpecialFragment)
        }
    }

}
