package com.pylons.loud.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class User(
    var name: String,
    var gold: Int,
    var pylonAmount: Int,
    var characters: MutableList<Character>,
    var activeCharacter: Character?,
    var inventory: MutableList<Weapon>,
    var activeWeapon: Weapon?
) {

}