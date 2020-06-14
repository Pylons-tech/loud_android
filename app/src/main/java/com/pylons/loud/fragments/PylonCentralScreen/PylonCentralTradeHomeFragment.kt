package com.pylons.loud.fragments.PylonCentralScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.Trade
import com.pylons.loud.fragments.trade.MyTradeRecyclerViewAdapter
import com.pylons.loud.fragments.trade.TradeFragment
import com.pylons.loud.models.trade.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import kotlinx.android.synthetic.main.fragment_pylon_central_trade_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class PylonCentralTradeHomeFragment : Fragment() {
    private val Log = Logger.getLogger(PylonCentralTradeHomeFragment::class.java.name)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pylon_central_trade_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTrades(MY_TRADES)

        text_create_trade.setOnClickListener {
            findNavController().navigate(R.id.createTradeFragment)
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
        CoroutineScope(Dispatchers.IO).launch {
            val tradesResponse = Core.engine.listTrades()
            val trades = tradesResponse.filter {
                !it.completed && !it.disabled && it.extraInfo.contains(Trade.DEFAULT)
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
                        it.sender == player?.address,
                        it.sender
                    )
                } else if (it.itemInputs.isNotEmpty()) {
                    BuyItemTrade(
                        it.id,
                        getItemSpec(it.itemInputs[0]),
                        CoinOutput(it.coinOutputs[0].denom, it.coinOutputs[0].amount),
                        it.sender == player?.address,
                        it.sender
                    )
                } else {
                    LoudTrade(
                        it.id,
                        CoinInput(it.coinInputs[0].coin, it.coinInputs[0].count),
                        CoinOutput(it.coinOutputs[0].denom, it.coinOutputs[0].amount),
                        it.sender == player?.address,
                        it.sender
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

            withContext(Main) {
                if (type == MY_TRADES) {
                    text_market.visibility = View.VISIBLE
                    text_my_trades.visibility = View.GONE

                    if (list.isEmpty()) {
                        text_trade_situation.text = getString(R.string.trade_situation_no_my_trades)
                    } else {
                        text_trade_situation.text = resources.getQuantityString(
                            R.plurals.trade_situation_my_trades,
                            list.size,
                            list.size
                        )
                    }
                } else {
                    text_my_trades.visibility = View.VISIBLE
                    text_market.visibility = View.GONE

                    if (list.isEmpty()) {
                        text_trade_situation.text = getString(R.string.trade_situation_no_market)
                    } else {
                        text_trade_situation.text = resources.getQuantityString(
                            R.plurals.trade_situation_market,
                            list.size,
                            list.size
                        )
                    }
                }
            }
        }


    }

    private fun getItemSpec(itemInput: ItemInput): ItemSpec {
        val name = itemInput.strings.find { it.key == "Name" }?.value ?: ""
        val level = itemInput.longs.find { it.key == "level" }

        return when {
            itemInput.longs.any {
                it.key == "Special"
            } -> CharacterSpec(
                name,
                Spec(
                    level?.minValue ?: 0,
                    level?.maxValue ?: 0
                ),
                Spec(
                    itemInput.doubles.find { it.key == "XP" }?.minValue?.toDouble() ?: 0.0,
                    itemInput.doubles.find { it.key == "XP" }?.maxValue?.toDouble() ?: 0.0
                ),
                itemInput.longs.find { it.key == "Special" }?.minValue ?: 0
            )
            name.contains("sword", true) -> WeaponSpec(
                name, Spec(
                    level?.minValue ?: 0,
                    level?.maxValue ?: 0
                ), Spec(
                    0,
                    0
                )
            )
            else -> MaterialSpec(
                name, Spec(
                    level?.minValue ?: 0,
                    level?.maxValue ?: 0
                )
            )
        }

    }
}
