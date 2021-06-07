package tech.pylons.loud.utils

import android.content.Context
import tech.pylons.loud.R
import kotlinx.coroutines.withContext

object Preferences {

    fun getFriendAddress(context: Context): String? {
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_default), Context.MODE_PRIVATE
            )

            val friendAddress = sharedPref.getString(getString(R.string.key_friend_address), "");

            if (friendAddress.equals("")) {
                return null
            }
            return friendAddress
        }
    }

    fun setFriendAddress(context: Context, address: String) {
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_default), Context.MODE_PRIVATE
            )

            with(sharedPref.edit()) {
                putString(getString(R.string.key_friend_address), address)
                commit()
            }
        }
    }

    fun deleteFriendAddress(context: Context) {
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_default), Context.MODE_PRIVATE
            )

            with(sharedPref.edit()) {
                remove(getString(R.string.key_friend_address))
                apply()
            }
        }
    }
}