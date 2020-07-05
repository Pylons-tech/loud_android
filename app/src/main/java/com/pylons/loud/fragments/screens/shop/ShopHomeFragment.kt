package com.pylons.loud.fragments.screens.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.Shop.BUY
import com.pylons.loud.constants.Shop.SELL
import com.pylons.loud.constants.Shop.UPGRADE
import kotlinx.android.synthetic.main.fragment_shop_home.*

/**
 * A simple [Fragment] subclass.
 */
class ShopHomeFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_buy.setOnClickListener {
            model.shopAction = BUY
            findNavController().navigate(R.id.shopActionFragment)
        }

        button_sell.setOnClickListener {
            model.shopAction = SELL
            findNavController().navigate(R.id.shopActionFragment)
        }

        button_upgrade.setOnClickListener {
            model.shopAction = UPGRADE
            findNavController().navigate(R.id.shopActionFragment)
        }
    }

}
