package com.pylons.loud.models.trade

open class TradeInput
open class TradeOutput
abstract class Trade {
    abstract val id: String
    abstract val input: TradeInput
    abstract val output: TradeOutput
    abstract val isMyTrade: Boolean
}

class CoinInput(val coin: String, val amount: Long) : TradeInput()
class CoinOutput(val coin: String, val amount: Long) : TradeOutput()
data class LoudTrade(
    override val id: String,
    override val input: CoinInput,
    override val output: CoinOutput,
    override val isMyTrade: Boolean
) : Trade()


class ItemOutput(val name: String, val level: Long) : TradeOutput()
data class SellItemTrade(
    override val id: String,
    override val input: CoinInput,
    override val output: ItemOutput,
    override val isMyTrade: Boolean
) : Trade()

class ItemInput(val name: String, val level: Long) : TradeInput()
data class BuyItemTrade(
    override val id: String,
    override val input: ItemInput,
    override val output: CoinOutput,
    override val isMyTrade: Boolean
) : Trade()
