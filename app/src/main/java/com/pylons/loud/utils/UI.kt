package com.pylons.loud.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.pylons.loud.R

object UI {
    fun displayLoading(context: Context, message: String): AlertDialog {
        with(context) {
            val dialogBuilder =
                AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage(
                message
            )
                .setCancelable(false)
                .setView(R.layout.view_loading)
            val alert = dialogBuilder.create()

            alert.show()

            return alert
        }
    }

    fun displayMessage(context: Context, message: String): AlertDialog {
        with(context) {
            val dialogBuilder =
                AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage(
                message
            )
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                }
            val alert = dialogBuilder.create()
            alert.show()

            return alert
        }
    }
}