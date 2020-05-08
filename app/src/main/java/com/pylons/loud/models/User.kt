package com.pylons.loud.models

import android.content.Context
import com.pylons.loud.R
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
class User(
    var name: String,
    var gold: Int,
    var pylonAmount: Int,
    var characters: MutableList<Character>,
    var activeCharacter: Int,
    var weapons: MutableList<Weapon>,
    var activeWeapon: Int
) {

    fun getActiveCharacter(): Character? {
        if (activeCharacter > -1) {
            return characters[activeCharacter]
        } else {
            return null
        }
    }

    fun setActiveCharacter(item: Character) {
        activeCharacter = characters.indexOf(item)
    }

    fun getActiveWeapon(): Weapon? {
        if (activeWeapon > -1) {
            return weapons[activeWeapon]
        } else {
            return null
        }
    }

    fun setActiveWeapon(item: Weapon) {
        activeWeapon = weapons.indexOf(item)
    }

    fun saveAsync(context: Context) {
        val player = this
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_account), Context.MODE_PRIVATE)
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<User> =
                moshi.adapter<User>(User::class.java)
            val json = jsonAdapter.toJson(player)

            with (sharedPref.edit()) {
                putString(player.name, json)
                apply()
            }
        }
    }
}