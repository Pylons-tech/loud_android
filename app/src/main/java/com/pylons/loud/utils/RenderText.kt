package com.pylons.loud.utils

import com.pylons.loud.R
import com.pylons.loud.models.User

object RenderText {
    fun getHomeDesc(player: User): Int {
        with (player) {
            if (pylonAmount == 0L) {
                return R.string.home_desc_without_pylon
            }

            if (activeCharacter == -1) {
                return R.string.home_desc_without_character
            } else {
//                val activeCharacter = getActiveCharacter()
//                if (activeCharacter != null) {
//                    if (activeCharacter.hp < activeCharacter.maxHP * .25) {
//                        return R.string.home_desc_with_low_hp
//                    }
//                }

                return R.string.home_desc
            }
        }
    }
}