package com.pylons.loud.models

abstract class Item {
    abstract val id: String
    abstract val name: String
    abstract val level: Long
    abstract val attack: Double
    abstract val value: Long
    abstract val lastUpdate: Long
    abstract val lockedTo: String

    fun getSellPriceRange(): String {
        val minPrice = value * .8
        val maxPrice = minPrice + 20
        return "${minPrice.toInt()}-${maxPrice.toInt()}"
    }
}