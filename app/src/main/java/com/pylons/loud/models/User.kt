package com.pylons.loud.models

import android.content.Context
import com.pylons.loud.R
import com.pylons.loud.constants.Coin
import com.pylons.loud.constants.Recipe.LOUD_CBID
import com.pylons.loud.models.trade.*
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
    var materials: MutableList<Material>,
    var address: String,
    var friends: MutableList<Friend>
) {

    fun getActiveCharacter(): Character? {
        return if (activeCharacter != -1 && activeCharacter < characters.size) {
            characters[activeCharacter]
        } else {
            null
        }
    }

    fun setActiveCharacter(item: Character) {
        activeCharacter = characters.indexOf(item)
    }

    fun getActiveWeapon(): Weapon? {
        return if (activeWeapon != -1 && activeWeapon < weapons.size) {
            weapons[activeWeapon]
        } else {
            null
        }
    }

    fun setActiveWeapon(item: Weapon) {
        activeWeapon = weapons.indexOf(item)
    }

    fun getItemIdByName(name: String): String {
        return materials.find {
            it.name == name
        }?.id ?: return weapons.find {
            it.name == name
        }?.id ?: ""
    }

    fun getItemNameByItemId(id: String): String {
        return materials.find {
            it.id == id
        }?.name ?: return weapons.find {
            it.id == id
        }?.name ?: ""
    }

    fun saveSync(context: Context) {
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
                commit()
            }
        }
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
        val prevActiveCharacterId = getActiveCharacter()?.id
        val prevActiveWeaponId = getActiveWeapon()?.id

        address = profile.credentials.address

        var pylonAmount = 0L
        var goldAmount = 0L
        profile.coins.forEach {
            when (it.denom) {
                Coin.PYLON -> pylonAmount = it.amount
                Coin.LOUD -> goldAmount = it.amount
            }
        }
        this.pylonAmount = pylonAmount
        this.gold = goldAmount

        val characters = mutableListOf<Character>()
        val weapons = mutableListOf<Weapon>()
        val materials = mutableListOf<Material>()
        profile.items.forEach i@{
            if (it.cookbookId != LOUD_CBID) {
                return@i
            }
            when (it.strings["Type"]) {
                "Character" -> {
                    val character = Character(
                        it.id,
                        it.strings["Name"] ?: "",
                        it.longs["level"] ?: 0,
                        0.0,
                        0,
                        it.lastUpdate,
                        0,
                        it.doubles["XP"] ?: 0.0,
                        it.longs["GiantKill"] ?: 0,
                        it.longs["Special"] ?: 0,
                        it.longs["SpecialDragonKill"] ?: 0,
                        it.longs["UndeadDragonKill"] ?: 0
                    )
                    characters.add(character)
                }
                else -> {
                    if (it.strings["Name"]?.contains("sword")!!) {
                        val weapon = Weapon(
                            it.id,
                            it.strings["Name"] ?: "",
                            it.longs["level"] ?: 0,
                            it.doubles["attack"] ?: 0.0,
                            it.longs["value"] ?: 0,
                            0,
                            listOf(),
                            it.lastUpdate
                        )
                        weapons.add(weapon)
                    } else {
                        val material = Material(
                            it.id,
                            it.strings["Name"] ?: "",
                            it.longs["level"] ?: 0,
                            it.doubles["attack"] ?: 0.0,
                            it.longs["value"] ?: 0,
                            it.lastUpdate
                        )
                        materials.add(material)
                    }
                }
            }
        }

        this.characters = characters
        this.weapons = weapons
        this.materials = materials

        if (getActiveCharacter()?.id != prevActiveCharacterId) {
            activeCharacter = -1
        }

        if (getActiveWeapon()?.id != prevActiveWeaponId) {
            activeWeapon = -1
        }
    }

    fun canFulfillTrade(trade: Trade): Boolean {
        when (trade) {
            is LoudTrade -> {
                when (trade.input.coin) {
                    Coin.LOUD -> return gold >= trade.input.amount
                    Coin.PYLON -> return pylonAmount >= trade.input.amount
                }
            }
            is SellItemTrade -> {
                when (trade.input.coin) {
                    Coin.PYLON -> return pylonAmount >= trade.input.amount
                }
            }
            is BuyItemTrade -> {
                return when (trade.input) {
                    is CharacterSpec -> characters.any {
                        it.special == trade.input.special &&
                                it.name == trade.input.name &&
                                it.xp >= trade.input.xp.min &&
                                it.xp <= trade.input.xp.max &&
                                it.level >= trade.input.level.min &&
                                it.level <= trade.input.level.max
                    }
                    else -> {
                        val items = mutableListOf<Item>()
                        items.addAll(weapons)
                        items.addAll(materials)
                        return items.any {
                            it.name == trade.input.name &&
                                    it.level >= trade.input.level.min &&
                                    it.level <= trade.input.level.max
                        }
                    }
                }
            }
        }

        return false
    }

    fun getMatchingTradeItems(trade: Trade): List<Item> {
        if (trade is BuyItemTrade) {
            return when (trade.input) {
                is CharacterSpec -> characters.filter {
                    it.special == trade.input.special &&
                            it.name == trade.input.name &&
                            it.xp >= trade.input.xp.min &&
                            it.xp <= trade.input.xp.max &&
                            it.level >= trade.input.level.min &&
                            it.level <= trade.input.level.max
                }
                else -> {
                    val items = mutableListOf<Item>()
                    items.addAll(weapons)
                    items.addAll(materials)
                    return items.filter {
                        it.name == trade.input.name &&
                                it.level >= trade.input.level.min &&
                                it.level <= trade.input.level.max
                    }
                }
            }
        }

        return listOf()
    }

    fun addFriend(address: String, name: String) {
        friends.add(Friend(address, name))
    }

    fun deleteFriend(friend: Friend) {
        friends.removeIf {
            it.address == friend.address && it.name == friend.name
        }
    }

    fun getItems(): List<Item> {
        val items = mutableListOf<Item>()
        items.addAll(characters)
        items.addAll(weapons)
        items.addAll(materials)
        return items
    }
}