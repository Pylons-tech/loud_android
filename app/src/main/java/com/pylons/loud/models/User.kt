package com.pylons.loud.models

import android.content.Context
import com.pylons.loud.R
import com.pylons.loud.constants.LOUD_CBID
import com.pylons.wallet.core.types.Profile
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class User(
    var name: String,
    var gold: Long,
    var pylonAmount: Long,
    var characters: MutableList<Character>,
    var activeCharacter: Int,
    var weapons: MutableList<Weapon>,
    var activeWeapon: Int,
    var address: String
) {

    fun getActiveCharacter(): Character? {
        return if (activeCharacter > -1) {
            characters[activeCharacter]
        } else {
            null
        }
    }

    fun setActiveCharacter(item: Character) {
        activeCharacter = characters.indexOf(item)
    }

    fun getActiveWeapon(): Weapon? {
        return if (activeWeapon > -1) {
            weapons[activeWeapon]
        } else {
            null
        }
    }

    fun setActiveWeapon(item: Weapon) {
        activeWeapon = weapons.indexOf(item)
    }

    fun getItemIdByName(name: String): String {
        TODO("Got to fix data structure first")
    }

    fun saveAsync(context: Context) {
        val player = this
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_account), Context.MODE_PRIVATE
            )
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<User> =
                moshi.adapter<User>(User::class.java)
            val json = jsonAdapter.toJson(player)

            with(sharedPref.edit()) {
                putString(player.name, json)
                apply()
            }
        }
    }

    fun syncProfile(profile: Profile) {
        address = profile.credentials.address

        profile.coins?.forEach {
            when (it.denom) {
                "pylon" -> pylonAmount = it.amount
                "loudcoin" -> gold = it.amount
            }
        }

        val characters = mutableListOf<Character>()
        val weapons = mutableListOf<Weapon>()
        profile.items.forEach i@ {
            if (it.cookbookId != LOUD_CBID) {
                return@i
            }
            when (it.strings["Type"]) {
                "Character" -> {
                    val character = Character(
                        it.id,
                        it.strings["Name"]!!,
                        it.longs["level"]!!,
                        0,
                        it.doubles["XP"]!!,
                        it.longs["GiantKill"]!!,
                        it.longs["Special"]!!,
                        it.longs["SpecialDragonKill"]!!,
                        it.longs["UndeadDragonKill"]!!,
                        it.lastUpdate
                    )
                    characters.add(character)
                }
                else -> {
                    val weapon = Weapon(
                        it.id,
                        it.strings["Name"]!!,
                        it.longs["level"]!!,
                        it.doubles["attack"]!!,
                        0,
                        listOf(),
                        it.lastUpdate
                    )
                    weapons.add(weapon)
                }
            }
        }

        this.characters = characters
        this.weapons = weapons
    }
}