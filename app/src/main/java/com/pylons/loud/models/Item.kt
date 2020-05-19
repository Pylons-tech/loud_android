package com.pylons.loud.models

abstract class Item {
    abstract val id: String
    abstract val name: String
    abstract val level: Long
    abstract val attack: Double
    abstract val lastUpdate: Long
}