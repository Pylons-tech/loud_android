package com.pylons.loud.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pylons.loud.R
import com.pylons.loud.models.User
import com.pylons.loud.utils.CoreController
import com.pylons.wallet.core.Core
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.logging.Logger


class LoginActivity : AppCompatActivity() {
    private val Log = Logger.getLogger(LoginActivity::class.java.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_continue.setOnClickListener {
            val username = edit_text_username.text.toString()

            if (username == "") {
                Toast.makeText(this, R.string.login_no_username_text, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            button_continue.isEnabled = false
            layout_loading.visibility = View.VISIBLE
            setAccount(username)
        }
    }

    private fun setAccount(username: String) {
        val sharedPrefKeys = getSharedPreferences(
            getString(R.string.preference_file_account_keys), Context.MODE_PRIVATE
        )

        val playerKeys = sharedPrefKeys.getString(username, "");
        Log.info(playerKeys)
        if (playerKeys.equals("")) {
            // no keys get account keys
            CoroutineScope(IO).launch {
                val tx = Core.engine.registerNewProfile(username, null)
                tx.submit()
                Log.info(tx.toString())
                Log.info(tx.id)
                val playerKeys = CoreController.dumpUserData()
                Log.info(playerKeys)

                with(sharedPrefKeys.edit()) {
                    putString(username, playerKeys)
                    commit()
                    setupWithKeys(username, playerKeys)
                }
            }
        } else {
            if (playerKeys != null) {
                setupWithKeys(username, playerKeys)
            } else {
                //handle no keys
            }
        }
    }

    private fun setupWithKeys(username: String, playerKeys: String) {
        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_account), Context.MODE_PRIVATE
        )

        var currentPlayer = User(
            username,
            0,
            0,
            mutableListOf(),
            -1,
            mutableListOf(),
            -1,
            mutableListOf(),
            ""
        )

        CoreController.setUserData(playerKeys)

        val playerJSON = sharedPref.getString(username, "")
        if (!playerJSON.equals("")) {
            try {
                val moshi = Moshi.Builder().build()
                val jsonAdapter: JsonAdapter<User> =
                    moshi.adapter<User>(User::class.java)

                currentPlayer = jsonAdapter.fromJson(playerJSON)!!
            } catch (ex: Exception) {
                Log.warning(ex.toString())
            }
        }

        CoroutineScope(IO).launch {
            CoreController.bootstrapCore()
            var profile = Core.engine.getOwnBalances()
            Log.info(profile.toString())

            // I have keys but no account on chain
            // No coins mean no account on chain?
            // TODO("Remove this check, walletcore should handle this once done")
            if (profile != null && profile.coins.isEmpty()) {
                val tx = Core.engine.getPylons(500)
                tx.submit()
                delay(5000)
                profile = Core.engine.getOwnBalances()
            }

            if (currentPlayer != null && profile != null) {
                currentPlayer.syncProfile(profile)
                save(currentPlayer)
            }
        }
    }

    private fun save(player: User) {
        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_account), Context.MODE_PRIVATE
        )

        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<User> =
            moshi.adapter<User>(User::class.java)
        val json = jsonAdapter.toJson(player)

        with(sharedPref.edit()) {
            putString(player.name, json)
            putString(getString(R.string.key_current_account), player.name)
            commit()
            goToGame()
        }
    }

    private fun goToGame() {
        val intent = Intent(this, GameScreenActivity::class.java)
        startActivity(intent)
        finish()
    }
}
