package com.pylons.loud.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weapon(
    override val id: String,
    override val name: String,
    override val level: Long,
    override val attack: Double,
    override val price: Int,
    override val preItem: List<String>,
    override var lastUpdate: Long
) : Item(id, name, level, attack, price, preItem, lastUpdate) {

}