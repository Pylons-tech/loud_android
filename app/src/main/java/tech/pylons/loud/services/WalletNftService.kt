package tech.pylons.loud.services

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import tech.pylons.lib.types.Cookbook
import tech.pylons.lib.types.Profile
import tech.pylons.lib.types.Transaction
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.loud.BuildConfig
import tech.pylons.loud.utils.UI
import java.math.BigDecimal


class WalletNftService {

    companion object {
        var userProfile: Profile? = null
        var userCookbooks = mutableListOf<Cookbook>()
        var userCookbook: Cookbook? = null
        var userNfts = mutableListOf<Recipe>()
    }

    /**
     * CreateNft
     * @param context
     * @param Nft name
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
        royalty: String,
        quantity: Long,
        url: String,
        description: String,
        callback: (Boolean) -> Unit
    ) {
        //if cookbook not created, create cookbook
        if (userCookbook == null) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    if (context != null) {
                        Toast.makeText(
                            context,
                            "Portfolio not initiated",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                createAutoCookbook(context, userProfile)
            }
            callback(false)
            return
        }

        val NFT_id = name.replace(" ", "_") //NFT Item ID
        val NFT_recipe_description =
            "this recipe is to issue NFT: ${name} on NFT Cookbook: ${userCookbook?.name!!}"

        runBlocking {
            launch {
                //this creation is wrong
                WalletInitializer.getWallet().createRecipe(
                    name = name,
                    itemInputs = listOf(),          // this field is not necessary in NFT creation
                    cookbook = userCookbook?.id!!,  //NFT creator's Cookbook ID
                    description = NFT_recipe_description,      //NFT Recipe Description
                    blockInterval = 0,
                    coinInputs = listOf(
                        CoinInput(
                            coin = "pylon",
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
                                                upper = royalty.toBigDecimal()
                                                    .multiply(BigDecimal.valueOf(1000000000000000000))
                                                    .toString(), //"${royalty}000000000000000000",  //20%
                                                lower = royalty.toBigDecimal()
                                                    .multiply(BigDecimal.valueOf(1000000000000000000))
                                                    .toString(),
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
                    )
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
                                    val raw_log = transaction?.raw_log

                                    if (code != Transaction.ResponseCode.OK) {
                                        Toast.makeText(
                                            context,
                                            "Creating NFT failed: ${raw_log}",
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
                            val raw_log = transaction?.raw_log

                            if (code != Transaction.ResponseCode.OK) {
                                Toast.makeText(
                                    context,
                                    "Creating NFT failed: ${raw_log}",
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
                WalletInitializer.getWallet().fetchProfile(address) {
                    val profile = it
                    var ret = false
                    when (profile) {
                        null -> {
                            ret = false
                        }
                        else -> {
                            val address = profile.address
                            val items = profile.items
                            val coins = profile.coins
                            userProfile = profile
                            //liveUserData.postValue(profile)
//                            Account.setCurrentAccountUserName(context!!, profile.address)
                            WalletLiveData.setUserProfile(userProfile)
                            ret = true
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
            WalletInitializer.getWallet().listCookbooks { cb ->
                if (cb.isNotEmpty()) {
                    userCookbook = cb.find {
                        it.sender == userProfile?.address && it.name.startsWith(BuildConfig.APP_NAME)
                    }
                }
                WalletLiveData.setUserCookbook(userCookbook)

                callback?.invoke()
            }
        }
    }

    /*private fun onListCookbooks(context: Context?): (List<Cookbook>) -> Unit {
        return {
            //get all cookbooks
            if (it.isNotEmpty()) {
                userCookbooks.clear()
                it.forEach { one ->
                    if (one.sender == userProfile?.address) {
                        if (one.name.startsWith(BuildConfig.APP_NAME))
                            userCookbook = one

                        userCookbooks.add(one)
                    }
                }
            }

//            if (userCookbooks.isNotEmpty()) {
//                userCookbook = userCookbooks.get(0)
//            }

            if (userCookbook != null) {

                //retrieve NFT list
                listNfts(context)
            } else {
                //this means not cookbook created for this user
                //create cookbook
                createAutoCookbook(context, userProfile)
            }
        }
    }*/

    fun callCreateAutoCookbook(context: Context) {
        val loading = UI.displayLoading(context, "Creating AutoCookbook ...")

        CoroutineScope(Dispatchers.IO).launch {
            createAutoCookbook(
                context,
                userProfile
            ) { ret ->
                CoroutineScope(Dispatchers.IO).launch {
                    loading.dismiss()
                    when (ret) {
                        true -> {
                            //cookbook created
                            listCookbooks(
                                context
                            ) { }
                        }
                        false -> {
                            withContext(Dispatchers.Main) {
                                //cookbook creation failed
                                UI.displayConfirm(context,
                                    "Portfolio Creation Failed.\r\nWill you retry Portfolio Creation?",
                                    callbackOK = {
                                        callCreateAutoCookbook(context)
                                    },
                                    callbackCancel = {

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    fun buyPylons(context: Context?) {
        CoroutineScope(Dispatchers.IO).launch {
            //loading screen launch
            WalletInitializer.getWallet().buyPylons(onPylonsBought(context))
        }
    }

    private fun onPylonsBought(context: Context?): (Transaction?) -> Unit {
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
    private fun listNfts(context: Context?, callback: ((List<Recipe>) -> Unit)? = null) {
        // recipes
        runBlocking {
            launch {
                WalletInitializer.getWallet().listRecipesBySender {
                    val nfts = it
                    if (nfts.isNotEmpty()) {
                        userNfts.clear()
                        nfts.forEach {
                            if (it.sender == Companion.userProfile?.address)
                                userNfts.add(it)
                        }
                    }
                    if (callback != null) {
                        callback.invoke(userNfts.toList())
                    }
                }
            }
        }
    }

    fun onListNfts(context: Context?): (List<Recipe>) -> Unit {
        return {
            val nfts = it
            if (nfts.isNotEmpty()) {
                userNfts.clear()
                nfts.forEach {
                    if (it.sender == userProfile?.address)
                        userNfts.add(it)
                }
            }
        }
        // unreachable code
        /*Toast.makeText(context,
                "Create Portfolio failed",
                Toast.LENGTH_LONG).show()*/
    }

    private fun createAutoCookbook(
        context: Context?,
        profile: Profile?,
        callback: ((Boolean) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            var ret: Boolean
            var message: String
            WalletInitializer.getWallet().createAutoCookbook(
                profile!!,
                BuildConfig.APP_NAME
            ) {
                when (val transaction = it) {
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
                            message =
                                "Create Portfolio failed raw_log: ${transaction.raw_log}"
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
        return {

            when (val transaction = it) {
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
            if (userProfile == null) {
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
            WalletInitializer.getWallet()
                .executeRecipe(
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

    fun GetWebLink(recipeName: String, recipeId: String): String {
        return WalletInitializer.getWallet().generateWebLink(recipeName, recipeId)
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
            val cookbooks = ArrayList<Cookbook>()
            var cookbook: Cookbook? = null
            val recipes = ArrayList<Recipe>()

            // fetch profile
            runBlocking {
                launch {
                    WalletInitializer.getWallet().fetchProfile(null) {
                        profile = it
                    }
                }
            }

            if (profile == null) {
                return@launch
            }

            runBlocking {
                launch {
                    WalletInitializer.getWallet().listCookbooks {
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
                        WalletInitializer.getWallet()
                            .createAutoCookbook(profile!!, BuildConfig.APP_NAME) {
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
                    WalletInitializer.getWallet().listRecipes {
                        it.forEach {
                            recipes.add(it)
                        }
                    }
                }
            }

            var nft_recipe = recipes.find {
                it.name == "test NFT recipe"
            }

            if (nft_recipe == null) {
                //nft creation
                WalletInitializer.getWallet().createRecipe(
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
                    )
                ) {
                    val transaction = it

                }

                return@launch
            }

            //create NFT
            runBlocking {
                launch {
                    WalletInitializer.getWallet().executeRecipe(
                        nft_recipe.name,
                        nft_recipe.cookbookId,
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
                    WalletInitializer.getWallet().fetchProfile(null) {
                        profile = it
                    }
                }
            }
        }
    }
}
