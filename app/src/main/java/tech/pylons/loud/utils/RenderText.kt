package tech.pylons.loud.utils

import tech.pylons.loud.R
import tech.pylons.loud.constants.FightId.ID_ACID_DRAGON
import tech.pylons.loud.constants.FightId.ID_FIRE_DRAGON
import tech.pylons.loud.constants.FightId.ID_GIANT
import tech.pylons.loud.constants.FightId.ID_GOBLIN
import tech.pylons.loud.constants.FightId.ID_ICE_DRAGON
import tech.pylons.loud.constants.FightId.ID_RABBIT
import tech.pylons.loud.constants.FightId.ID_TROLL
import tech.pylons.loud.constants.FightId.ID_UNDEAD_DRAGON
import tech.pylons.loud.constants.FightId.ID_WOLF
import tech.pylons.loud.models.User

object RenderText {
    fun getHomeDesc(player: User): Int {
        with(player) {
            if (pylonAmount == 0L) {
                return R.string.home_desc_without_pylon
            }

            if (activeCharacter == -1) {
                return R.string.home_desc_without_character
            } else {
                return R.string.home_desc
            }
        }
    }

    fun getFightIcon(id: Int): Int {
        return when (id) {
            ID_RABBIT -> R.string.rabbit_icon
            ID_GOBLIN -> R.string.goblin_icon
            ID_TROLL -> R.string.troll_icon
            ID_WOLF -> R.string.wolf_icon
            ID_GIANT -> R.string.giant_icon
            ID_FIRE_DRAGON -> R.string.fire_dragon_icon
            ID_ICE_DRAGON -> R.string.ice_dragon_icon
            ID_ACID_DRAGON -> R.string.acid_dragon_icon
            ID_UNDEAD_DRAGON -> R.string.undead_dragon_icon
            else -> R.string.character_icon
        }
    }
}