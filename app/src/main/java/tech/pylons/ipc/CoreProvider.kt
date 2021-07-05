package tech.pylons.ipc

import tech.pylons.lib.constants.ReservedKeys
import tech.pylons.loud.services.WalletInitializer

class CoreProvider {
    companion object {
        fun getBlockStatusHeight(): Long {
            return WalletInitializer.getIpcConnection().getCoreData(ReservedKeys.statusBlock)?.toLong() ?: 0L
        }
        fun getUserName(): String {
            return WalletInitializer.getIpcConnection().getCoreData(ReservedKeys.profileName) ?: ""
        }
    }
}