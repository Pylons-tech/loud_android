package com.pylons.loud.models

import com.pylons.loud.constants.FightRequirements.IRON_SWORD
import com.pylons.loud.constants.FightRequirements.SWORD

class Fight(
    val id: Int,
    val name: String,
    val hp: Int,
    val attack: Int,
    val reward: String,
    val requirements: List<String>,
    val conditions: List<String>
) {

    fun meetsRequirements(player: User): Boolean {
        val activeWeapon = player.getActiveWeapon()

        var isValid: Boolean
        requirements.forEach {
            isValid = when (it) {
                SWORD -> activeWeapon != null
                IRON_SWORD -> activeWeapon != null && activeWeapon.name == IRON_SWORD
                else -> false
            }

            if (!isValid) {
                return false
            }
        }

        return true
    }
}