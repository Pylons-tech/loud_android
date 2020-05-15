package com.pylons.loud.models

abstract class Item(
    open val id: String,
    open val name: String,
    open val level: Long,
    open val attack: Double,
    open val price: Int,
    open val preItem: List<String>,
    open var lastUpdate: Long
) {
}