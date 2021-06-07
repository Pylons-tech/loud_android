package tech.pylons.loud.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Material(
    override val id: String,
    override val name: String,
    override val level: Long,
    override val attack: Double,
    override val value: Long,
    override val lastUpdate: Long,
    override val lockedTo: String
) : Item()