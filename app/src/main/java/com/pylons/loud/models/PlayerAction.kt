package com.pylons.loud.models

data class PlayerAction(val id: Int, val name: String) {
    override fun toString(): String = name
}