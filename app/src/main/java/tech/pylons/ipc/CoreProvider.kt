package tech.pylons.ipc

import tech.pylons.loud.services.WalletInitializer

class CoreProvider {
    companion object {
        fun getBlockStatusHeight(): Long {
            return WalletInitializer.getIpcConnection().getCoreData(0)?.toLong() ?: 0L
        }
    }
}