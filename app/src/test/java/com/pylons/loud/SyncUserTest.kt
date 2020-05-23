package com.pylons.loud

import com.pylons.loud.models.Character
import com.pylons.loud.models.Material
import com.pylons.loud.models.User
import com.pylons.loud.models.Weapon
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.Coin
import com.pylons.wallet.core.types.Profile
import com.pylons.wallet.core.types.tx.item.Item
import org.junit.Test
import org.junit.Assert.*

class SyncUserTest {
    data class MyTest(val test: String, val user: User, val profile: Profile, val expected: User) {
    }

    private val tests = arrayOf<MyTest>(
        MyTest(
            "Default",
            User(
                "cluo",
                0,
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                -1,
                mutableListOf(),
                ""
            ), Profile(
                TxPylonsEngine.Credentials("abc"),
                mutableMapOf(),
                listOf(),
                listOf(
                )
            ), User(
                "cluo",
                0,
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                "abc"
            )
        ),
        MyTest(
            "Gold, items, characters, weapons",
            User(
                "cluo",
                0,
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                -1,
                mutableListOf(),
                ""
            ), Profile(
                TxPylonsEngine.Credentials("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"),
                mutableMapOf(),
                listOf(
                    Coin("loudcoin", 5000),
                    Coin("pylon", 500)
                ),
                listOf(
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        3000,
                        mapOf("XP" to 1.0),
                        mapOf(
                            "level" to 1L,
                            "GiantKill" to 0L,
                            "Special" to 0L,
                            "SpecialDragonKill" to 0L,
                            "UndeadDragonKill" to 0L
                        ),
                        mapOf("Name" to "Tiger", "Type" to "Character")
                    ),
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        5000,
                        mapOf("attack" to 3.0),
                        mapOf("level" to 1L, "value" to 100L),
                        mapOf("Name" to "Wooden sword")
                    )
                )
            ), User(
                "cluo",
                5000,
                500,
                mutableListOf(
                    Character(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "Tiger",
                        1,
                        0,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        3000
                    )
                ),
                -1,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "Wooden sword",
                        1,
                        3.0,
                        100,
                        0,
                        listOf(),
                        5000
                    )
                ),
                -1,
                mutableListOf(),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            )
        ),
        MyTest(
            "Wrong cookbook id should not sync characters and weapons",
            User(
                "cluo",
                0,
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                -1,
                mutableListOf(),
                ""
            ), Profile(
                TxPylonsEngine.Credentials("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"),
                mutableMapOf(),
                listOf(
                    Coin("loudcoin", 5000),
                    Coin("pylon", 500)
                ),
                listOf(
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "LOUD-v0.1.0",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        3000,
                        mapOf("XP" to 1.0),
                        mapOf(
                            "level" to 1L,
                            "GiantKill" to 0L,
                            "Special" to 0L,
                            "SpecialDragonKill" to 0L,
                            "UndeadDragonKill" to 0L
                        ),
                        mapOf("Name" to "Tiger", "Type" to "Character")
                    ),
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "LOUD-v0.1.0",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        5000,
                        mapOf("attack" to 3.0),
                        mapOf("level" to 1L),
                        mapOf("Name" to "Wooden sword")
                    )
                )
            ), User(
                "cluo",
                5000,
                500,
                mutableListOf(
                ),
                -1,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            )
        ),
        MyTest(
            "Wrong cookbook id should not sync characters, but sync weapon",
            User(
                "cluo",
                0,
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                -1,
                mutableListOf(),
                ""
            ), Profile(
                TxPylonsEngine.Credentials("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"),
                mutableMapOf(),
                listOf(
                    Coin("loudcoin", 5000),
                    Coin("pylon", 500)
                ),
                listOf(
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "LOUD-v0.1.0",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        3000,
                        mapOf("XP" to 1.0),
                        mapOf(
                            "level" to 1L,
                            "GiantKill" to 0L,
                            "Special" to 0L,
                            "SpecialDragonKill" to 0L,
                            "UndeadDragonKill" to 0L
                        ),
                        mapOf("Name" to "Tiger", "Type" to "Character")
                    ),
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        5000,
                        mapOf("attack" to 3.0),
                        mapOf("level" to 1L, "value" to 100L),
                        mapOf("Name" to "Wooden sword")
                    )
                )
            ), User(
                "cluo",
                5000,
                500,
                mutableListOf(
                ),
                -1,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "Wooden sword",
                        1,
                        3.0,
                        100,
                        0,
                        listOf(),
                        5000
                    )
                ),
                -1,
                mutableListOf(),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            )
        ),
        MyTest(
            "Gold, items, characters, weapons, materials",
            User(
                "cluo",
                0,
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                -1,
                mutableListOf(),
                ""
            ), Profile(
                TxPylonsEngine.Credentials("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"),
                mutableMapOf(),
                listOf(
                    Coin("loudcoin", 5000),
                    Coin("pylon", 500)
                ),
                listOf(
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        3000,
                        mapOf("XP" to 1.0),
                        mapOf(
                            "level" to 1L,
                            "GiantKill" to 0L,
                            "Special" to 0L,
                            "SpecialDragonKill" to 0L,
                            "UndeadDragonKill" to 0L
                        ),
                        mapOf("Name" to "Tiger", "Type" to "Character")
                    ),
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        5000,
                        mapOf("attack" to 3.0),
                        mapOf("level" to 1L, "value" to 100L),
                        mapOf("Name" to "Wooden sword")
                    ),
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        5500,
                        mapOf("attack" to 0.0),
                        mapOf("level" to 1L, "value" to 50L),
                        mapOf("Name" to "Goblin ear")
                    )
                )
            ), User(
                "cluo",
                5000,
                500,
                mutableListOf(
                    Character(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "Tiger",
                        1,
                        0,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        3000
                    )
                ),
                -1,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "Wooden sword",
                        1,
                        3.0,
                        100,
                        0,
                        listOf(),
                        5000
                    )
                ),
                -1,
                mutableListOf(
                    Material(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k",
                        "Goblin ear",
                        1,
                        0.0,
                        50,
                        5500
                    )
                ),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            )
        ),

        MyTest(
            "Gold, items, characters, weapons, materials (existing data)",
            User(
                "cluo",
                5000,
                500,
                mutableListOf(
                    Character(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "Tiger",
                        1,
                        0,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        3000
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "Wooden sword",
                        1,
                        3.0,
                        100,
                        0,
                        listOf(),
                        5000
                    )
                ),
                0,
                mutableListOf(
                    Material(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k",
                        "Goblin ear",
                        1,
                        0.0,
                        50,
                        5500
                    )
                ),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            ), Profile(
                TxPylonsEngine.Credentials("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"),
                mutableMapOf(),
                listOf(
                    Coin("loudcoin", 5000),
                    Coin("pylon", 500)
                ),
                listOf(
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        3000,
                        mapOf("XP" to 1.0),
                        mapOf(
                            "level" to 1L,
                            "GiantKill" to 0L,
                            "Special" to 0L,
                            "SpecialDragonKill" to 0L,
                            "UndeadDragonKill" to 0L
                        ),
                        mapOf("Name" to "Tiger", "Type" to "Character")
                    ),
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        5000,
                        mapOf("attack" to 3.0),
                        mapOf("level" to 1L, "value" to 100L),
                        mapOf("Name" to "Wooden sword")
                    ),
                    Item(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k",
                        "LOUD-v0.1.0-1589853709",
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                        "",
                        true,
                        5500,
                        mapOf("attack" to 0.0),
                        mapOf("level" to 1L, "value" to 50L),
                        mapOf("Name" to "Goblin ear")
                    )
                )
            ), User(
                "cluo",
                5000,
                500,
                mutableListOf(
                    Character(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "Tiger",
                        1,
                        0,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        3000
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "Wooden sword",
                        1,
                        3.0,
                        100,
                        0,
                        listOf(),
                        5000
                    )
                ),
                0,
                mutableListOf(
                    Material(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k",
                        "Goblin ear",
                        1,
                        0.0,
                        50,
                        5500
                    )
                ),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            )
        ),

        MyTest(
            "Character died and weapon loss",
            User(
                "cluo",
                0,
                0,
                mutableListOf(
                    Character(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "Tiger",
                        1,
                        0,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        3000
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "Wooden sword",
                        1,
                        3.0,
                        100,
                        0,
                        listOf(),
                        5000
                    )
                ),
                0,
                mutableListOf(),
                ""
            ), Profile(
                TxPylonsEngine.Credentials("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"),
                mutableMapOf(),
                listOf(
                    Coin("loudcoin", 5000),
                    Coin("pylon", 500)
                ),
                listOf(
                )
            ), User(
                "cluo",
                5000,
                500,
                mutableListOf(
                ),
                -1,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            )
        ),
        MyTest(
            "Existing local account and empty profile (should not happen?)",
            User(
                "cluo",
                20000,
                500,
                mutableListOf(
                    Character(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
                        "Tiger",
                        1,
                        0,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        3000
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        "Wooden sword",
                        1,
                        3.0,
                        100,
                        0,
                        listOf(),
                        5000
                    )
                ),
                0,
                mutableListOf(
                    Material(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k",
                        "Goblin ear",
                        1,
                        0.0,
                        50,
                        5500
                    )
                ),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            ), Profile(
                TxPylonsEngine.Credentials("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"),
                mutableMapOf(),
                listOf(),
                listOf(
                )
            ), User(
                "cluo",
                0,
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
            )
        )
    )

    @Test
    fun syncIsCorrect() {
        tests.forEach {
            it.user.syncProfile(it.profile)
            assertEquals(it.test, it.expected, it.user)
        }
    }
}
