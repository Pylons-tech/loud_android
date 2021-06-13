package tech.pylons.loud.utils

import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import tech.pylons.loud.BuildConfig
import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.Multicore
import java.util.logging.Logger

/**
 * Object encapsulating some walletcore setup/config boilerplate.
 */
object CoreController {
    private val Log = Logger.getLogger(CoreController::class.java.name)

    private val config = Config(
        Backend.LIVE_DEV,
        listOf(BuildConfig.API_URL)
    ) // should list real ips for remote notes, ask mike for that
    private var userJson = ""

    /**
     * Prepares core to handle blockchain functionality.
     * Call setUserData first if user data exists.
     */
    fun bootstrapCore() {
        Multicore.enable(config)
//        Core.start(config, userJson)
    }

    /**
     * Serializes current user-determined core state (keys, etc.) as a JSON string.
     * Store this on the filesystem so you can retrieve it later.
     */
    @ExperimentalUnsignedTypes
    fun dumpUserData(): String = Core.current?.backupUserData().orEmpty()

    /**
     * Sets the JSON string we're going to pass to the core as an argument when we bootstrap it.
     * This is used to serialize users' keys, etc. between sessions; get it from FS and pass it in
     * here.
     */
    fun setUserData(json: String) {
        userJson = json
    }

    @ExperimentalUnsignedTypes
    fun getItemById(id: String): tech.pylons.lib.types.tx.item.Item? {
        val profile = Core.current?.getProfile()
        return profile?.items?.find { it.id == id }
    }
}