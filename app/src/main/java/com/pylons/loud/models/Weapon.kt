package com.pylons.loud.models

import com.pylons.loud.constants.Item.WOODEN_SWORD
import com.pylons.loud.constants.Item.COPPER_SWORD
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weapon(
    override val id: String,
    override val name: String,
    override val level: Long,
    override val attack: Double,
    val price: Int,
    val preItem: List<String>,
    override val lastUpdate: Long
) : Item() {
    fun getUpgradePrice(): Int {
        return when (name) {
            WOODEN_SWORD -> 100
            COPPER_SWORD -> 250
            else -> -1
        }
    }
}
