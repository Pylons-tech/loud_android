package tech.pylons.loud.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.Purchase
import com.google.android.material.bottomsheet.BottomSheetDialog
import tech.pylons.lib.types.tx.trade.TradeItemInput
import kotlinx.android.synthetic.main.bottom_sheet_friend.view.*
import kotlinx.android.synthetic.main.content_game_screen.*
import kotlinx.android.synthetic.main.dialog_input_text.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import tech.pylons.lib.types.Transaction
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.recipe.CoinInput
import tech.pylons.lib.types.tx.recipe.Recipe
import tech.pylons.loud.R
import tech.pylons.loud.constants.FightId.ID_ACID_GIANT
import tech.pylons.loud.constants.FightId.ID_FIRE_GIANT
import tech.pylons.loud.constants.FightId.ID_GIANT
import tech.pylons.loud.constants.FightId.ID_ICE_GIANT
import tech.pylons.loud.constants.FightId.ID_RABBIT
import tech.pylons.loud.constants.FightRequirements.ACID_SPECIAL
import tech.pylons.loud.constants.FightRequirements.FIRE_SPECIAL
import tech.pylons.loud.constants.FightRequirements.ICE_SPECIAL
import tech.pylons.loud.constants.FightRequirements.NO_SPECIAL
import tech.pylons.loud.constants.Item.COPPER_SWORD
import tech.pylons.loud.constants.Item.DROP_DRAGONACID
import tech.pylons.loud.constants.Item.DROP_DRAGONFIRE
import tech.pylons.loud.constants.Item.DROP_DRAGONICE
import tech.pylons.loud.constants.Item.GOBLIN_EAR
import tech.pylons.loud.constants.Item.TROLL_TOES
import tech.pylons.loud.constants.Item.WOLF_TAIL
import tech.pylons.loud.constants.Item.WOODEN_SWORD
import tech.pylons.loud.constants.ItemID.ID_ANGEL_SWORD
import tech.pylons.loud.constants.ItemID.ID_BRONZE_SWORD
import tech.pylons.loud.constants.ItemID.ID_COPPER_SWORD
import tech.pylons.loud.constants.ItemID.ID_IRON_SWORD
import tech.pylons.loud.constants.ItemID.ID_SILVER_SWORD
import tech.pylons.loud.constants.ItemID.ID_WOODEN_SWORD
import tech.pylons.loud.constants.Location.FOREST
import tech.pylons.loud.constants.Location.FRIENDS
import tech.pylons.loud.constants.Location.HOME
import tech.pylons.loud.constants.Location.INVENTORY
import tech.pylons.loud.constants.Location.PYLONS_CENTRAL
import tech.pylons.loud.constants.Location.SETTINGS
import tech.pylons.loud.constants.Location.SHOP
import tech.pylons.loud.constants.Recipe.RCP_BUY_ANGEL_SWORD
import tech.pylons.loud.constants.Recipe.RCP_BUY_BRONZE_SWORD
import tech.pylons.loud.constants.Recipe.RCP_BUY_CHARACTER
import tech.pylons.loud.constants.Recipe.RCP_BUY_COPPER_SWORD
import tech.pylons.loud.constants.Recipe.RCP_BUY_GOLD_WITH_PYLON
import tech.pylons.loud.constants.Recipe.RCP_BUY_IRON_SWORD
import tech.pylons.loud.constants.Recipe.RCP_BUY_SILVER_SWORD
import tech.pylons.loud.constants.Recipe.RCP_BUY_WOODEN_SWORD
import tech.pylons.loud.constants.Recipe.RCP_COPPER_SWORD_UPGRADE
import tech.pylons.loud.constants.Recipe.RCP_GET_TEST_ITEMS
import tech.pylons.loud.constants.Recipe.RCP_SELL_SWORD
import tech.pylons.loud.constants.Recipe.RCP_WOODEN_SWORD_UPGRADE
import tech.pylons.loud.fragments.lists.character.CharacterFragment
import tech.pylons.loud.fragments.lists.fight.FightFragment
import tech.pylons.loud.fragments.lists.friend.FriendFragment
import tech.pylons.loud.fragments.lists.item.ItemFragment
import tech.pylons.loud.fragments.lists.itemspec.ItemSpecFragment
import tech.pylons.loud.fragments.lists.trade.TradeFragment
import tech.pylons.loud.fragments.screens.forest.ForestFightPreviewFragment
import tech.pylons.loud.fragments.screens.pyloncentral.CreateTradeFragment
import tech.pylons.loud.fragments.screens.pyloncentral.PylonCentralHomeFragment
import tech.pylons.loud.fragments.screens.pyloncentral.purchasepylon.PurchasePylonFragment
import tech.pylons.loud.fragments.screens.senditem.SendItemConfirmFragment
import tech.pylons.loud.fragments.screens.senditem.SendItemViewModel
import tech.pylons.loud.fragments.screens.setting.SettingsScreenFragment
import tech.pylons.loud.fragments.ui.BottomNavigationFragment
import tech.pylons.loud.fragments.ui.PlayerStatusFragment
import tech.pylons.loud.fragments.ui.blockchainstatus.BlockChainStatusViewModel
import tech.pylons.loud.localdb.LocalDb
import tech.pylons.loud.models.*
import tech.pylons.loud.models.fight.Fight
import tech.pylons.loud.models.trade.*
import tech.pylons.loud.services.WalletInitializer
import tech.pylons.loud.utils.Account.getCurrentUser
import tech.pylons.loud.utils.CoreController.getItemById
import tech.pylons.loud.utils.Preferences.getFriendAddress
import tech.pylons.loud.utils.RenderText.getFightIcon
import tech.pylons.loud.utils.UI.displayLoading
import tech.pylons.loud.utils.UI.displayMessage
import tech.pylons.wallet.core.Core
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.fixedRateTimer

class GameScreenActivity : AppCompatActivity(),
    BottomNavigationFragment.OnFragmentInteractionListener,
    FightFragment.OnListFragmentInteractionListener,
    ItemFragment.OnListFragmentInteractionListener,
    CharacterFragment.OnListFragmentInteractionListener,
    ForestFightPreviewFragment.OnFragmentInteractionListener,
    PylonCentralHomeFragment.OnFragmentInteractionListener,
    SettingsScreenFragment.OnFragmentInteractionListener,
    TradeFragment.OnListFragmentInteractionListener,
    CreateTradeFragment.OnFragmentInteractionListener,
    ItemSpecFragment.OnListFragmentInteractionListener,
    FriendFragment.OnListFragmentInteractionListener,
    SendItemConfirmFragment.OnFragmentInteractionListener,
    PurchasePylonFragment.OnFragmentInteractionListener,
    PlayerStatusFragment.OnFragmentInteractionListener {
    private val Log = Logger.getLogger(GameScreenActivity::class.java.name)

    class SharedViewModel : ViewModel() {
        private val player = MutableLiveData<User>()
        private val playerLocation = MutableLiveData<Int>()
        lateinit var fightPreview: Fight
        var shopAction = 0
        private val tradeInput = MutableLiveData<ItemSpec>()
        private val tradeOutput = MutableLiveData<tech.pylons.lib.types.tx.item.Item>()
        lateinit var trade: Trade
        lateinit var tradeBuyMatchingItems: List<Item>

        fun getPlayer(): MutableLiveData<User> {
            return player
        }

        fun setPlayer(user: User) {
            player.value = user
        }

        fun getPlayerLocation(): MutableLiveData<Int> {
            return playerLocation
        }

        fun setPlayerLocation(location: Int) {
            playerLocation.value = location
        }

        fun getTradeInput(): MutableLiveData<ItemSpec> {
            return tradeInput
        }

        fun setTradeInput(item: ItemSpec?) {
            tradeInput.value = item
        }

        fun getTradeOutput(): MutableLiveData<tech.pylons.lib.types.tx.item.Item> {
            return tradeOutput
        }

        fun setTradeOutput(item: tech.pylons.lib.types.tx.item.Item?) {
            tradeOutput.value = item
        }
    }

    private val model: SharedViewModel by viewModels()
    private val sendItemViewModel: SendItemViewModel by viewModels()
    private val blockChainStatusViewModel: BlockChainStatusViewModel by viewModels()
    private lateinit var getStatusBlockTimer: Timer
    private lateinit var localCacheClient: LocalDb

    private val cookbookId = "Cookbook"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        val currentPlayer = getCurrentUser(this)
        if (currentPlayer != null) {
            model.setPlayer(currentPlayer)

            val friendAddress = getFriendAddress(this)
            friendAddress?.let {
                onNavigation(FRIENDS)
            }

            localCacheClient = LocalDb.getInstance(this)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }

    override fun onPause() {
        Log.info("onPause")
        super.onPause()
        cancelTimer()
    }

    override fun onResume() {
        Log.info("onResume")
        super.onResume()
        initTimer()
    }

    private fun initTimer() {
        getStatusBlockTimer = fixedRateTimer("getStatusBlock", false, 0, 5000) {
            CoroutineScope(IO).launch {
                blockChainStatusViewModel.getStatusBlock()
            }
        }
    }

    private fun cancelTimer() {
        if (this::getStatusBlockTimer.isInitialized) {
            getStatusBlockTimer.cancel()
        }
    }

    override fun onFight(fight: Fight) {
        model.getPlayer().value?.let {
            if (fight.meetsRequirements(it)) {
                model.fightPreview = fight
                val frag =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
                    .navigate(R.id.forestFightPreviewFragment)
            } else {
                var prompt = "Need ${fight.requirements.joinToString(", ")}"
                prompt = prompt.replace(NO_SPECIAL, "non-special character")
                prompt = prompt.replace(FIRE_SPECIAL, "fire character")
                prompt = prompt.replace(ICE_SPECIAL, "ice character")
                prompt = prompt.replace(ACID_SPECIAL, "acid character")
                Toast.makeText(
                    this,
                    prompt,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onNavigation(id: Int) {
        nav_host_fragment.findNavController().popBackStack()
        when (id) {
            HOME -> {
                nav_host_fragment.findNavController().navigate(R.id.homeScreenFragment)
            }
            FOREST -> {
                if (model.getPlayer().value?.activeCharacter == -1) {
                    Toast.makeText(
                        this,
                        R.string.you_cant_go_to_forest_without_character, Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                nav_host_fragment.findNavController().navigate(R.id.forestScreenFragment)
            }
            SHOP -> {
                nav_host_fragment.findNavController().navigate(R.id.shopScreenFragment)
            }
            PYLONS_CENTRAL -> {
                nav_host_fragment.findNavController().navigate(R.id.pylonCentralFragment)
            }
            FRIENDS -> {
                nav_host_fragment.findNavController().navigate(R.id.friendsScreenFragment)
            }
            SETTINGS -> {
                nav_host_fragment.findNavController().navigate(R.id.settingsScreenFragment)
            }
            INVENTORY -> {
                nav_host_fragment.findNavController().navigate(R.id.inventoryFragment)
            }
            else -> {
                Log.warning("Not exist")
            }
        }
    }

    override fun onItemSelect(item: Item) {
        if (item.lockedTo.isNotBlank()) {
            displayMessage(this, getString(R.string.item_is_locked, item.lockedTo))
            return
        }

        val name = item.name
        val player = model.getPlayer().value

        when (item) {
            is Character -> onCharacter(item)
            is Weapon -> if (player != null) {
                var prompt = "Set $name as active weapon?"
                if (player.getActiveWeapon() == item) {
                    prompt = "Unset $name as active weapon?"
                }
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage(prompt)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                        if (player.getActiveWeapon() == item) {
                            player.activeWeapon = -1
                        } else {
                            player.setActiveWeapon(item)
                        }
                        model.setPlayer(player)
                        player.saveAsync(this)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.show()
            }
        }

    }

    override fun onItemBuy(item: Item) {
        val name = item.name
        val price = (item as Weapon).price
        val goldIcon = getString(R.string.gold_icon)
        val player = model.getPlayer().value ?: return
        var recipes = ArrayList<Recipe>()

        Log.info(item.toString())

        if (player.unlockedGold < item.price) {
            Toast.makeText(
                this@GameScreenActivity,
                getString(R.string.you_dont_have_enough_gold),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        var prompt = "Buy $name for $goldIcon $price"
        if (item.preItem.isNotEmpty()) {
            val preItems = item.preItem.joinToString(", ")
            prompt += " and $preItems"
        }
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("$prompt?")
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val itemIds = mutableListOf<String>()
                var recipeId = ""

                when (item.id) {
                    ID_WOODEN_SWORD -> {
                        recipeId = RCP_BUY_WOODEN_SWORD
                    }
                    ID_COPPER_SWORD -> {
                        recipeId = RCP_BUY_COPPER_SWORD
                    }
                    ID_SILVER_SWORD -> {
                        recipeId = RCP_BUY_SILVER_SWORD
                        itemIds.add(player.getItemIdByName(GOBLIN_EAR))
                    }
                    ID_BRONZE_SWORD -> {
                        recipeId = RCP_BUY_BRONZE_SWORD
                        itemIds.add(player.getItemIdByName(WOLF_TAIL))
                    }
                    ID_IRON_SWORD -> {
                        recipeId = RCP_BUY_IRON_SWORD
                        itemIds.add(player.getItemIdByName(TROLL_TOES))
                    }
                    ID_ANGEL_SWORD -> {
                        recipeId = RCP_BUY_ANGEL_SWORD
                        itemIds.add(player.getItemIdByName(DROP_DRAGONFIRE))
                        itemIds.add(player.getItemIdByName(DROP_DRAGONICE))
                        itemIds.add(player.getItemIdByName(DROP_DRAGONACID))
                    }
                }

                if (itemIds.contains("")) {
                    Toast.makeText(
                        this@GameScreenActivity,
                        getString(R.string.you_dont_have_enough_resources),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
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

                var nft_recipe = recipes.find { it.name == "test NFT recipe" }

                //create NFT
                runBlocking {
                    launch {
                        WalletInitializer.getWallet().executeRecipe(
                            nft_recipe!!.name,
                            nft_recipe!!.cookbookId,
                            listOf()
                        ){
                            if(it?.code == tech.pylons.lib.types.Transaction.ResponseCode.OK) {

                            }
                        }
                    }
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.show()
    }

    @ExperimentalUnsignedTypes
    override fun onItemSell(item: Item) {
        if (item.lockedTo.isNotBlank()) {
            displayMessage(this, getString(R.string.item_is_locked, item.lockedTo))
            return
        }

        val player = model.getPlayer().value
        if (player != null) {
            val name = item.name
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Sell $name for ${getString(R.string.gold_icon)} ${item.getSellPriceRange()}?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->

                    val loading = displayLoading(
                        this,
                        getString(
                            R.string.loading_sell_shop_item,
                            item.name,
                            "${getString(R.string.gold_icon)} ${item.getSellPriceRange()}"
                        )
                    )

                    CoroutineScope(IO).launch {
                        val tx = txFlow {
                            Core.current?.applyRecipe(RCP_SELL_SWORD, cookbookId, listOf(item.id))!!
                        }

                        withContext(Main) {
                            val message = if (tx.code == Transaction.ResponseCode.OK) {
                                var amount = 0L

                                val output = tx.txData.output
                                if (output.isNotEmpty()) {
                                    amount = output[0].amount
                                }

                                getString(R.string.you_sold_item_for_gold, name, amount)
                            } else {
                                tx.raw_log
                            }

                            loading.dismiss()
                            displayMessage(
                                this@GameScreenActivity,
                                message
                            )
                            tx.id?.let { blockChainStatusViewModel.setTx(it) }
                        }
                    }
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }
    }

    @ExperimentalUnsignedTypes
    override fun onItemUpgrade(item: Item) {
        if (item.lockedTo.isNotBlank()) {
            displayMessage(this, getString(R.string.item_is_locked, item.lockedTo))
            return
        }

        val name = item.name
        val player = model.getPlayer().value

        if (item is Weapon && player != null) {
            if (player.unlockedGold > item.getUpgradePrice()) {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Upgrade $name?")
                    .setCancelable(false)
                    .setPositiveButton("Upgrade") { _, _ ->
                        val recipeId = when (item.name) {
                            WOODEN_SWORD -> RCP_WOODEN_SWORD_UPGRADE
                            COPPER_SWORD -> RCP_COPPER_SWORD_UPGRADE
                            else -> ""
                        }

                        val loading =
                            displayLoading(
                                this,
                                getString(R.string.loading_upgrade_shop_item, item.name)
                            )
                        CoroutineScope(IO).launch {
                            val tx = txFlow {
                                Core.current?.applyRecipe(recipeId, cookbookId, listOf(item.id))!!
                            }

                            withContext(Main) {
                                val message = if (tx.code == Transaction.ResponseCode.OK) {
                                    getString(R.string.you_have_upgraded_item, name)
                                } else {
                                    tx.raw_log
                                }

                                loading.dismiss()
                                displayMessage(
                                    this@GameScreenActivity,
                                    message
                                )
                                tx.id?.let { blockChainStatusViewModel.setTx(it) }
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.show()
            } else {
                displayMessage(this, getString(R.string.you_dont_have_enough_gold_to_upgrade, name))
            }
        }
    }

    override fun onCharacter(item: Character) {
        val name = item.name
        val player = model.getPlayer().value
        if (player != null) {
            var prompt = "Set ${name} as active character?"
            if (player.getActiveCharacter() == item) {
                prompt = "Unset $name as active character?"
            }
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(prompt)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    if (player.getActiveCharacter() == item) {
                        player.activeCharacter = -1
                    } else {
                        player.setActiveCharacter(item)
                    }
                    model.setPlayer(player)
                    player.saveAsync(this)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }
    }

    @ExperimentalUnsignedTypes
    private suspend fun txFlow(func: () -> Transaction): Transaction {
        val tx = func()
        tx.submit()
        Log.info(tx.toString())

        if (tx.state == Transaction.State.TX_REFUSED) {
            return tx
        }

        // TODO("Remove delay, walletcore should handle it")
        delay(5000)

        syncProfile()

        val id = tx.id
        return if (id != null) {
            Log.info(tx.id)
            val txResult = Core.current?.getTransaction(id)
            Log.info(txResult.toString())
            txResult!!
        } else {
            tx
        }
    }

    @ExperimentalUnsignedTypes
    private suspend fun syncProfile() {
        val player = model.getPlayer().value
        if (player != null) {
            val profile = Core.current?.getProfile()
            if (profile != null) {
                player.syncProfile(profile)
                withContext(Main) {
                    model.setPlayer(player)
                }
                player.saveAsync(this@GameScreenActivity)
                Log.info("saved user")
            }
        }

        Log.info("Done syncProfile")
    }

    @ExperimentalUnsignedTypes
    override fun onBuyCharacter(item: Character) {
        val player = model.getPlayer().value
        if (player != null) {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(getString(R.string.buy_character_prompt, item.name))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    val loading =
                        displayLoading(this, getString(R.string.loading_buy_character, item.name))

                    CoroutineScope(IO).launch {
                        val tx = txFlow {
                            Core.current?.applyRecipe(
                                RCP_BUY_CHARACTER,
                                cookbookId,
                                listOf()
                            )!!
                        }

                        withContext(Main) {
                            val message = if (tx.code == Transaction.ResponseCode.OK) {
                                getString(
                                    R.string.buy_character_complete,
                                    item.name
                                )
                            } else {
                                tx.raw_log
                            }

                            loading.dismiss()
                            displayMessage(
                                this@GameScreenActivity,
                                message
                            )
                            tx.id?.let { blockChainStatusViewModel.setTx(it) }

                            if (tx.code == Transaction.ResponseCode.OK) {
                                if (player.activeCharacter == -1) {
                                    val index = player.characters.indexOfFirst {
                                        it.id == tx.txData?.output?.get(0)?.itemId
                                    }
                                    player.setActiveCharacter(player.characters[index])
                                    model.setPlayer(player)
                                    player.saveAsync(this@GameScreenActivity)
                                }

                                onNavigation(INVENTORY)
                            }
                        }
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }
    }

    @ExperimentalUnsignedTypes
    override fun onEngageFight(fight: Fight, recipeId: String, itemIds: Array<String>) {
        val player = model.getPlayer().value
        if (player != null) {
            val currentCharacterName = player.getActiveCharacter()?.name
            val currentCharacterLevel = player.getActiveCharacter()?.level
            val loading = displayLoading(
                this,
                getString(
                    R.string.loading_fight,
                    fight.name,
                    currentCharacterName,
                    currentCharacterLevel
                )
            )

            CoroutineScope(IO).launch {
                val tx = txFlow {
                    Core.current?.applyRecipe(recipeId, cookbookId, itemIds.toList())!!
                }

                withContext(Main) {
                    var message = ""
                    if (tx.code == Transaction.ResponseCode.OK) {
                        if (tx.txData.output.isEmpty()) {
                            message =
                                getString(
                                    R.string.you_were_killed,
                                    currentCharacterName,
                                    "${getString(getFightIcon(fight.id))} ${fight.name}"
                                )
                            nav_host_fragment.findNavController()
                                .navigate(R.id.homeScreenFragment)
                        } else {
                            message = getString(
                                R.string.you_did_fight_with_and_earned,
                                "${getString(getFightIcon(fight.id))} ${fight.name}",
                                tx.txData.output[0].amount
                            )

                            when (tx.txData.output.size) {
                                2 -> {
                                    // Rabbit does not use weapon
                                    if (fight.id != ID_RABBIT) {
                                        message += "\n ${getString(R.string.you_have_lost_your_weapon)}"
                                        nav_host_fragment.findNavController()
                                            .navigate(R.id.forestScreenFragment)
                                    }
                                }
                                3 -> {
                                    if (fight.id == ID_GIANT || fight.id == ID_FIRE_GIANT || fight.id == ID_ICE_GIANT || fight.id == ID_ACID_GIANT) {
                                        val character = player.getActiveCharacter()
                                        if (character != null && character.special != NO_SPECIAL.toLong()) {
                                            val special = when (character.special) {
                                                1L -> getString(R.string.fire_icon)
                                                2L -> getString(R.string.ice_icon)
                                                3L -> getString(R.string.acid_icon)
                                                else -> ""
                                            }
                                            val dragon = when (character.special) {
                                                1L -> getString(R.string.fire_dragon)
                                                2L -> getString(R.string.ice_dragon)
                                                3L -> getString(R.string.acid_dragon)
                                                else -> ""
                                            }
                                            message += "\n${getString(
                                                R.string.fight_giant_special,
                                                special,
                                                dragon
                                            )}"

                                            nav_host_fragment.findNavController()
                                                .navigate(R.id.forestScreenFragment)
                                        }

                                    }
                                }
                                4 -> message += "\n ${getString(
                                    R.string.you_got_bonus_item,
                                    player.getItemNameByItemId(tx.txData.output[3].itemId)
                                )}"
                            }
                        }
                    } else {
                        message = tx.raw_log
                    }

                    loading.dismiss()
                    displayMessage(
                        this@GameScreenActivity,
                        message
                    )
                    tx.id?.let { blockChainStatusViewModel.setTx(it) }
                }


            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun onBuyGoldWithPylons() {
        val player = model.getPlayer().value
        player?.let {
            if (player.unlockedPylon < 100) {
                Toast.makeText(this, getString(R.string.not_enough_pylons), Toast.LENGTH_SHORT)
                    .show()
            } else {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage(
                    getString(
                        R.string.confirm_buy_gold_with_pylons,
                        100,
                        5000
                    )
                )
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                        val loading =
                            displayLoading(
                                this,
                                getString(R.string.loading_buy_gold_with_pylon, 100, 5000)
                            )
                        CoroutineScope(IO).launch {
                            val tx = txFlow {
                                Core.current?.applyRecipe(RCP_BUY_GOLD_WITH_PYLON, cookbookId, listOf())!!
                            }

                            withContext(Main) {
                                val message = if (tx.code == Transaction.ResponseCode.OK) {
                                    getString(R.string.bought_gold_with_pylons, 5000, 100)
                                } else {
                                    tx.raw_log
                                }

                                loading.dismiss()
                                displayMessage(
                                    this@GameScreenActivity,
                                    message
                                )
                                tx.id?.let { blockChainStatusViewModel.setTx(it) }
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.confirm))
                alert.show()
            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun onGetDevItems() {
        val loading =
            displayLoading(this, getString(R.string.loading_get_dev_items))
        CoroutineScope(IO).launch {
            val tx = txFlow {
                Core.current?.applyRecipe(RCP_GET_TEST_ITEMS, cookbookId, listOf())!!
            }

            withContext(Main) {
                val message = if (tx.code == Transaction.ResponseCode.OK) {
                    getString(R.string.got_dev_items)
                } else {
                    tx.raw_log
                }

                loading.dismiss()
                displayMessage(
                    this@GameScreenActivity,
                    message
                )
                tx.id?.let { blockChainStatusViewModel.setTx(it) }

                if (tx.code == Transaction.ResponseCode.OK) {
                    onNavigation(INVENTORY)
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun onGetPylons() {
        val loading =
            displayLoading(this, getString(R.string.loading_get_pylons))
        CoroutineScope(IO).launch {
            val tx = txFlow {
                Core.current?.getPylons(500)!!
            }

            withContext(Main) {
                val message = if (tx.code == Transaction.ResponseCode.OK) {
                    getString(R.string.got_pylons)
                } else {
                    tx.raw_log
                }

                loading.dismiss()
                displayMessage(
                    this@GameScreenActivity,
                    message
                )
                tx.id?.let { blockChainStatusViewModel.setTx(it) }
            }
        }
    }

    @ExperimentalUnsignedTypes
    private fun promptTrade(trade: Trade, itemIds: List<String>) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(
            getString(R.string.trade_fulfill)
        )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                val loading =
                    displayLoading(
                        this,
                        getString(R.string.trade_fulfill_loading)
                    )
                CoroutineScope(IO).launch {
                    val tx = txFlow {
                        Core.current?.fulfillTrade(trade.id, itemIds)!!
                    }

                    withContext(Main) {
                        val message = if (tx.code == Transaction.ResponseCode.OK) {
                            getString(R.string.trade_fulfill_complete)
                        } else {
                            tx.raw_log
                        }

                        loading.dismiss()
                        displayMessage(
                            this@GameScreenActivity,
                            message
                        )
                        tx.id?.let { blockChainStatusViewModel.setTx(it) }

                        if (tx.code == Transaction.ResponseCode.OK) {
                            refreshTrade()
                        }
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.show()
    }

    override fun onTrade(trade: Trade) {
        val player = model.getPlayer().value ?: return

        if (!player.canFulfillTrade(trade)) {
            Toast.makeText(this, getString(R.string.trade_cannot_fulfill), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (trade is BuyItemTrade) {
            model.trade = trade
            model.tradeBuyMatchingItems = player.getMatchingTradeItems(trade)
            val frag =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
                .navigate(R.id.itemSelectFragment)
            return
        }

        promptTrade(trade, listOf())
    }

    @ExperimentalUnsignedTypes
    override fun onCreateTrade(
        coinInput: List<CoinInput>,
        itemInput: List<TradeItemInput>,
        coinOutput: List<Coin>,
        itemOutput: List<tech.pylons.lib.types.tx.item.Item>,
        extraInfo: String
    ) {
        val loading =
            displayLoading(
                this,
                getString(R.string.trade_create_loading)
            )
        CoroutineScope(IO).launch {
            val tx = txFlow {
                Core.current?.engine?.createTrade(
                    coinInput,
                    itemInput,
                    coinOutput,
                    itemOutput,
                    extraInfo
                )!!
            }

            withContext(Main) {
                val message = if (tx.code == Transaction.ResponseCode.OK) {
                    getString(R.string.trade_create_complete)
                } else {
                    tx.raw_log
                }

                loading.dismiss()
                displayMessage(
                    this@GameScreenActivity,
                    message
                )
                tx.id?.let { blockChainStatusViewModel.setTx(it) }

                if (tx.code == Transaction.ResponseCode.OK) {
                    refreshTrade()
                }
            }
        }
    }

    private fun refreshTrade() {
        val frag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
            .popBackStack()
        frag.childFragmentManager.fragments[0].childFragmentManager.fragments[0].findNavController()
            .navigate(R.id.pylonCentralTradeFragment)
    }

    override fun onItemTradeBuy(item: ItemSpec) {
        model.setTradeInput(item)
    }

    override fun onItemTradeSell(item: Item) {
        if (item.lockedTo.isNotBlank()) {
            displayMessage(this, getString(R.string.item_is_locked, item.lockedTo))
            return
        }

        onTradeSell(item.id)
    }

    override fun onCharacterTradeSell(character: Character) {
        if (character.lockedTo.isNotBlank()) {
            displayMessage(this, getString(R.string.item_is_locked, character.lockedTo))
            return
        }
        onTradeSell(character.id)
    }

    private fun onTradeSell(id: String) {
        CoroutineScope(IO).launch {
            val coreItem = getItemById(id)
            if (coreItem != null) {
                withContext(Main) {
                    model.setTradeOutput(
                        coreItem
                    )
                }
            } else {
                // TODO("handle error")
            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun onCancel(trade: Trade) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(
            getString(R.string.trade_cancel)
        )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                val loading =
                    displayLoading(
                        this,
                        getString(R.string.trade_cancel_loading)
                    )
                CoroutineScope(IO).launch {
                    val tx = txFlow {
                        Core.current?.cancelTrade(trade.id)!!
                    }

                    withContext(Main) {
                        val message = if (tx.code == Transaction.ResponseCode.OK) {
                            getString(R.string.trade_cancel_complete)
                        } else {
                            tx.raw_log
                        }

                        loading.dismiss()
                        displayMessage(
                            this@GameScreenActivity,
                            message
                        )
                        tx.id?.let { blockChainStatusViewModel.setTx(it) }

                        if (tx.code == Transaction.ResponseCode.OK) {
                            refreshTrade()
                        }
                    }
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.show()
    }

    override fun onCharacterUpdate(character: Character) {
        if (character.lockedTo.isNotBlank()) {
            displayMessage(this, getString(R.string.item_is_locked, character.lockedTo))
            return
        }

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_text, null)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(
            getString(R.string.update_character_prompt)
        )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.confirm))
        alert.setView(mDialogView)
        alert.show()

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = mDialogView.edit_text.text.toString()
            if (name.isNotBlank()) {
                onRenameCharacter(character, name)
                alert.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @ExperimentalUnsignedTypes
    private fun onRenameCharacter(character: Character, name: String) {
        val loading =
            displayLoading(
                this,
                getString(R.string.update_character_loading, character.name, name)
            )
        CoroutineScope(IO).launch {
            val tx = txFlow {
                Core.current?.engine?.setItemFieldString(character.id, "Name", name)!!
            }

            withContext(Main) {
                val message = if (tx.code == Transaction.ResponseCode.OK) {
                    getString(R.string.update_character_complete, name)
                } else {
                    tx.raw_log
                }

                loading.dismiss()
                displayMessage(
                    this@GameScreenActivity,
                    message
                )
                tx.id?.let { blockChainStatusViewModel.setTx(it) }
            }
        }

    }

    @ExperimentalUnsignedTypes
    override fun onItemTradeBuy(item: Item) {
        promptTrade(model.trade, listOf(item.id))
    }

    override fun onFriend(friend: Friend) {
        val sheet = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_friend, null)
        view.text_view_delete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(
                getString(
                    R.string.delete_friend_prompt,
                    friend.name,
                    friend.address
                )
            )
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    val player = model.getPlayer().value
                    if (player != null) {
                        player.deleteFriend(friend)
                        model.setPlayer(player)
                        player.saveAsync(this)
                        sheet.dismiss()
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                    sheet.dismiss()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.confirm))
            alert.show()
        }

        sheet.setContentView(view)
        sheet.show()
    }

    override fun onSendItem(friend: Friend) {
        sendItemViewModel.friend = friend
        findNavController(R.id.nav_host_fragment_send_item).navigate(R.id.sendItemListFragment)
    }

    override fun onItemSend(item: Item) {
        if (item.lockedTo.isNotBlank()) {
            displayMessage(this, getString(R.string.item_is_locked, item.lockedTo))
            return
        }
        sendItemViewModel.itemIds = listOf(item)
        findNavController(R.id.nav_host_fragment_send_item).navigate(R.id.sendItemConfirmFragment)
    }

    @ExperimentalUnsignedTypes
    override fun onSendItems(friendAddress: String, itemIds: List<Item>) {
        val player = model.getPlayer().value
        player?.let {
            val loading = displayLoading(this, getString(R.string.send_items_loading))
            CoroutineScope(IO).launch {
                val tx = txFlow {
                    Core.current?.engine?.sendItems(player.address, /*listOf(friendAddress),*/ itemIds.map { it.id })!!
                }

                withContext(Main) {
                    val message = if (tx.code == Transaction.ResponseCode.OK) {
                        getString(R.string.send_items_complete)
                    } else {
                        tx.raw_log
                    }

                    loading.dismiss()
                    displayMessage(
                        this@GameScreenActivity,
                        message
                    )
                    tx.id?.let { blockChainStatusViewModel.setTx(it) }

                    if (tx.code == Transaction.ResponseCode.OK) {
                        onNavigation(PYLONS_CENTRAL)
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun disbursePylons(purchase: Purchase) {
        Log.info("disbursePylons purchase ${purchase.purchaseToken}")
        CoroutineScope(IO).launch {
            lateinit var loading: AlertDialog
            withContext(Main) {
                loading =
                    displayLoading(this@GameScreenActivity, getString(R.string.loading_get_pylons))
            }

            val tx = txFlow {
                Core.current?.googleIapGetPylons(
                    productId = purchase.sku,
                    purchaseToken = purchase.purchaseToken,
                    receiptData = purchase.originalJson,
                    signature = purchase.signature
                )!!
            }

            withContext(Main) {
                val message = if (tx.code == Transaction.ResponseCode.OK) {
                    getString(R.string.purchase_complete)
                } else {
                    tx.raw_log
                }

                loading.dismiss()
                displayMessage(
                    this@GameScreenActivity,
                    message
                )
                tx.id?.let { id -> blockChainStatusViewModel.setTx(id) }
            }

            if (tx.code == Transaction.ResponseCode.OK) {
                Log.info("purchase tx ok, delete local record")
                localCacheClient.purchaseDao().delete(purchase)
            }
        }

    }

    override fun onRefresh() {
        val loading = displayLoading(this, getString(R.string.syncing_account))
        CoroutineScope(IO).launch {
            syncProfile()
            withContext(Main) {
                loading.dismiss()
            }
        }
    }
}
