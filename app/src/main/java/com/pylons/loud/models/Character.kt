package com.pylons.loud.models

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Transaction
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.delay

@JsonClass(generateAdapter = true)
data class Character(
    val id: String,
    val name: String,
    var level: Long,
    var price: Int,
    var xp: Double,
    var giantKill: Long,
    var special: Long,
    var specialDragonKill: Long,
    var undeadDragonKill: Long,
    var lastUpdate: Long
) {
    suspend fun rename(name: String): Transaction? {
        val tx = Core.engine.setItemFieldString(id, "Name", name)
        tx.submit()

        // TODO("Remove delay, walletcore should handle it")
        delay(5000)
        val txId = tx.id
        if (txId != null) {
            val tx = Core.engine.getTransaction(txId)
            return tx
        }

        return null
    }
}