package com.pylons.loud

import com.pylons.loud.constants.FightId
import com.pylons.loud.constants.FightId.ID_FIRE_DRAGON
import com.pylons.loud.constants.FightId.ID_ICE_DRAGON
import com.pylons.loud.constants.FightId.ID_UNDEAD_DRAGON
import com.pylons.loud.constants.FightRequirements
import com.pylons.loud.constants.FightRequirements.ICE_SPECIAL
import com.pylons.loud.constants.FightRequirements.IRON_SWORD
import com.pylons.loud.constants.FightRequirements.NO_SPECIAL
import com.pylons.loud.constants.Item
import com.pylons.loud.models.Character
import com.pylons.loud.models.fight.Fight
import com.pylons.loud.models.User
import com.pylons.loud.models.Weapon
import org.junit.Test
import org.junit.Assert.*

class FightTest {
    data class MyTest(val test: String, val user: User, val fight: Fight, val expected: Boolean) {
    }

    private val tests = listOf<MyTest>(
        MyTest(
            "rabbit no sword", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                -1,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_RABBIT,
                "",
                0,
                0,
                "",
                listOf(),
                listOf("1% chance of character dying")
            ), true
        ),
        MyTest(
            "giant user no iron sword", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.WOODEN_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_GIANT,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, NO_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),
        MyTest(
            "giant user with iron sword and no special character", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_GIANT,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, NO_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), true
        ),
        MyTest(
            "giant user with no weapon and no special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                ),
                -1,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_GIANT,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, NO_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),
        MyTest(
            "giant user with wooden sword and special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        1,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.WOODEN_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_GIANT,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, NO_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),
        MyTest(
            "giant user with iron sword and fire character", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        1,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_GIANT,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, NO_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),
        MyTest(
            "fire dragon user with iron sword and fire character", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        1,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                ID_FIRE_DRAGON,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, FightRequirements.FIRE_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), true
        ),
        MyTest(
            "fire dragon user with iron sword and no special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                ID_FIRE_DRAGON,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, FightRequirements.FIRE_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),
        MyTest(
            "ice dragon user with iron sword and ice special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        2,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                ID_ICE_DRAGON,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, ICE_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), true
        ),
        MyTest(
            "ice dragon user with iron sword and fire special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        1,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                ID_ICE_DRAGON,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, ICE_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),

        MyTest(
            "acid dragon user with iron sword and acid special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        3,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_ACID_DRAGON,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, FightRequirements.ACID_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), true
        ),

        MyTest(
            "acid dragon user with iron sword and ice special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        2,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                FightId.ID_ACID_DRAGON,
                "",
                100,
                10,
                "",
                listOf(IRON_SWORD, FightRequirements.ACID_SPECIAL),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),

        MyTest(
            "undead dragon user with angel sword and ice special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        2,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.ANGEL_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                ID_UNDEAD_DRAGON,
                "",
                100,
                10,
                "",
                listOf(FightRequirements.ANGEL_SWORD),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), true
        ),

        MyTest(
            "undead dragon user with iron sword and ice special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        2,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.IRON_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                ID_UNDEAD_DRAGON,
                "",
                100,
                10,
                "",
                listOf(FightRequirements.ANGEL_SWORD),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), false
        ),

        MyTest(
            "undead dragon user with angel sword and no special", User(
                "cluo",
                5000,
                0,
                50000,
                0,
                mutableListOf(
                    Character(
                        "001",
                        "Tiger",
                        1,
                        0.0,
                        0,
                        0,
                        1,
                        1.0,
                        0,
                        0,
                        0,
                        0,
                        ""
                    )
                ),
                0,
                mutableListOf(
                    Weapon(
                        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                        Item.ANGEL_SWORD,
                        1,
                        3.0,
                        0,
                        0,
                        listOf(),
                        5000,
                        ""
                    )
                ),
                0,
                mutableListOf(),
                "",
                mutableListOf()
            ), Fight(
                ID_UNDEAD_DRAGON,
                "",
                100,
                10,
                "",
                listOf(FightRequirements.ANGEL_SWORD),
                listOf("10% chance of sword lose", "GiantKiller badget on character")
            ), true
        )
    )


    @Test
    fun meetsRequirementsTest() {
        tests.forEach {
            assertEquals(it.test, it.expected, it.fight.meetsRequirements(it.user))
        }
    }
}