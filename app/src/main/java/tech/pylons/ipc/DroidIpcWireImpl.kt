package tech.pylons.ipc

import android.content.Context
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.pylons.loud.services.WalletNftService
import tech.pylons.loud.BuildConfig
import tech.pylons.loud.services.WalletInitializer

/**
 * Test Wallet wrapper
 * only refer the internal logic
 */
class DroidIpcWireImpl : DroidIpcWire() {

    companion object {
        private const val TAG: String = "Pylons/Easel"

        init {
            implementation = DroidIpcWireImpl()
            Log.i(TAG, "DroidIpcWireImpl has just been instantiated.")
        }

        /**
         * initWallet
         * call when IpcService initiated. IpcService::onServiceConnected()
         * all ipc actions will be come after initWallet() succeed
         */
        fun initWallet(context: Context?) {
            runBlocking {
                launch {
                    establishConnection(BuildConfig.APP_NAME, BuildConfig.APPLICATION_ID) {
                        if (it) { // only if handshake is succeeded
                            println("Wallet Initiated")
                            WalletNftService().initUserInfo(context)
                        }
                    }
                }
            }
        }
    }

    override fun readString(): String? {
        return WalletInitializer.getIpcConnection().getFromWallet()
    }

    override fun writeString(s: String) {
        WalletInitializer.getIpcConnection().submitToWallet(s)
    }

}