package com.pylons.loud

import com.pylons.loud.constants.Coin
import com.pylons.loud.constants.Item
import com.pylons.loud.constants.Item.GOBLIN_EAR
import com.pylons.loud.constants.Item.IRON_SWORD
import com.pylons.loud.constants.Item.WOLF_TAIL
import com.pylons.loud.constants.Item.WOODEN_SWORD
import com.pylons.loud.models.Character
import com.pylons.loud.models.Material
import com.pylons.loud.models.User
import com.pylons.loud.models.Weapon
import com.pylons.loud.models.trade.*
import org.junit.Assert.assertEquals
import org.junit.Test

class TradeTest {

    private val user = User(
        "user",
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
                Item.WOODEN_SWORD,
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
                Item.GOBLIN_EAR,
                1,
                0.0,
                50,
                5500
            )
        ),
        "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c"
    )

    @Test()
    fun canFulFillTradeBuyLOUD() {
        assertEquals(
            true,
            user.canFulfillTrade(
                LoudTrade(
                    "",
                    CoinInput(Coin.LOUD, 100),
                    CoinOutput(Coin.PYLON, 10),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun canFulFillTradeSellLOUD() {
        assertEquals(
            true,
            user.canFulfillTrade(
                LoudTrade(
                    "",
                    CoinInput(Coin.PYLON, 100),
                    CoinOutput(Coin.LOUD, 10),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeBuyLOUD() {
        assertEquals(
            false,
            user.canFulfillTrade(
                LoudTrade(
                    "",
                    CoinInput(Coin.LOUD, 5001),
                    CoinOutput(Coin.PYLON, 10),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeSellLOUD() {
        assertEquals(
            false,
            user.canFulfillTrade(
                LoudTrade(
                    "",
                    CoinInput(Coin.PYLON, 501),
                    CoinOutput(Coin.LOUD, 10),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun canFulFillTradeSellItem() {
        assertEquals(
            true,
            user.canFulfillTrade(
                SellItemTrade(
                    "",
                    CoinInput(Coin.PYLON, 500),
                    ItemOutput(WOODEN_SWORD, 1),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeSellItem() {
        assertEquals(
            false,
            user.canFulfillTrade(
                SellItemTrade(
                    "",
                    CoinInput(Coin.PYLON, 501),
                    ItemOutput(WOODEN_SWORD, 1),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun canFulFillTradeBuyItemCharacter() {
        assertEquals(
            true,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    CharacterSpec("Tiger", Spec(1, 1), Spec(1.0, 10000.0), 0),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeBuyItemCharacter() {
        assertEquals(
            false,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    CharacterSpec("Tiger", Spec(2, 2), Spec(1.0, 10000.0), 0),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeBuyItemCharacter2() {
        assertEquals(
            false,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    CharacterSpec("Tiger", Spec(1, 1), Spec(2.0, 10000.0), 0),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeBuyItemCharacter3() {
        assertEquals(
            false,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    CharacterSpec("Tiger", Spec(1, 1), Spec(1.0, 10000.0), 1),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun canFulFillTradeBuyItemWeapon() {
        assertEquals(
            true,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    WeaponSpec(WOODEN_SWORD, Spec(1, 1), Spec(10, 10)),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeBuyItemWeapon() {
        assertEquals(
            false,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    WeaponSpec(WOODEN_SWORD, Spec(2, 2), Spec(10, 10)),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeBuyItemWeapon2() {
        assertEquals(
            false,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    WeaponSpec(IRON_SWORD, Spec(2, 2), Spec(10, 10)),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun canFulFillTradeBuyItemMaterial() {
        assertEquals(
            true,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    MaterialSpec(GOBLIN_EAR, Spec(1, 1)),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }

    @Test()
    fun cannotFulFillTradeBuyItemMaterial() {
        assertEquals(
            false,
            user.canFulfillTrade(
                BuyItemTrade(
                    "",
                    MaterialSpec(WOLF_TAIL, Spec(1, 1)),
                    CoinOutput(Coin.PYLON, 100),
                    false,
                    ""
                )
            )
        )
    }
}