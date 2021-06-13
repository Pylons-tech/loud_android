package tech.pylons.loud.services

import android.content.Context
import android.content.pm.PackageManager
import androidx.startup.Initializer
import tech.pylons.ipc.IpcServiceConnection
import tech.pylons.lib.Wallet
import java.lang.ref.WeakReference

class WalletInitializer : Initializer<Wallet> {

    /**
     *
     * - Initialize AndroidWallet instance
     *
     * - Bind IPC service connection with Wallet-UI
     *
     */
    override fun create(context: Context): Wallet {
        if (instance == null) {
            instance = Wallet.android()
        }

        isWalletInitialized = true

        ipcServiceConnection = WeakReference(IpcServiceConnection(context))
        if (ifWalletExists(context)) {
            ipcServiceConnection!!.get()!!.bind() // do bind here
        }

        return instance as Wallet
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    companion object {
        var instance: Wallet? = null
        var isWalletInitialized: Boolean = false

        var ipcServiceConnection: WeakReference<IpcServiceConnection>? = null

        fun getWallet(): Wallet = instance!!

        fun getIpcConnection(): IpcServiceConnection = ipcServiceConnection!!.get()!!

        fun ifWalletExists(context: Context): Boolean = if (isWalletInitialized)
            try {
                val pm: PackageManager = context.packageManager
                pm.getPackageInfo("tech.pylons.wallet", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        else
            false
    }
}
