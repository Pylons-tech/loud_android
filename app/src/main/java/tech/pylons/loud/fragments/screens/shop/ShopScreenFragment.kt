package tech.pylons.loud.fragments.screens.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.constants.Location.SHOP

/**
 * A simple [Fragment] subclass.
 */
class ShopScreenFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.setPlayerLocation(SHOP)
    }
}
