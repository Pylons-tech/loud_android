package com.pylons.loud.fragments.PylonCentralScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pylons.loud.R
import java.util.logging.Logger

const val MY_TRADES = 1
const val MARKET_TRADES = 2
/**
 * A simple [Fragment] subclass.
 */
class PylonCentralTradeFragment : Fragment() {
    private val Log = Logger.getLogger(PylonCentralTradeFragment::class.java.name)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pylon_central_trade, container, false)
    }
}
