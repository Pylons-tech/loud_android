package com.pylons.loud.activities

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
        val intent = Intent(this, GameScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

}
