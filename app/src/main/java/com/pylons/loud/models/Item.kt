package com.pylons.loud.models

abstract class Item(
    val id: String,
    val name: String,
    val level: Int,
    val attack: Int,
    val price: Int,
    val preItem: String,
    var lastUpdate: Int
) {
}