package com.pylons.loud

import com.pylons.loud.constants.Item.GOBLIN_EAR
import com.pylons.loud.constants.Item.WOLF_TAIL
import com.pylons.loud.constants.Item.WOODEN_SWORD
import com.pylons.loud.models.Character
import com.pylons.loud.models.Material
import com.pylons.loud.models.User
import com.pylons.loud.models.Weapon
import org.junit.Test
import org.junit.Assert.*

class UserTest {

    private val user = User(
        "cluo",
        5000,
        500,
        mutableListOf(
            Character(
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c673a40ac-872e-4474-97cb-5250c400abff",
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
                0
            )
        ),
        -1,
        mutableListOf(
            Weapon(
                "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
                WOODEN_SWORD,
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
                GOBLIN_EAR,
                1,
                0.0,
                50,
                5500
            )
        ),
        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
    )

    @Test
    fun userGetItemIdByName() {
        assertEquals(
            "get item id by item name (material)",
            "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k",
            user.getItemIdByName(GOBLIN_EAR)
        )

        assertEquals(
            "get item id by item name (weapon)",
            "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1",
            user.getItemIdByName(WOODEN_SWORD)
        )

        assertEquals(
            "get item id by item name that does not exist",
            "",
            user.getItemIdByName(WOLF_TAIL)
        )
    }

    @Test
    fun userGetItemNameByItemId() {
        assertEquals(
            "get item name by item id (material)",
            GOBLIN_EAR,
            user.getItemNameByItemId("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2l3k")
        )

        assertEquals(
            "get item name by item id (weapon)",
            WOODEN_SWORD,
            user.getItemNameByItemId("cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28cd49e6431-3722-48d1-bf82-dd9aa0bc2ad1")
        )

        assertEquals(
            "get item name by item id that does not exist",
            "",
            user.getItemNameByItemId("doesnotexistid")
        )
    }
}