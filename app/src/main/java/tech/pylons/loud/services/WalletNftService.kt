package tech.pylons.loud.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import tech.pylons.loud.BuildConfig
import tech.pylons.lib.types.Cookbook
import tech.pylons.lib.types.Profile
import tech.pylons.lib.types.Transaction
import tech.pylons.lib.types.tx.recipe.*
import java.math.BigDecimal


class WalletNftService {

    companion object{
        var userProfile:Profile? = null
        var userCookbooks = mutableListOf<Cookbook>()
        var userCookbook:Cookbook? = null
        var userNfts = mutableListOf<Recipe>()
        var appName = BuildConfig.APP_NAME
        var appPkgName = BuildConfig.APPLICATION_ID
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
        context:Context,
        name: String,
        price: String,
        royalty: String,
        quantity: Long,
        url: String,
        description: String,
        callback: (Boolean)->Unit
    ) {
        //if cookbook not created, create cookbook
        if (userCookbook == null) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main){
                    if (context != null) {
                        Toast.makeText(context,
                            "Portfolio not initiated",
                            Toast.LENGTH_LONG).show()

                    }
                }

                createAutoCookbook(context, userProfile)
            }
            callback(false)
            return
        }

        val NFT_id = name.replace(" ", "_") //NFT Item ID
        val NFT_recipe_description = "this recipe is to issue NFT: ${name} on NFT Cookbook: ${userCookbook?.name!!}"



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
                                        key="Residual%", //this should be reserved keyword for NFT
                                        program = "",
                                        rate = BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(1000000000000000000)).toString(), //  "1000000000000000000", //"1.0",
                                        weightRanges = listOf(
                                            DoubleWeightRange(
                                                upper= royalty.toBigDecimal().multiply(BigDecimal.valueOf(1000000000000000000)).toString(), //"${royalty}000000000000000000",  //20%
                                                lower = royalty.toBigDecimal().multiply(BigDecimal.valueOf(1000000000000000000)).toString(),
                                                weight = 1
                                            )
                                        )
                                    )
                                ),
                                longs = listOf(
                                    //NFT Quantity
                                    //Pls confirm if this is the right place for NFT Quantity defintion
                                    LongParam(
                                        key="Quantity",
                                        program = "",
                                        rate = BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(1000000000000000000)).toString(), //"1.0",
                                        weightRanges = listOf(
                                            LongWeightRange(
                                                upper=quantity.toString() , //quantity 10 copies
                                                lower=quantity.toString(),
                                                weight = 1
                                            )
                                        )
                                    )
                                ),
                                strings = listOf(
                                    //pls confirm this field
                                    StringParam(
                                        rate = BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(1000000000000000000)).toString(), // "1.0"
                                        key = "Name",
                                        value = name,
                                        program = ""
                                    ),
                                    StringParam(
                                        rate = BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(1000000000000000000)).toString(), // "1.0"
                                        key = "NFT_URL",
                                        value = url,
                                        program = ""
                                    ),
                                    StringParam(
                                        rate = BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(1000000000000000000)).toString(), // "1.0"
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
                ){
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
            when(it){
                null->{
                    //if this is correct logic?
                    CoroutineScope(Dispatchers.IO).launch{
                        withContext(Dispatchers.Main){
                            Toast.makeText(
                                context,
                                "Creating Nft Cancelled!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                else->{
                    //retrieve NFT list
                    listNfts(context)

                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {

                            //this part should be considered
                            val id = transaction?.id
                            val code = transaction?.code
                            val raw_log = transaction?.raw_log

                            if(code != Transaction.ResponseCode.OK){
                                Toast.makeText(context,
                                    "Creating NFT failed: ${raw_log}",
                                    Toast.LENGTH_LONG).show()
                                return@withContext
                            }
                            else{
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
     */
    fun fetchProfile(context: Context?, address: String?) {
        runBlocking {
            launch {
                WalletInitializer.getWallet().fetchProfile(address){
                    val profile = it
                    when(profile) {
                        null->{}
                        else->{
                            val address = profile.address
                            val items = profile.items
                            val coins = profile.coins
                            userProfile = profile

                            //retrieve cookbook list
                            //listNfts(context)
                            listCookbooks(context)
                        }
                    }
                }
            }
        }
    }


    /**
     * Wallet API listCookbooks
     * Cookbook naming rule: {appName}_autocookbook_{profileaddress}
     */
    fun listCookbooks(context: Context?){
        runBlocking {
            launch {
                WalletInitializer.getWallet().listCookbooks(onListCookbooks(context))
            }
        }
    }

    fun onListCookbooks(context:Context?): (List<Cookbook>)->Unit {
        return {
            //get all cookbooks
            val cookbooks = it

            if (cookbooks.isNotEmpty()) {
                userCookbooks.clear()
                cookbooks.forEach {
                    if (it.sender == userProfile?.address)
                        userCookbooks.add(it)
                }
            }

            //get LOUD Cookbook
            //second cookbook creation always fails wtf.
            //val LOUD_cookbook_name = "${appName}_autocookbook_${userProfile?.address}"
            //Companion.userCookbook = Companion.userCookbooks.find{
            //    it.name == LOUD_cookbook_name
            //}
            if (userCookbooks.isNotEmpty()) {
                userCookbook = userCookbooks.get(0)
            }

            if (userCookbook != null) {
                //retrieve NFT list
                listNfts(context)
            } else {
                //this means not cookbook created for this user
                //create cookbook
                createAutoCookbook(context, userProfile)
            }
        }
    }

    fun buyPylons(context: Context?) {
        CoroutineScope(Dispatchers.IO).launch {
            //loading screen launch
            WalletInitializer.getWallet().buyPylons(onBuyPylons(context))
        }
    }

    fun onBuyPylons(context:Context?):(Transaction?)->Unit {
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
    fun listNfts(context:Context?){
        // recipes
        runBlocking {
            launch {
                WalletInitializer.getWallet().listRecipes(onListNfts(context))

            }

        }
    }

    fun onListNfts(context:Context?): (List<Recipe>)->Unit {
        return {
            val nfts = it
            if (nfts.isNotEmpty()) {
                userNfts.clear()
                nfts.forEach {
                    if(it.sender == userProfile?.address)
                        userNfts.add(it)
                }
            }
        }
        // unreachable code
        /*Toast.makeText(context,
                "Create Portfolio failed",
                Toast.LENGTH_LONG).show()*/
    }

    fun createAutoCookbook(context:Context?, profile:Profile?) {
        runBlocking {
            launch {
                WalletInitializer.getWallet().createAutoCookbook(profile!!, BuildConfig.APP_NAME, onCreateAutoCookbook(context))
            }
        }
    }

    fun onCreateAutoCookbook(context:Context?) : (Transaction?)->Unit{
        return {

            val transaction = it
            when (transaction) {
                null -> {
                    //reject or exception handling
                    if(context != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context,
                                    "Create Portfolio failed",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                else -> {
                    if(transaction.code == Transaction.ResponseCode.OK){
                        //transaction ok
                        //retrieve NFT list
                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context,
                                    "Create Portfolio Success",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                        listCookbooks(context)
                    }
                    else
                    {
                        //transaction failed

                        if(context != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context,
                                        "Create Portfolio failed raw_log: ${transaction.raw_log}",
                                        Toast.LENGTH_LONG).show()
                                }

                            }
                        }

                    }
                }
            }
        }
    }


    fun initUserInfo(context: Context?){
        CoroutineScope(Dispatchers.IO).launch {
            if(userProfile == null){
                //testCreateNft(context)
                fetchProfile(context, null)

            }
        }
    }

    fun executeRecipe(recipe: String, cookbook:String, itemInputs: List<String>, context: Context) {

        CoroutineScope(Dispatchers.IO).launch {
            WalletInitializer.getWallet().executeRecipe(recipe, cookbook, itemInputs, callback=onExecuteRecipe(context))
        }
    }

    fun onExecuteRecipe(context: Context): (Transaction?)->Unit {
        return {

        }
    }

    fun GetWebLink(recipeName:String, recipeId: String): String {
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
    fun testCreateNft(context: Context?){

        CoroutineScope(Dispatchers.IO).launch {
            var profile: Profile? = null
            var cookbooks = ArrayList<Cookbook>()
            var cookbook: Cookbook? = null
            var recipes = ArrayList<Recipe>()

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
                        WalletInitializer.getWallet().createAutoCookbook(profile!!, BuildConfig.APP_NAME) {
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
                                        key="Residual%", //this should be reserved keyword for NFT
                                        program = "",
                                        rate = "1.0",
                                        weightRanges = listOf(
                                            DoubleWeightRange(
                                                upper="20", //20%
                                                lower = "20",
                                                weight = 1
                                            )
                                        )
                                    )
                                ),
                                longs = listOf(
                                    LongParam(
                                        key="Quantity",
                                        program = "",
                                        rate = "1.0",
                                        weightRanges = listOf(
                                            LongWeightRange(
                                                upper=BigDecimal.valueOf(10).setScale(18).toString(), //quantity 10 copies
                                                lower=BigDecimal.valueOf(10).setScale(18).toString(),
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
                    ){
                        if(it?.code == Transaction.ResponseCode.OK) {

                        }
                    }
                }
            }

            //check
            runBlocking {
                launch {
                    WalletInitializer.getWallet().fetchProfile(null){
                        profile = it
                    }
                }
            }
        }
    }
}
