package tech.pylons.loud.models.trade

open class TradeInput
open class TradeOutput
abstract class Trade {
    abstract val id: String
    abstract val input: TradeInput
    abstract val output: TradeOutput
    abstract val isMyTrade: Boolean
    abstract val sender: String
}

class CoinInput(val coin: String, val amount: Long) : TradeInput()
class CoinOutput(val coin: String, val amount: Long) : TradeOutput()
data class LoudTrade(
    override val id: String,
    override val input: CoinInput,
    override val output: CoinOutput,
    override val isMyTrade: Boolean,
    override val sender: String
) : Trade()


class ItemOutput(val name: String, val level: Long) : TradeOutput()
data class SellItemTrade(
    override val id: String,
    override val input: CoinInput,
    override val output: ItemOutput,
    override val isMyTrade: Boolean,
    override val sender: String
) : Trade()

data class BuyItemTrade(
    override val id: String,
    override val input: ItemSpec,
    override val output: CoinOutput,
    override val isMyTrade: Boolean,
    override val sender: String
) : Trade()

class Spec<T>(val min: T, val max: T)
open class ItemSpec(val name: String, val level: Spec<Long>): TradeInput()
class CharacterSpec(name: String, level: Spec<Long>, val xp: Spec<Double>, val special: Long) : ItemSpec(name, level)
class WeaponSpec(name: String, level: Spec<Long>, val attack: Spec<Int>) : ItemSpec(name, level)
class MaterialSpec(name: String, level: Spec<Long>) : ItemSpec(name, level)