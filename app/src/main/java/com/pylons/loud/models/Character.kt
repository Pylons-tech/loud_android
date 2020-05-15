package com.pylons.loud.models

import com.squareup.moshi.JsonClass

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

}