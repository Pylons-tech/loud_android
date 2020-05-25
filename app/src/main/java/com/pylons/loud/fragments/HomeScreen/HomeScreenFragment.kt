package com.pylons.loud.fragments.HomeScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.Location.HOME
import com.pylons.loud.models.User
import com.pylons.loud.utils.RenderText.getHomeDesc
import kotlinx.android.synthetic.main.fragment_home_screen.*

/**
 * A simple [Fragment] subclass.
 */
class HomeScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.setPlayerLocation(HOME)
        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            text_home_screen.setText(getHomeDesc(player))
        })
    }
}
