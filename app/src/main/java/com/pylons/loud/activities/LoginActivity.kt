package com.pylons.loud.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pylons.loud.R
import com.pylons.loud.utils.Account.initAccount
import com.pylons.loud.utils.UI.displayLoading
import kotlinx.android.synthetic.main.activity_login.*
import java.util.logging.Logger

class LoginActivity : AppCompatActivity() {
    private val Log = Logger.getLogger(LoginActivity::class.java.name)
    private var loading: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_continue.setOnClickListener {
            val username = edit_text_username.text.toString()

            if (username == "") {
                Toast.makeText(this, R.string.login_no_username_text, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loading = displayLoading(this, getString(R.string.loading_account, username))
            initAccount(this, username)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loading?.dismiss()
    }
}
