package com.pylons.loud.fragments.PylonCentralScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.fragments.trade.MyTradeRecyclerViewAdapter
import com.pylons.loud.fragments.trade.TradeFragment
import com.pylons.loud.models.trade.*
import com.pylons.wallet.core.Core
import kotlinx.android.synthetic.main.fragment_pylon_central_trade.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.logging.Logger

const val MY_TRADES = 1
const val MARKET_TRADES = 2
/**
 * A simple [Fragment] subclass.
 */
class PylonCentralTradeFragment : Fragment() {
    private val Log = Logger.getLogger(PylonCentralTradeFragment::class.java.name)
    private var listener: OnFragmentInteractionListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pylon_central_trade, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTrades(MY_TRADES)

        text_create_trade.setOnClickListener {
            listener?.onCreateTrade()
        }

        text_market.setOnClickListener {
            getTrades(MARKET_TRADES)
        }

        text_my_trades.setOnClickListener {
            getTrades(MY_TRADES)
        }
    }

    private fun getTrades(type: Int) {
        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        val player = model.getPlayer().value
        CoroutineScope(IO).launch {
            val tradesResponse = Core.engine.listTrades()
            val trades = tradesResponse.filter {
                !it.completed && !it.disabled && it.extraInfo.contains("created by loud game")
            }
            Log.info(trades.toString())

            val list = trades.filter {
                if (type == MY_TRADES) {
                    it.sender == player?.address
                } else {
                    it.sender != player?.address
                }
            }.map {
                if (it.itemOutputs.isNotEmpty()) {
                    SellItemTrade(
                        it.id,
                        CoinInput(it.coinInputs[0].coin, it.coinInputs[0].count),
                        ItemOutput(
                            it.itemOutputs[0].strings["Name"] ?: "",
                            it.itemOutputs[0].longs["level"] ?: 0
                        ),
                        it.sender == player?.address
                    )
                } else if (it.itemInputs.isNotEmpty()) {
                    BuyItemTrade(
                        it.id,
                        ItemInput(
                            it.itemInputs[0].strings.find { it2 -> it2.key == "Name" }?.value ?: "",
                            it.itemInputs[0].longs.find { it2 -> it2.key == "level" }?.maxValue ?: 0
                        ),
                        CoinOutput(it.coinOutputs[0].denom, it.coinOutputs[0].amount),
                        it.sender == player?.address
                    )
                } else {
                    LoudTrade(
                        it.id,
                        CoinInput(it.coinInputs[0].coin, it.coinInputs[0].count),
                        CoinOutput(it.coinOutputs[0].denom, it.coinOutputs[0].amount),
                        it.sender == player?.address
                    )
                }
            }

            val frag = childFragmentManager.findFragmentById(R.id.fragment_trade_list)
            val view = frag?.view as RecyclerView
            val c = context

            if (c is TradeFragment.OnListFragmentInteractionListener) {
                withContext(Main) {
                    view.adapter = MyTradeRecyclerViewAdapter(list, c)
                }
            }

        }

        if (type == MY_TRADES) {
            text_market.visibility = View.VISIBLE
            text_my_trades.visibility = View.GONE
        } else {
            text_my_trades.visibility = View.VISIBLE
            text_market.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onCreateTrade()
    }
}