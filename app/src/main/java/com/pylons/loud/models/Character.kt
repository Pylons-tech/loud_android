package com.pylons.loud.models

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Transaction
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.delay

@JsonClass(generateAdapter = true)
data class Character(
    override val id: String,
    override val name: String,
    override var level: Long,
    override val attack: Double,
    override val value: Long,
    override var lastUpdate: Long,
    var price: Int,
    var xp: Double,
    var giantKill: Long,
    var special: Long,
    var specialDragonKill: Long,
    var undeadDragonKill: Long
) : Item() {
    suspend fun rename(name: String): Transaction {
        val tx = Core.engine.setItemFieldString(id, "Name", name)
        tx.submit()

        // TODO("Remove delay, walletcore should handle it")
        delay(5000)

        val id = tx.id
        return if (id != null) {
            val resultTx = Core.engine.getTransaction(id)
            resultTx
        } else {
            tx
        }
    }
}