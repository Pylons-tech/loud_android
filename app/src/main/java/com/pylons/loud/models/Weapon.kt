package com.pylons.loud.models

class Weapon(
    id: String,
    name: String,
    level: Int,
    attack: Int,
    price: Int,
    preItem: String,
    lastUpdate: Int
) : Item(id, name, level, attack, price, preItem, lastUpdate) {

}