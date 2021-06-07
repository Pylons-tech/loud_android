package tech.pylons.loud.models.fight

import tech.pylons.loud.constants.FightRequirements.ACID_SPECIAL
import tech.pylons.loud.constants.FightRequirements.ANGEL_SWORD
import tech.pylons.loud.constants.FightRequirements.FIRE_SPECIAL
import tech.pylons.loud.constants.FightRequirements.ICE_SPECIAL
import tech.pylons.loud.constants.FightRequirements.IRON_SWORD
import tech.pylons.loud.constants.FightRequirements.NO_SPECIAL
import tech.pylons.loud.constants.FightRequirements.SWORD
import tech.pylons.loud.models.User

open class Fight(
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
        val activeCharacter = player.getActiveCharacter()

        var isValid: Boolean
        requirements.forEach {
            isValid = when (it) {
                SWORD -> activeWeapon != null
                IRON_SWORD -> activeWeapon != null && activeWeapon.name == IRON_SWORD
                ANGEL_SWORD -> activeWeapon != null && activeWeapon.name == ANGEL_SWORD
                NO_SPECIAL -> activeCharacter != null && activeCharacter.special == NO_SPECIAL.toLong()
                FIRE_SPECIAL -> activeCharacter != null && activeCharacter.special == FIRE_SPECIAL.toLong()
                ICE_SPECIAL -> activeCharacter != null && activeCharacter.special == ICE_SPECIAL.toLong()
                ACID_SPECIAL -> activeCharacter != null && activeCharacter.special == ACID_SPECIAL.toLong()
                else -> false
            }

            if (!isValid) {
                return false
            }
        }

        return true
    }
}