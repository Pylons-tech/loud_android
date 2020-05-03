package com.pylons.loud.models

class User(
    var name: String,
    var gold: Int,
    var pylonAmount: Int,
    var characters: List<Character>,
    var activeCharacter: Character?
) {

}