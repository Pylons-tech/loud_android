package com.pylons.loud.models

class User(
    var name: String,
    var gold: Int,
    var pylonAmount: Int,
    var characters: MutableList<Character>,
    var activeCharacter: Character?,
    var inventory: MutableList<Item>,
    var activeWeapon: Weapon?
) {

}