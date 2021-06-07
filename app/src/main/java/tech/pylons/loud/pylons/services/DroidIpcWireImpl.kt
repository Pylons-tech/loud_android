package tech.pylons.loud.pylons.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.beust.klaxon.*
import tech.pylons.ipc.DroidIpcWire
import tech.pylons.ipc.IPCLayer
import tech.pylons.ipc.Message
import tech.pylons.ipc.Response
import tech.pylons.lib.Wallet
import tech.pylons.lib.klaxon
import tech.pylons.lib.types.*
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import java.lang.StringBuilder
import java.util.*
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions

/**
 * Test Wallet wrapper
 * only refer the internal logic
 */
class DroidIpcWireImpl() : DroidIpcWire() {

    companion object {
        val instance: DroidIpcWireImpl = DroidIpcWireImpl()
    }

    init { DroidIpcWire.implementation = this }

    override fun readString(): String? {
        return WalletInitializer.getIpcConnection().getFromWallet()
    }

    override fun writeString(s: String) {
        WalletInitializer.getIpcConnection().submitToWallet(s)
    }

}