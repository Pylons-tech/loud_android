package tech.pylons.loud

import tech.pylons.loud.models.Character
import tech.pylons.loud.models.User
import tech.pylons.loud.utils.RenderText.getHomeDesc
import org.junit.Test

import org.junit.Assert.*

class HomeDesc {
    private val playerDefault = User(
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
    )

    private val playerNoCharacter = User(
        "cluo",
        5000,
        0,
        50000,
        0,
        mutableListOf(),
        -1,
        mutableListOf(),
        -1,
        mutableListOf(),
        "",
        mutableListOf()
    )

    private val playerNoPylon = User(
        "cluo",
        5000,
        0,
        0,
        0,
        mutableListOf(),
        -1,
        mutableListOf(),
        -1,
        mutableListOf(),
        "",
        mutableListOf()
    )

    private val playerNoPylon2 = User(
        "cluo",
        5000,
        0,
        0,
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
    )

    private val playerNoPylon3 = User(
        "cluo",
        5000,
        0,
        0,
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
        -1,
        mutableListOf(),
        -1,
        mutableListOf(),
        "",
        mutableListOf()
    )

    @Test
    fun homeDescNoCharacter() {
        assertEquals(R.string.home_desc_without_character, getHomeDesc(playerNoCharacter))
    }

    @Test
    fun homeDescNoPylon() {
        assertEquals(R.string.home_desc_without_pylon, getHomeDesc(playerNoPylon))
        assertEquals(R.string.home_desc_without_pylon, getHomeDesc(playerNoPylon2))
        assertEquals(R.string.home_desc_without_pylon, getHomeDesc(playerNoPylon3))
    }

    @Test
    fun homeDescDefault() {
        assertEquals(R.string.home_desc, getHomeDesc(playerDefault))
    }
}