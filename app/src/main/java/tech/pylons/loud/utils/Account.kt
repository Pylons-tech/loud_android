package tech.pylons.loud.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.models.User
import tech.pylons.wallet.core.Core
import tech.pylons.lib.types.Profile
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.logging.Logger

object Account {
    private val Log = Logger.getLogger(Account::class.java.name)

    @ExperimentalUnsignedTypes
    fun initAccount(context: Context, username: String) {
        val playerKeys = getPlayerKeys(context, username)
        if (playerKeys != null) {
            setupWithKeys(context, username, playerKeys)
        } else {
            // no keys get account keys
            CoroutineScope(Dispatchers.IO).launch {
                val tx = Core.current?.engine?.registerNewProfile(username, null)
                tx?.submit()
                Log.info(tx?.toString())
                //     Log.info(tx.id)
                delay(5000)
                Core.current?.getProfile()

                val tx2 = Core.current?.getPylons(500)
                tx2?.submit()
                delay(5000)
                val playerKeys = CoreController.dumpUserData()

                Log.info(playerKeys)
                savePlayerKeys(context, username, playerKeys)
                setupWithKeys(context, username, playerKeys)
            }
        }
    }

    private fun getPlayerKeys(context: Context, username: String): String? {
        with(context) {
            val sharedPrefKeys = getSharedPreferences(
                getString(R.string.preference_file_account_keys), Context.MODE_PRIVATE
            )
            val playerKeys = sharedPrefKeys.getString(username, "");
            Log.info(playerKeys)
            return if (playerKeys.equals("")) {
                null
            } else {
                playerKeys
            }
        }
    }

    private fun savePlayerKeys(context: Context, username: String, playerKeys: String) {
        with(context) {
            val sharedPrefKeys = getSharedPreferences(
                getString(R.string.preference_file_account_keys), Context.MODE_PRIVATE
            )
            with(sharedPrefKeys.edit()) {
                putString(username, playerKeys)
                commit()
            }
        }
    }

    private fun getUser(context: Context, username: String): User? {
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_account), Context.MODE_PRIVATE
            )

            val playerJSON = sharedPref.getString(username, "")
            if (!playerJSON.equals("")) {
                try {
                    val moshi = Moshi.Builder().build()
                    val jsonAdapter: JsonAdapter<User> =
                        moshi.adapter(User::class.java)

                    return jsonAdapter.fromJson(playerJSON)!!
                } catch (ex: Exception) {
                    Log.warning(ex.toString())
                }
            }

            return null
        }
    }

    @ExperimentalUnsignedTypes
    private fun createAccount(username:String): Profile? {
        //add new core to Multicore

        val tx = Core.current?.newProfile(username, null)
        //Core.engine.getOwnBalances()

        val tx2 = Core.current?.getPylons(500)
        return Core.current?.getProfile()
    }

    @ExperimentalUnsignedTypes
    private fun setupWithKeys(context: Context, username: String, playerKeys: String) {
        CoreController.setUserData(playerKeys)

        val currentPlayer = getUser(context, username) ?: User(
            username,
            0,
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

        CoroutineScope(Dispatchers.IO).launch {
//            CoreController.bootstrapCore()
            var profile = Core.current?.getProfile()
            Log.info(profile.toString())

            // I have keys but no account on chain
            // TODO("Remove this check, walletcore should handle this once done")
            if (profile == null) {
                profile = createAccount(username)
            }

            if (profile != null) {
                currentPlayer.syncProfile(profile)
                currentPlayer.saveSync(context)
                setCurrentAccountUserName(context, currentPlayer.name)
                goToGame(context)
            } else {
                // delay not enough? rerun.
                setupWithKeys(context, username, playerKeys)
            }
        }
    }

    fun setCurrentAccountUserName(context: Context, username: String) {
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_default), Context.MODE_PRIVATE
            )

            with(sharedPref.edit()) {
                putString(getString(tech.pylons.loud.R.string.key_current_account), username)
                commit()
            }
        }
    }

    fun goToGame(context: Context) {
        with(context) {
            val intent = Intent(this, GameScreenActivity::class.java)
            startActivity(intent)
            (this as Activity).finish()
        }
    }

    private fun getCurrentAccountUserName(context: Context): String? {
        with(context) {
            val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_default), Context.MODE_PRIVATE
            )

            val currentUsername = sharedPref.getString(getString(R.string.key_current_account), "");

            if (currentUsername.equals("")) {
                return null
            }
            return currentUsername
        }
    }

    fun getCurrentUser(context: Context): User? {
        val username = getCurrentAccountUserName(context) ?: return null
        return getUser(context, username)
    }
}