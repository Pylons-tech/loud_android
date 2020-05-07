package com.pylons.loud.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pylons.loud.R
import com.pylons.loud.models.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_login.*
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

            setAccount(username)
        }
    }

    private fun setAccount(username: String) {
        val player = User(
            username,
            5000,
            50000,
            mutableListOf(),
            null,
            mutableListOf(),
            null
        )

        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<User> =
            moshi.adapter<User>(User::class.java)

        val json = jsonAdapter.toJson(player)
        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_account), Context.MODE_PRIVATE)

        with (sharedPref.edit()) {
            putString(username, json)
            putString(getString(R.string.key_current_account), json)
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
