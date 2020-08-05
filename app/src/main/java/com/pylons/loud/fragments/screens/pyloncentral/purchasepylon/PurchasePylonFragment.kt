package com.pylons.loud.fragments.screens.pyloncentral.purchasepylon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.fragments.ui.blockchainstatus.BlockChainStatusViewModel
import com.pylons.loud.utils.UI
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Transaction
import kotlinx.android.synthetic.main.purchase_item.view.*
import kotlinx.android.synthetic.main.purchase_pylon_fragment.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.ArrayList
import java.util.logging.Logger

class PurchasePylonFragment : Fragment() {
    private val Log = Logger.getLogger(PurchasePylonFragment::class.java.name)

    private lateinit var billingClient: BillingClient
    private lateinit var productsAdapter: ProductsAdapter
    private val model: GameScreenActivity.SharedViewModel by activityViewModels()
    private val blockChainStatusViewModel: BlockChainStatusViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.purchase_pylon_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBillingClient()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.info("onDestroy")
        if (this::billingClient.isInitialized) {
            billingClient.endConnection()
        }
    }

    private fun setupBillingClient() {
        Log.info("setupBillingClient")
        context?.let {
            billingClient = BillingClient
                .newBuilder(it)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build()

            connectToPlayBillingService()
        }
    }

    private fun connectToPlayBillingService(): Boolean {
        Log.info("connectToPlayBillingService")
        if (!billingClient.isReady) {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    when (billingResult.responseCode) {
                        BillingClient.BillingResponseCode.OK -> {
                            Log.info("onBillingSetupFinished successfully")
                            querySkuDetails()
                            queryPurchases()
                            queryHistory()
                        }
                        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                            //Some apps may choose to make decisions based on this knowledge.
                            Log.info(billingResult.debugMessage)
                        }
                        else -> {
                            //do nothing. Someone else will connect it through retry policy.
                            //May choose to send to server though
                            Log.info(billingResult.debugMessage)
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.info("onBillingServiceDisconnected")

                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    connectToPlayBillingService()
                }
            })
            return true
        }
        return false
    }

    private fun querySkuDetails() {
        CoroutineScope(IO).launch {
            val skuList = ArrayList<String>()
            skuList.add("pylons_1000")
            skuList.add("android.test.purchased")
            skuList.add("android.test.canceled")
            skuList.add("android.test.refunded")
            skuList.add("android.test.item_unavailable")
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            val skuDetailsResult = withContext(IO) {
                billingClient.querySkuDetails(params.build())
            }

            Log.info(skuDetailsResult.skuDetailsList.toString())

            withContext(Main) {
                skuDetailsResult.skuDetailsList?.let { initProductAdapter(it) }
            }
        }
    }

    private fun queryPurchases() {
        Log.info("queryPurchases")
        val purchasesResult = mutableListOf<Purchase>()
        val result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)

        result.purchasesList?.apply { purchasesResult.addAll(this) }
        processPurchases(purchasesResult)
    }

    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.info("purchaseUpdateListener")

            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    purchases?.let {
                        it.forEach { purchase ->
                            handlePurchase(purchase)
                        }
                    }
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    Log.info(billingResult.debugMessage)
                    queryPurchases()
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                    connectToPlayBillingService()
                }
                else -> {
                    Log.info(billingResult.debugMessage)
                }
            }
        }

    private fun processPurchases(purchasesResult: MutableList<Purchase>) {
        logPurchases(purchasesResult)

        purchasesResult.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                handlePurchase(purchase)
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Log.info("Received a pending purchase of SKU: ${purchase.sku}")
                // handle pending purchases, e.g. confirm with users about the pending
                // purchases, prompt them to complete it, etc.
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
//        consumePurchase(purchase)
        context?.let {
            val loading =
                UI.displayLoading(it, getString(R.string.loading_get_pylons))
            CoroutineScope(IO).launch {
                val tx = txFlow {
                    Core.engine.googleIapGetPylons(
                        productId = purchase.sku,
                        purchaseToken = purchase.purchaseToken,
                        receiptData = purchase.originalJson,
                        signature = purchase.signature
                    )
                }

                withContext(Main) {
                    val message = if (tx.code == Transaction.ResponseCode.OK) {
                        getString(R.string.purchase_complete)
                    } else {
                        tx.raw_log
                    }

                    loading.dismiss()
                    UI.displayMessage(
                        it,
                        message
                    )
                    tx.id?.let { id -> blockChainStatusViewModel.setTx(id) }

                    if (tx.code == Transaction.ResponseCode.OK) {
                        consumePurchase(purchase)
                    }
                }
            }
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        Log.info("consumePurchase")

        Log.info(purchase.sku)

        if (purchase.sku == "android.test.purchased") {
            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

            billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        // Handle the success of the consume operation.
                        Log.info("outToken: $outToken")
                    }
                    else -> {
                        Log.info(billingResult.debugMessage)
                    }
                }
            }
        }
    }

    private fun logPurchases(purchases: MutableList<Purchase>) {
        if (purchases != null && purchases.isNotEmpty()) {
            purchases.forEach {
                Log.info("isAcknowledged: ${it.isAcknowledged}")
                Log.info("purchaseState: ${it.purchaseState}")
                Log.info("isAutoRenewing: ${it.isAutoRenewing}")
                Log.info("sig: ${it.signature}")
                Log.info("developerPayload: ${it.developerPayload}")
                Log.info("orderId: ${it.orderId}")
                Log.info("originalJson: ${it.originalJson}")
                Log.info("packageName: ${it.packageName}")
                Log.info("purchaseToken: ${it.purchaseToken}")
                Log.info("sku: ${it.sku}")
                Log.info("obfuscatedAccountId: ${it.accountIdentifiers?.obfuscatedAccountId}")
                Log.info("obfuscatedProfileId: ${it.accountIdentifiers?.obfuscatedProfileId}")
            }
        } else {
            Log.info("no purchase list")
        }
    }

    private fun queryHistory() {
        CoroutineScope(IO).launch {
            val result2 = billingClient.queryPurchaseHistory(BillingClient.SkuType.INAPP)
            Log.info("queryPurchaseHistory")
            Log.info(result2.toString())
            val purchases = result2.purchaseHistoryRecordList
            Log.info(purchases.toString())
            if (purchases != null && purchases.isNotEmpty()) {
                purchases.forEach {
                    Log.info("sig: ${it.signature}")
                    Log.info("developerPayload: ${it.developerPayload}")
                    Log.info("originalJson: ${it.originalJson}")
                    Log.info("purchaseToken: ${it.purchaseToken}")
                    Log.info("sku: ${it.sku}")
                }
            } else {
                Log.info("no purchase list")
            }
        }
    }

    private fun initProductAdapter(skuDetailsList: List<SkuDetails>) {
        activity?.let { a ->
            productsAdapter = ProductsAdapter(skuDetailsList) {
                val billingFlowParams = BillingFlowParams
                    .newBuilder()
                    .setSkuDetails(it)
                    .build()
                val result = billingClient.launchBillingFlow(a, billingFlowParams)
                Log.info("resposneCode: ${result?.responseCode} ${result?.debugMessage}")
            }
            products.adapter = productsAdapter
        }
    }

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
            val txResult = Core.engine.getTransaction(id)
            Log.info(txResult.toString())
            txResult
        } else {
            tx
        }
    }

    private suspend fun syncProfile() {
        val player = model.getPlayer().value
        player?.let {
            val profile = Core.engine.getOwnBalances()
            profile?.let {
                player.syncProfile(profile)
                withContext(Main) {
                    model.setPlayer(player)
                }
                context?.let {
                    player.saveAsync(it)
                    Log.info("saved user")
                }
            }

        }
        Log.info("Done syncProfile")
    }
}

class ProductsAdapter(
    private val list: List<SkuDetails>,
    private val onProductClicked: (SkuDetails) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.purchase_item, parent, false)
        val viewHolder = ViewHolder(textView)
        textView.setOnClickListener { onProductClicked(list[viewHolder.adapterPosition]) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.price.text = item.price
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val title: TextView = mView.text_title
        val description: TextView = mView.text_description
        val price: TextView = mView.text_price

        override fun toString(): String {
            return "ViewHolder(mView=$mView, title=$title, description=$description, price=$price)"
        }
    }
}