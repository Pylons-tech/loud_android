package tech.pylons.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.startup.Initializer
import kotlinx.coroutines.*
import tech.pylons.lib.Wallet
import tech.pylons.lib.constants.ReservedKeys
import tech.pylons.lib.types.Cookbook
import tech.pylons.lib.types.Profile
import tech.pylons.lib.types.Transaction
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.loud.BuildConfig
import java.lang.ref.WeakReference
import java.math.BigDecimal

class WalletHandler : Initializer<Wallet> {

    companion object {
        private const val appName = BuildConfig.APP_NAME
        private const val appPkgName = BuildConfig.APPLICATION_ID
        private const val TAG: String = "Pylons/$appName"

        /**
         * The wallet object which is instantiated by this initializer when the app starts.
         */
        private var wallet: Wallet? = null

        /**
         * A weak reference to the instance of IPC service connection with the Wallet-UI.
         */
        private var walletConnection: WeakReference<IpcServiceConnection>? = null

        /**
         * A mutable live data of the Cookbook for this client app.
         */
        private var userCookbook = MutableLiveData<Cookbook?>()

        /**
         * A mutable live data of the wallet user's profile.
         */
        private var userProfile = MutableLiveData<Profile?>()

        /**
         * A mutable list of all created NFTs.
         */
        var userNfts = mutableListOf<Recipe>()


        fun getWallet(): Wallet {
            return wallet!!
        }

        fun getWalletConnection(): IpcServiceConnection {
            return walletConnection!!.get()!!
        }

        fun ifWalletExists(context: Context): Boolean = try {
            val pm: PackageManager = context.packageManager
            pm.getPackageInfo("tech.pylons.wallet", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        fun getLiveUserCookbook(): LiveData<Cookbook?> = userCookbook

        fun getUserCookbook(): Cookbook? {
            return userCookbook.value
        }

        fun setUserCookbook(cookbook: Cookbook?) {
            userCookbook.postValue(cookbook)
        }

        fun getLiveUserProfile(): LiveData<Profile?> = userProfile

        fun getUserProfile(): Profile? {
            return userProfile.value
        }

        fun setUserProfile(profile: Profile?) {
            userProfile.postValue(profile)
        }


        fun getBlockStatusHeight(): Long {
            return getWalletConnection().getCoreData(ReservedKeys.statusBlock)?.toLong() ?: 0L
        }

        fun getUserName(): String {
            return getWalletConnection().getCoreData(ReservedKeys.profileName) ?: ""
        }

        /**
         * CreateNft
         * @param context
         * @param name
         * @param price
         * @param royalty
         * @param quantity
         * @param url
         * @param description
         * @param royalty
         * @param quantity
         * @param url
         * @param description
         */
        fun createNft(
            context: Context,
            name: String,
            price: String,
            currency: String,
            royalty: String,
            quantity: Long,
            url: String,
            description: String,
            callback: (Boolean) -> Unit
        ) {
            //if cookbook not created, create cookbook
            if (getUserCookbook() == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Portfolio not initiated",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    createAutoCookbook(context, getUserProfile())
                }
                callback(false)
                return
            }

            val NFT_id = name.replace(" ", "_") //NFT Item ID
            //val NFT_recipe_description = "this recipe is to issue NFT: $name on NFT Cookbook: ${userCookbook?.name!!}"

            runBlocking {
                launch {
                    //this creation is wrong
                    getWallet().createRecipe(
                        name = name,
                        itemInputs = listOf(),          // this field is not necessary in NFT creation
                        cookbook = getUserCookbook()?.id!!,  //NFT creator's Cookbook ID
                        description = description,      //NFT_recipe_description,      //NFT Recipe Description
                        blockInterval = 0,
                        coinInputs = listOf(
                            CoinInput(
                                coin = currency, //"pylon, usd",
                                count = price.toLong()  //NFT Price
                            )
                        ), // NFT price definition

                        outputTable = EntriesList(
                            coinOutputs = listOf(),         //in NFT creation, coinOutput is inavailable
                            itemModifyOutputs = listOf(),   //in NFT creation, itemModifyOutputs is unecessary
                            itemOutputs = listOf(           //NFT definition
                                //NFT description
                                ItemOutput(
                                    id = NFT_id,            //NFT Item ID made with NFT Name
                                    doubles = listOf(
                                        //Residual% definition
                                        //Pls confirm if this is the right place for Residual defintion
                                        DoubleParam(
                                            key = "Residual%", //this should be reserved keyword for NFT
                                            program = "",
                                            rate = BigDecimal.valueOf(1)
                                                .multiply(BigDecimal.valueOf(1000000000000000000))
                                                .toString(), //  "1000000000000000000", //"1.0",
                                            weightRanges = listOf(
                                                DoubleWeightRange(
                                                    upper = royalty.toBigDecimal().multiply(
                                                        BigDecimal.valueOf(1000000000000000000)
                                                    )
                                                        .toString(), //"${royalty}000000000000000000",  //20%
                                                    lower = royalty.toBigDecimal().multiply(
                                                        BigDecimal.valueOf(1000000000000000000)
                                                    ).toString(),
                                                    weight = 1
                                                )
                                            )
                                        )
                                    ),
                                    longs = listOf(
                                        //NFT Quantity
                                        //Pls confirm if this is the right place for NFT Quantity defintion
                                        LongParam(
                                            key = "Quantity",
                                            program = "",
                                            rate = BigDecimal.valueOf(1)
                                                .multiply(BigDecimal.valueOf(1000000000000000000))
                                                .toString(), //"1.0",
                                            weightRanges = listOf(
                                                LongWeightRange(
                                                    upper = quantity.toString(), //quantity 10 copies
                                                    lower = quantity.toString(),
                                                    weight = 1
                                                )
                                            )
                                        )
                                    ),
                                    strings = listOf(
                                        //pls confirm this field
                                        StringParam(
                                            rate = BigDecimal.valueOf(1)
                                                .multiply(BigDecimal.valueOf(1000000000000000000))
                                                .toString(), // "1.0"
                                            key = "Name",
                                            value = name,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = BigDecimal.valueOf(1)
                                                .multiply(BigDecimal.valueOf(1000000000000000000))
                                                .toString(), // "1.0"
                                            key = "NFT_URL",
                                            value = url,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = BigDecimal.valueOf(1)
                                                .multiply(BigDecimal.valueOf(1000000000000000000))
                                                .toString(), // "1.0"
                                            key = "Description",
                                            value = description,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = BigDecimal.valueOf(1)
                                                .multiply(BigDecimal.valueOf(1000000000000000000))
                                                .toString(), // "1.0"
                                            key = "Currency",
                                            value = currency,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = BigDecimal.valueOf(1)
                                                .multiply(BigDecimal.valueOf(1000000000000000000))
                                                .toString(), // "1.0"
                                            key = "Price",
                                            value = price,
                                            program = ""
                                        )
                                    ),
                                    transferFee = 0 //transfer Fee should be defined in NFT creation, currently set to 0
                                ) // NFT entry
                            )
                        ),
                        outputs = listOf(
                            WeightedOutput(
                                entryIds = listOf(
                                    NFT_id
                                ),
                                weight = "1"
                            )
                        ),
                        extraInfo = listOf()
                    ) {
                        val transaction = it
                        var ret = false
                        Log.i("onNftRetrieved()", "Response from Wallet: $it")
                        CoroutineScope(Dispatchers.IO).launch {
                            when (it) {
                                null -> {
                                    //if this is correct logic?
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Creating Nft Cancelled!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                                else -> {
                                    //retrieve NFT list
                                    listNfts(context)
                                    withContext(Dispatchers.Main) {

                                        //this part should be considered
                                        val id = transaction?.id
                                        val code = transaction?.code
                                        val rawLog = transaction?.raw_log

                                        if (code != Transaction.ResponseCode.OK) {
                                            Toast.makeText(
                                                context,
                                                "Creating NFT failed: $rawLog",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@withContext
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Successfully Created Nft!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            ret = true
                                            return@withContext
                                        }
                                    }
                                }
                            }

                            callback(ret)
                        }
                    }
                }
            }


        }


        private fun onNftRetrieved(context: Context?): (Transaction?) -> Unit {
            return {
                val transaction = it
                Log.i("onNftRetrieved()", "Response from Wallet: $it")
                when (it) {
                    null -> {
                        //if this is correct logic?
                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Creating Nft Cancelled!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    else -> {
                        //retrieve NFT list
                        listNfts(context)

                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {

                                //this part should be considered
                                val id = transaction?.id
                                val code = transaction?.code
                                val rawLog = transaction?.raw_log

                                if (code != Transaction.ResponseCode.OK) {
                                    Toast.makeText(
                                        context,
                                        "Creating NFT failed: $rawLog",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@withContext
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Successfully Created Nft!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@withContext
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         *  Wallet API fetchProfile
         *  @param context
         *  @param address if null, default address
         *  @callback Boolean true if profile exists, else false
         */
        fun fetchProfile(context: Context?, address: String?, callback: (Boolean) -> Unit) {
            runBlocking {
                launch {
                    getWallet().fetchProfile(address) { profile ->
                        val ret: Boolean = when (profile) {
                            null -> {
                                false
                            }
                            else -> {
                                val address = profile.address
                                val items = profile.items
                                val coins = profile.coins
                                //liveUserData.postValue(profile)
                                setUserProfile(profile)
                                true
                            }
                        }

                        callback.invoke(ret)
                    }
                }
            }
        }


        /**
         * Wallet API listCookbooks
         * Cookbook naming rule: {appName}_autocookbook_{profileaddress}
         */
        fun listCookbooks(context: Context?, callback: (() -> Unit)? = null) {
            CoroutineScope(Dispatchers.IO).launch {
                getWallet().listCookbooks {

                    val cookbooks = it

                    if (cookbooks.isNotEmpty()) {
                        setUserCookbook(cookbooks.find { cb ->
                            cb.sender == getUserProfile()?.address && cb.id.startsWith(
                                appName,
                                true
                            )
                        })
                    } else {
                        setUserCookbook(null)
                    }

                    callback?.invoke()
                }
            }
        }
        /*
        fun onListCookbooks(context:Context?): (List<Cookbook>)->Unit {
            return {
                //get all cookbooks
                val cookbooks = it

                if (cookbooks.isNotEmpty()) {
                    Companion.userCookbooks.clear()
                    cookbooks.forEach {
                        if (it.sender == Companion.userProfile?.address)
                            Companion.userCookbooks.add(it)
                    }
                }

                //get Easel Cookbook
                //second cookbook creation always fails wtf.
                //val Easel_cookbook_name = "${appName}_autocookbook_${userProfile?.address}"
                //Companion.userCookbook = Companion.userCookbooks.find{
                //    it.name == Easel_cookbook_name
                //}
                if (Companion.userCookbooks.isNotEmpty()) {
                    Companion.userCookbook = Companion.userCookbooks.get(0)
                }

                if (Companion.userCookbook != null) {
                    //retrieve NFT list
                    listNfts(context)
                } else {
                    //this means not cookbook created for this user
                    //create cookbook
                    createAutoCookbook(context, Companion.userProfile)
                }
            }
        }
         */

        fun buyPylons(context: Context?) {
            CoroutineScope(Dispatchers.IO).launch {
                //loading screen launch
                getWallet().buyPylons(onBuyPylons(context))
            }
        }

        fun onBuyPylons(context: Context?): (Transaction?) -> Unit {
            //loading screen dismiss
            return {
                val transaction = it
                if (transaction != null) {

                }
            }
        }

        /**
         * Wallet API listRecipes
         */
        fun listNfts(context: Context?, callback: ((List<Recipe>) -> Unit)? = null) {
            // recipes
            runBlocking {
                launch {
                    getWallet().listRecipesBySender { nfts ->
                        if (nfts.isNotEmpty()) {
                            userNfts.clear()
                            nfts.forEach { rcp ->
                                if (rcp.sender == getUserProfile()?.address)
                                    userNfts.add(rcp)
                            }
                        }

                        callback?.invoke(userNfts.toList())
                    }

                }

            }
        }

        fun onListNfts(context: Context?): (List<Recipe>) -> Unit {
            return { nfts ->
                if (nfts.isNotEmpty()) {
                    userNfts.clear()
                    nfts.forEach { rcp ->
                        if (rcp.sender == getUserProfile()?.address)
                            userNfts.add(rcp)
                    }
                }
            }
        }

        fun createAutoCookbook(
            context: Context?,
            profile: Profile?,
            callback: ((Boolean) -> Unit)? = null
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                var ret: Boolean
                var message: String

                getWallet().createAutoCookbook(
                    profile!!,
                    appName
                ) { transaction ->
                    when (transaction) {
                        null -> {
                            message = "Create portfolio failed"
                            ret = false
                        }
                        else -> {
                            if (transaction.code == Transaction.ResponseCode.OK) {
                                message = "Create Portfolio Success"
                                ret = true
                            } else {
                                //transaction failed
                                message = "Create Portfolio failed raw_log: ${transaction.raw_log}"
                                ret = false
                            }
                        }
                    }
                    //reject or exception handling
                    CoroutineScope(Dispatchers.IO).launch {

                        withContext(Dispatchers.Main) {
                            if (context != null) {
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            callback?.invoke(ret)
                        }
                    }
                }
            }
        }

        fun onCreateAutoCookbook(context: Context?): (Transaction?) -> Unit {
            return { transaction ->
                when (transaction) {
                    null -> {
                        //reject or exception handling
                        if (context != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Create Portfolio failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    else -> {
                        if (transaction.code == Transaction.ResponseCode.OK) {
                            //transaction ok
                            //retrieve NFT list
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Create Portfolio Success",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            listCookbooks(context)
                        } else {
                            //transaction failed
                            if (context != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Create Portfolio failed raw_log: ${transaction.raw_log}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        fun initUserInfo(context: Context?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (getUserProfile() == null) {
                    //testCreateNft(context)
                    fetchProfile(context, null) {
                        when (it) {
                            true -> {
                                //retrieved profile
                            }
                            false -> {
                                //no profile
                            }
                        }
                    }

                }
            }
        }

        fun executeRecipe(
            recipe: String,
            cookbook: String,
            itemInputs: List<String>,
            context: Context
        ) {

            CoroutineScope(Dispatchers.IO).launch {
                getWallet().executeRecipe(
                    recipe,
                    cookbook,
                    itemInputs,
                    callback = onExecuteRecipe(context)
                )
            }
        }

        fun onExecuteRecipe(context: Context): (Transaction?) -> Unit {
            return {

            }
        }

        fun getWebLink(recipeName: String, recipeId: String): String {
            return getWallet().generateWebLink(recipeName, recipeId)
        }

        /**
         * testCreateNft flow
         * 1. fetch Wallet Profile
         * 2. list Creator Cookbooks, if cookbook is null, createAutoCookbook
         * 3. list Recipes for Creator Cookbook
         * 4. Create test NFT Recipe
         * 5. Execute test NFT Recipe
         */
        fun testCreateNft(context: Context?) {

            CoroutineScope(Dispatchers.IO).launch {
                var profile: Profile? = null
                var cookbooks = ArrayList<Cookbook>()
                var cookbook: Cookbook? = null
                var recipes = ArrayList<Recipe>()

                // fetch profile
                runBlocking {
                    launch {
                        getWallet().fetchProfile(null) {
                            profile = it
                        }
                    }
                }

                if (profile == null) {
                    return@launch
                }

                runBlocking {
                    launch {
                        getWallet().listCookbooks {
                            it.forEach {
                                //my cookbook
                                if (it.sender == profile?.address)
                                    cookbooks.add(it)
                            }
                        }
                    }
                }

                if (cookbooks.isEmpty()) {
                    //account has not cookbook
                    //create cookbook
                    runBlocking {
                        launch {
                            getWallet().createAutoCookbook(
                                profile!!,
                                appName
                            ) {
                                if (it != null) {

                                }
                            }
                        }
                    }
                }

                if (cookbooks.isEmpty()) {
                    //something wrong with cookbook creation
                    return@launch
                }

                cookbook = cookbooks.find {
                    it.sender == profile?.address
                }
                if (cookbook == null) {
                    //not yet created cookbook
                    return@launch
                }


                runBlocking {
                    launch {
                        getWallet().listRecipes {
                            it.forEach {
                                recipes.add(it)
                            }
                        }
                    }
                }

                val nftRecipe = recipes.find {
                    it.name == "test NFT recipe"
                }

                if (nftRecipe == null) {
                    //nft creation
                    getWallet().createRecipe(
                        name = "test NFT recipe",
                        itemInputs = listOf(), // this field is not necessary in NFT creation
                        cookbook = cookbook.id,
                        description = "test recipe description for nft image",
                        blockInterval = 0,
                        coinInputs = listOf(
                            CoinInput(
                                coin = "pylon",
                                count = 100
                            )
                        ), // NFT price
                        outputTable = EntriesList(
                            coinOutputs = listOf(),
                            itemModifyOutputs = listOf(),
                            itemOutputs = listOf(
                                //NFT description
                                ItemOutput(
                                    id = "test_NFT_v1",
                                    doubles = listOf(

                                        DoubleParam(
                                            key = "Residual%", //this should be reserved keyword for NFT
                                            program = "",
                                            rate = "1.0",
                                            weightRanges = listOf(
                                                DoubleWeightRange(
                                                    upper = "20", //20%
                                                    lower = "20",
                                                    weight = 1
                                                )
                                            )
                                        )
                                    ),
                                    longs = listOf(
                                        LongParam(
                                            key = "Quantity",
                                            program = "",
                                            rate = "1.0",
                                            weightRanges = listOf(
                                                LongWeightRange(
                                                    upper = BigDecimal.valueOf(10).setScale(18)
                                                        .toString(), //quantity 10 copies
                                                    lower = BigDecimal.valueOf(10).setScale(18)
                                                        .toString(),
                                                    weight = 1
                                                )
                                            )
                                        )
                                    ),
                                    strings = listOf(
                                        //pls confirm this field
                                        StringParam(
                                            rate = "1.0",
                                            key = "Name",
                                            value = "NFT",
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1.0",
                                            key = "NFT_URL",
                                            value = "http://127.0.0.1/test_nft.html",
                                            program = ""
                                        )
                                    ),
                                    transferFee = 0
                                ) // NFT entry
                            )
                        ),
                        outputs = listOf(
                            WeightedOutput(
                                entryIds = listOf(
                                    "test_NFT_v1"
                                ),
                                weight = "1"
                            )
                        ),
                        extraInfo = listOf()
                    ) {
                        val transaction = it

                    }

                    return@launch
                }

                //create NFT
                runBlocking {
                    launch {
                        getWallet().executeRecipe(
                            nftRecipe.name,
                            nftRecipe.cookbookId,
                            listOf()
                        ) {
                            if (it?.code == Transaction.ResponseCode.OK) {

                            }
                        }
                    }
                }

                //check
                runBlocking {
                    launch {
                        getWallet().fetchProfile(null) {
                            profile = it
                        }
                    }
                }
            }
        }
    }

    /**
     *  Entry point to instantiate the Wallet Connection and then returns the Wallet instance.
     *
     * - Initialize AndroidWallet instance
     * - Bind IPC service connection with the Wallet-UI
     */
    override fun create(context: Context): Wallet {
        if (wallet == null) {
            wallet = Wallet.android()
        }

        walletConnection = WeakReference(IpcServiceConnection(context))
        if (ifWalletExists(context)) {
            walletConnection!!.get()!!.bind() // do bind here
        } else {
            Toast.makeText(context, "Wallet not installed.", Toast.LENGTH_LONG).show()
        }

        return wallet as Wallet
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    /**
     * <IpcServiceConnection>
     * interface for monitoring Wallet's IPCService
     *
     * - iIpcService: IPC interface instance
     *
     * - bind() / unbind()
     *
     * - getFromWallet() / submitToWallet()
     */
    class IpcServiceConnection(ctx: Context) : ServiceConnection {

        private var isServiceBinded: Boolean = false

        private var context: Context? = ctx
        private var iIpcService: IIpcInterface? = null

        /**
         * Returns a message sent from the Wallet-UI
         */
        fun getFromWallet(): String? {
            return if (isServiceBinded) {
                val msg = iIpcService!!.wallet2client()
                println("getFromWallet $msg")
                msg
            } else {
                null
            }
        }

        /**
         * Do send a message to the Wallet-UI
         */
        fun submitToWallet(json: String) {
            if (isServiceBinded)
                iIpcService!!.client2wallet(json)
        }

        /**
         * Get data directly from Wallet-UI's core instance, without going through libpylons
         */
        fun getCoreData(whatToGet: String): String? {
            return iIpcService!!.getCoreData(whatToGet)
        }

        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            isServiceBinded = true
            iIpcService = IIpcInterface.Stub.asInterface(service)
            DroidIpcWireImpl.initWallet(context) // handshake will be initiated immediately after ipc channel is established ...
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            iIpcService = null
            isServiceBinded = false
        }

        fun bind() {
            Log.d(TAG, "Bind")
            val serviceIntent = Intent("tech.pylons.wallet.ipc.BIND")
            serviceIntent.setPackage("tech.pylons.wallet")
            context!!.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
        }

        fun unbind() {
            if (iIpcService != null) {
                Log.d(TAG, "Unbind")
                context!!.unbindService(this)
                iIpcService = null
            }
        }
    }

    class DroidIpcWireImpl : DroidIpcWire() {

        companion object {

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
                                initUserInfo(context)
                            }
                        }
                    }
                }
            }
        }

        override fun readString(): String? {
            return getWalletConnection().getFromWallet()
        }

        override fun writeString(s: String) {
            getWalletConnection().submitToWallet(s)
        }

    }
}
