package com.pylons.loud.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pylons.loud.R
import com.pylons.loud.pylons.services.WalletInitializer
import com.pylons.loud.utils.Account.initAccount
import com.pylons.loud.utils.UI.displayLoading
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.pylons.lib.types.tx.recipe.Recipe
import java.util.ArrayList
import kotlinx.coroutines.*
import tech.pylons.lib.types.Transaction
import tech.pylons.lib.types.tx.recipe.*


class LoginActivity : AppCompatActivity() {
    private var loading: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_continue.setOnClickListener {
            val username = edit_text_username.text.toString()
            if (username.isBlank()) {
                Toast.makeText(this, R.string.login_no_username_text, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loading = displayLoading(this, getString(R.string.loading_account, username))
            initAccount(this, username)
        }

        executeRecipe()
    }

    override fun onDestroy() {
        super.onDestroy()
        loading?.dismiss()
    }

    fun executeRecipe() {
        var recipes = ArrayList<Recipe>()
        try {
            runBlocking {
                launch(Dispatchers.IO) {
                    WalletInitializer.getWallet().listRecipes {
                        it.forEach {
                            recipes.add(it)
                        }
                    }
                }
            }
        }catch (e :Exception){
            Log.e("Exeption during execute executeRecipe method", e.toString())
        }finally {
            if (recipes.isEmpty()){
                Toast.makeText(this, R.string.recipe_not_exist, Toast.LENGTH_SHORT).show()
            }
        }

        try {
            var nftRecipe = recipes.find { it.name == "test NFT recipe" }
            if (nftRecipe != null) {
                runBlocking {
                    launch(Dispatchers.IO) {
                        WalletInitializer.getWallet().executeRecipe(nftRecipe.name, "Easel_autocookbook_cosmos1n5euj3rmtm3yvwc8afcjtfnprtt7y9xv2pmm4Q", listOf()) {
                                if (it?.code == Transaction.ResponseCode.OK)
                                    Log.i("Execute executeRecipe succes with code ", it.code.toString())
                            }
                       }
                }
            } else {
                Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Exeption during execute executeRecipe method", e.toString())
        }
    }
}
