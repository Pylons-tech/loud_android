package tech.pylons.loud.models

import com.squareup.moshi.JsonClass

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
    var undeadDragonKill: Long,
    override val lockedTo: String
) : Item()