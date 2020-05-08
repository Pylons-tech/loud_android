package com.pylons.loud.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pylons.loud.R
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    private val Log = Logger.getLogger(MainActivity::class.java.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_account), Context.MODE_PRIVATE)
        sharedPref.all.forEach {
            Log.info(it.toString())
        }

        val currentUsername = sharedPref.getString(getString(R.string.key_current_account), "");
        if (currentUsername.equals("")) {
            goToLogin()
            return
        }

        val playerJSON = sharedPref.getString(currentUsername, "");
        if (playerJSON.equals("")) {
            goToLogin()
            return
        }

        goToGameScreen()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToGameScreen() {
        val intent = Intent(this, GameScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

}
