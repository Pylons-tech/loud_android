package com.pylons.loud.fragments.screens.pyloncentral

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.Character.ACID_SPECIAL
import com.pylons.loud.constants.Character.FIRE_SPECIAL
import com.pylons.loud.constants.Character.ICE_SPECIAL
import com.pylons.loud.constants.Character.NO_SPECIAL
import com.pylons.loud.constants.Coin
import com.pylons.loud.constants.Item.ANGEL_SWORD
import com.pylons.loud.constants.Item.BRONZE_SWORD
import com.pylons.loud.constants.Item.COPPER_SWORD
import com.pylons.loud.constants.Item.DROP_DRAGONACID
import com.pylons.loud.constants.Item.DROP_DRAGONFIRE
import com.pylons.loud.constants.Item.DROP_DRAGONICE
import com.pylons.loud.constants.Item.GOBLIN_EAR
import com.pylons.loud.constants.Item.IRON_SWORD
import com.pylons.loud.constants.Item.SILVER_SWORD
import com.pylons.loud.constants.Item.TROLL_TOES
import com.pylons.loud.constants.Item.WOLF_TAIL
import com.pylons.loud.constants.Item.WOODEN_SWORD
import com.pylons.loud.constants.Recipe.LOUD_CBID
import com.pylons.loud.constants.Trade
import com.pylons.loud.constants.Trade.MINIMUM_TRADE_PRICE
import com.pylons.loud.fragments.lists.character.CharacterFragment
import com.pylons.loud.fragments.lists.character.MyCharacterRecyclerViewAdapter
import com.pylons.loud.fragments.lists.item.ItemFragment
import com.pylons.loud.fragments.lists.item.MyItemRecyclerViewAdapter
import com.pylons.loud.fragments.lists.itemspec.ItemSpecFragment
import com.pylons.loud.fragments.lists.itemspec.MyItemSpecRecyclerViewAdapter
import com.pylons.loud.models.Character
import com.pylons.loud.models.Item
import com.pylons.loud.models.trade.CharacterSpec
import com.pylons.loud.models.trade.MaterialSpec
import com.pylons.loud.models.trade.Spec
import com.pylons.loud.models.trade.WeaponSpec
import com.pylons.wallet.core.types.tx.recipe.*
import com.pylons.wallet.core.types.tx.trade.TradeItemInput
import kotlinx.android.synthetic.main.create_trade_buy.*
import kotlinx.android.synthetic.main.create_trade_confirm.*
import kotlinx.android.synthetic.main.create_trade_sell.*
import kotlinx.android.synthetic.main.dialog_input.view.*
import kotlinx.android.synthetic.main.fragment_create_trade.*
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class CreateTradeFragment : Fragment() {
    private val Log = Logger.getLogger(CreateTradeFragment::class.java.name)

    private var listener: OnFragmentInteractionListener? = null

    private var coinInput = listOf<CoinInput>()
    private var itemInput = listOf<TradeItemInput>()
    private var coinOutput = listOf<CoinOutput>()
    private var itemOutput = listOf<com.pylons.wallet.core.types.tx.item.Item>()
    private var extraInfo = Trade.DEFAULT
    private lateinit var itemBuyFragment: ItemSpecFragment
    private lateinit var characterSellFragment: CharacterFragment
    private lateinit var itemSellFragment: ItemFragment
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_trade, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragments()
        initModel()
        initTextPylonsBuy()
        initTextGoldBuy()
        initCharacterBuy()
        initItemBuy()
        initTextGoldSell()
        initCharacterSell()
        initItemSell()
        initCreateTradeButton()
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
        fun onCreateTrade(
            coinInput: List<CoinInput>,
            itemInput: List<TradeItemInput>,
            coinOutput: List<CoinOutput>,
            itemOutput: List<com.pylons.wallet.core.types.tx.item.Item>,
            extraInfo: String
        )
    }

    private fun initFragments() {
        itemBuyFragment =
            childFragmentManager.findFragmentById(R.id.fragment_item_buy) as ItemSpecFragment
        characterSellFragment =
            childFragmentManager.findFragmentById(R.id.fragment_character_sell) as CharacterFragment
        itemSellFragment =
            childFragmentManager.findFragmentById(R.id.fragment_item_sell) as ItemFragment
        childFragmentManager.beginTransaction().hide(itemBuyFragment).commit()
        childFragmentManager.beginTransaction().hide(characterSellFragment).commit()
        childFragmentManager.beginTransaction().hide(itemSellFragment).commit()
    }

    private fun initModel() {
        model.getTradeInput().observe(viewLifecycleOwner, Observer { item ->
            if (item != null) {
                Log.info(item.toString())
                itemInput = when (item) {
                    is CharacterSpec ->
                        listOf(
                            TradeItemInput(
                                LOUD_CBID,
                                ItemInput(
                                    listOf(
                                        DoubleInputParam(
                                            "XP",
                                            item.xp.min.toString(),
                                            item.xp.max.toString()
                                        )
                                    ),
                                    listOf(
                                        LongInputParam(
                                            "level",
                                            item.level.min,
                                            item.level.max
                                        ),
                                        LongInputParam(
                                            "Special",
                                            item.special,
                                            item.special
                                        )
                                    ),
                                    listOf(StringInputParam("Name", item.name))
                                )
                            )
                        )

                    is WeaponSpec ->
                        listOf(
                            TradeItemInput(
                                LOUD_CBID,
                                ItemInput(
                                    listOf(),
                                    listOf(
                                        LongInputParam(
                                            "level",
                                            item.level.min,
                                            item.level.max
                                        )
                                    ),
                                    listOf(StringInputParam("Name", item.name))
                                )
                            )
                        )

                    is MaterialSpec ->
                        listOf(
                            TradeItemInput(
                                LOUD_CBID,
                                ItemInput(
                                    listOf(),
                                    listOf(
                                        LongInputParam(
                                            "level",
                                            item.level.min,
                                            item.level.max
                                        )
                                    ),
                                    listOf(StringInputParam("Name", item.name))
                                )
                            )
                        )
                    else -> listOf()
                }
                extraInfo = when (item) {
                    is CharacterSpec -> Trade.CHARACTER_BUY
                    else -> Trade.ITEM_BUY
                }

                promptBuyOrderStep2()
                model.setTradeInput(null)
            }
        })

        model.getTradeOutput().observe(viewLifecycleOwner, Observer { output ->
            if (output != null) {
                Log.info(output.toString())

                val c = context
                if (c != null) {
                    itemOutput = listOf(output)
                    extraInfo = if (output.strings["Type"] == "Character") {
                        Trade.CHARACTER_SELL
                    } else {
                        Trade.ITEM_SELL
                    }
                    Log.info(itemOutput.toString())
                    displayConfirmTrade()
                    model.setTradeOutput(null)
                }
            }
        })
    }

    private fun initTextPylonsBuy() {
        button_pylons_buy.setOnClickListener {
            childFragmentManager.beginTransaction().hide(itemBuyFragment).commit()

            val c = context
            if (c != null) {
                val mDialogView = LayoutInflater.from(c).inflate(R.layout.dialog_input, null)

                val dialogBuilder = AlertDialog.Builder(c, R.style.MyDialogTheme)
                dialogBuilder.setMessage(
                    getString(R.string.trade_pylon_buy, MINIMUM_TRADE_PRICE)
                )
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.setView(mDialogView)
                alert.show()

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val amount = mDialogView.edit_text_amount.text.toString()
                    if (amount.isNotBlank() && amount.toLong() >= MINIMUM_TRADE_PRICE) {
                        coinInput = listOf(
                            CoinInput(
                                Coin.PYLON,
                                amount.toLong()
                            )
                        )

                        displaySellTrade()
                        alert.dismiss()
                    } else {
                        Toast.makeText(
                            c,
                            getString(R.string.enter_valid_amount),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun initTextGoldBuy() {
        button_gold_buy.setOnClickListener {
            childFragmentManager.beginTransaction().hide(itemBuyFragment).commit()

            val c = context
            if (c != null) {
                val mDialogView = LayoutInflater.from(c).inflate(R.layout.dialog_input, null)

                val dialogBuilder = AlertDialog.Builder(c, R.style.MyDialogTheme)
                dialogBuilder.setMessage(
                    getString(R.string.trade_gold_buy)
                )
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.setView(mDialogView)
                alert.show()

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val amount = mDialogView.edit_text_amount.text.toString()
                    if (amount.isNotBlank() && amount.toLong() > 0) {
                        coinInput = listOf(
                            CoinInput(
                                Coin.LOUD,
                                amount.toLong()
                            )
                        )
                        extraInfo = Trade.DEFAULT
                        promptBuyOrderStep2()
                        alert.dismiss()
                    } else {
                        Toast.makeText(
                            c,
                            getString(R.string.enter_valid_amount),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun initCharacterBuy() {
        button_character_buy.setOnClickListener {
            val adapter = MyItemSpecRecyclerViewAdapter(
                listOf(
                    CharacterSpec("LionBaby", Spec(1, 2), Spec(1.0, 1000000.0), NO_SPECIAL),
                    CharacterSpec("FireBaby", Spec(1, 1000), Spec(1.0, 1000000.0), FIRE_SPECIAL),
                    CharacterSpec("IceBaby", Spec(1, 1000), Spec(1.0, 1000000.0), ICE_SPECIAL),
                    CharacterSpec("AcidBaby", Spec(1, 1000), Spec(1.0, 1000000.0), ACID_SPECIAL)
                ),
                itemBuyFragment.getListener()
            )
            val view1 = itemBuyFragment.view as RecyclerView
            view1.adapter = adapter
            childFragmentManager.beginTransaction().show(itemBuyFragment).commit()
        }
    }

    private fun initItemBuy() {
        button_item_buy.setOnClickListener {
            val adapter = MyItemSpecRecyclerViewAdapter(
                listOf(
                    WeaponSpec(
                        WOODEN_SWORD,
                        Spec(1, 1),
                        Spec(3, 3)
                    ),
                    WeaponSpec(
                        WOODEN_SWORD,
                        Spec(2, 2),
                        Spec(6, 6)
                    ),
                    WeaponSpec(
                        COPPER_SWORD,
                        Spec(1, 1),
                        Spec(10, 10)
                    ),
                    WeaponSpec(
                        COPPER_SWORD,
                        Spec(2, 2),
                        Spec(20, 20)
                    ),
                    WeaponSpec(
                        SILVER_SWORD,
                        Spec(1, 1),
                        Spec(30, 30)
                    ),
                    WeaponSpec(
                        BRONZE_SWORD,
                        Spec(1, 1),
                        Spec(50, 50)
                    ),
                    WeaponSpec(
                        IRON_SWORD,
                        Spec(1, 1),
                        Spec(100, 100)
                    ),
                    WeaponSpec(
                        ANGEL_SWORD,
                        Spec(1, 1),
                        Spec(1000, 1000)
                    ),
                    MaterialSpec(
                        TROLL_TOES,
                        Spec(1, 1)
                    ),
                    MaterialSpec(
                        WOLF_TAIL,
                        Spec(1, 1)
                    ),
                    MaterialSpec(
                        GOBLIN_EAR,
                        Spec(1, 1)
                    ),
                    MaterialSpec(
                        TROLL_TOES,
                        Spec(1, 1)
                    ),
                    MaterialSpec(
                        DROP_DRAGONFIRE,
                        Spec(1, 1)
                    ),
                    MaterialSpec(
                        DROP_DRAGONICE,
                        Spec(1, 1)
                    ),
                    MaterialSpec(
                        DROP_DRAGONACID,
                        Spec(1, 1)
                    )
                ), itemBuyFragment.getListener()
            )
            val myView = itemBuyFragment.view as RecyclerView
            myView.adapter = adapter
            childFragmentManager.beginTransaction().show(itemBuyFragment).commit()
        }
    }

    private fun promptBuyOrderStep2() {
        val c = context
        if (c != null) {
            val mDialogView = LayoutInflater.from(c).inflate(R.layout.dialog_input, null)
            val dialogBuilder = AlertDialog.Builder(c, R.style.MyDialogTheme)
            dialogBuilder.setMessage(
                getString(R.string.trade_pylon_offer, MINIMUM_TRADE_PRICE)
            )
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                    itemInput = listOf()
                    coinInput = listOf()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.setView(mDialogView)
            alert.show()

            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val amount = mDialogView.edit_text_amount.text.toString()
                val player = model.getPlayer().value
                if (amount.isNotBlank() && amount.toLong() >= MINIMUM_TRADE_PRICE && amount.toLong() < player?.pylonAmount ?: -1
                ) {
                    coinOutput = listOf(
                        CoinOutput(
                            Coin.PYLON,
                            amount.toLong()
                        )
                    )
                    displayConfirmTrade()
                    alert.dismiss()
                } else {
                    Toast.makeText(c, getString(R.string.enter_valid_amount), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun displaySellTrade() {
        layout_create_trade_buy.visibility = View.GONE
        layout_create_trade_sell.visibility = View.VISIBLE
        layout_create_trade_confirm.visibility = View.GONE
    }

    private fun initTextGoldSell() {
        button_gold_sell.setOnClickListener {
            with(childFragmentManager) {
                beginTransaction().hide(characterSellFragment).commit()
                beginTransaction().hide(itemSellFragment).commit()
            }

            val c = context
            if (c != null) {
                val mDialogView = LayoutInflater.from(c).inflate(R.layout.dialog_input, null)

                val dialogBuilder = AlertDialog.Builder(c, R.style.MyDialogTheme)
                dialogBuilder.setMessage(
                    getString(R.string.trade_gold_sell)
                )
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.setView(mDialogView)
                alert.show()

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val amount = mDialogView.edit_text_amount.text.toString()
                    val player = model.getPlayer().value
                    if (amount.isNotBlank() && amount.toLong() > 0 && amount.toLong() < player?.gold ?: -1) {
                        coinOutput = listOf(
                            CoinOutput(
                                Coin.LOUD,
                                amount.toLong()
                            )
                        )
                        extraInfo = Trade.DEFAULT
                        displayConfirmTrade()
                        alert.dismiss()
                    } else {
                        Toast.makeText(
                            c,
                            getString(R.string.enter_valid_amount),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun initCharacterSell() {
        val player = model.getPlayer().value
        val list = player?.characters ?: listOf<Character>()
        button_character_sell.text = "${getString(R.string.trade_character)} (${list.size})"

        button_character_sell.setOnClickListener {
            val player = model.getPlayer().value
            val list = player?.characters ?: listOf<Character>()

            if (list.isNotEmpty()) {
                val adapter = MyCharacterRecyclerViewAdapter(
                    list,
                    characterSellFragment.getListener(),
                    3
                )
                val view1 = characterSellFragment.view as RecyclerView
                view1.adapter = adapter

                with(childFragmentManager) {
                    beginTransaction().hide(itemSellFragment).commit()
                    beginTransaction().show(characterSellFragment).commit()
                }
            }
        }
    }

    private fun initItemSell() {
        val player = model.getPlayer().value
        if (player != null) {
            val items = mutableListOf<Item>()
            items.addAll(player.weapons)
            items.addAll(player.materials)
            button_item_sell.text = "${getString(R.string.trade_item)} (${items.size})"
        }

        button_item_sell.setOnClickListener {
            val player = model.getPlayer().value
            if (player != null) {
                val items = mutableListOf<Item>()
                items.addAll(player.weapons)
                items.addAll(player.materials)

                if (items.isNotEmpty()) {
                    val adapter =
                        MyItemRecyclerViewAdapter(items, itemSellFragment.getListener(), 5)
                    val myView = itemSellFragment.view as RecyclerView
                    myView.adapter = adapter

                    with(childFragmentManager) {
                        beginTransaction().hide(characterSellFragment).commit()
                        beginTransaction().show(itemSellFragment).commit()
                    }
                }
            }
        }
    }

    private fun displayConfirmTrade() {
        layout_create_trade_buy.visibility = View.GONE
        layout_create_trade_sell.visibility = View.GONE
        layout_create_trade_confirm.visibility = View.VISIBLE

        if (coinOutput.isNotEmpty()) {
            text_trade_output.text = "${coinOutput[0].amount} ${coinOutput[0].denom}"
        }

        if (coinInput.isNotEmpty()) {
            text_trade_input.text = "${coinInput[0].count} ${coinInput[0].coin}"
        }

        if (itemOutput.isNotEmpty()) {
            text_trade_output.text =
                "${itemOutput[0].strings["Name"]} Lv${itemOutput[0].longs["level"]}"
        }

        if (itemInput.isNotEmpty()) {
            val specLevel = itemInput[0].itemInput.longs.find { it.key == "level" }
            text_trade_input.text = if (specLevel != null) {
                val range = if (specLevel.maxValue == specLevel.minValue) {
                    specLevel.maxValue
                } else {
                    "${specLevel.minValue}-${specLevel.maxValue}"
                }
                "${itemInput[0].itemInput.strings.find { it.key == "Name" }?.value} Lv$range"
            } else {
                "${itemInput[0].itemInput.strings.find { it.key == "Name" }?.value}"
            }
        }

    }

    private fun initCreateTradeButton() {
        button_create_trade.setOnClickListener {
            listener?.onCreateTrade(
                coinInput,
                itemInput,
                coinOutput,
                itemOutput,
                extraInfo
            )
        }
    }

}
