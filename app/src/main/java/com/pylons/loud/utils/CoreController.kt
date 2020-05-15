package com.pylons.loud.utils

import com.pylons.loud.BuildConfig
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Config
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
        Core.start(config, userJson)
    }

    /**
     * Serializes current user-determined core state (keys, etc.) as a JSON string.
     * Store this on the filesystem so you can retrieve it later.
     */
    fun dumpUserData(): String = Core.backupUserData().orEmpty()

    /**
     * Sets the JSON string we're going to pass to the core as an argument when we bootstrap it.
     * This is used to serialize users' keys, etc. between sessions; get it from FS and pass it in
     * here.
     */
    fun setUserData(json: String) {
        userJson = json
    }
}