package tech.pylons.loud.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.pylons.loud.R
import tech.pylons.loud.utils.Account.getCurrentUser
import tech.pylons.loud.utils.Account.initAccount
import tech.pylons.loud.utils.CoreController
import tech.pylons.loud.utils.Preferences.setFriendAddress
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    private val Log = Logger.getLogger(MainActivity::class.java.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun initIntent() {
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        Log.info("action: $action")
        Log.info("data: $data")
        if (action == "android.intent.action.VIEW") {
            data?.let {
                val address = it.getQueryParameter("address")
                address?.let {
                    setFriendAddress(this, address)
                }
            }
        }
    }

    private fun init() {
        initIntent()

        val sharedPrefD =
            getSharedPreferences(getString(R.string.preference_file_default), Context.MODE_PRIVATE)
        sharedPrefD.all.forEach {
            Log.info(it.toString())
        }

        val sharedPref =
            getSharedPreferences(getString(R.string.preference_file_account), Context.MODE_PRIVATE)
        sharedPref.all.forEach {
            Log.info(it.toString())
        }

        val sharedPrefKeys = getSharedPreferences(
            getString(R.string.preference_file_account_keys), Context.MODE_PRIVATE
        )
        sharedPrefKeys.all.forEach {
            Log.info(it.toString())
        }

        CoreController.bootstrapCore() // should actually call setUserData first

        val user = getCurrentUser(this)
        if (user != null) {
            initAccount(this, user.name)
        } else {
            goToGame(this)
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToGame(context: Context) {
        with(context) {
            val intent = Intent(this, GameScreenActivity::class.java)
            startActivity(intent)
            (this as Activity).finish()
        }
    }
}
