package tech.pylons.loud.fragments.screens.pyloncentral.purchasepylon

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import tech.pylons.loud.R
import tech.pylons.loud.constants.GameSku.INAPP_SKUS
import tech.pylons.loud.localdb.LocalDb
import tech.pylons.wallet.core.Core
import kotlinx.android.synthetic.main.purchase_item.view.*
import kotlinx.android.synthetic.main.purchase_pylon_fragment.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.logging.Logger

class PurchasePylonFragment : Fragment() {
    private val Log = Logger.getLogger(PurchasePylonFragment::class.java.name)

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var billingClient: BillingClient
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var localCacheClient: LocalDb

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.purchase_pylon_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            localCacheClient = LocalDb.getInstance(it)
            setupBillingClient()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.info("onDestroy")
        if (this::billingClient.isInitialized) {
            billingClient.endConnection()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun disbursePylons(purchase: Purchase)
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
                            checkPendingPurchases()
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
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(INAPP_SKUS).setType(BillingClient.SkuType.INAPP)
            val skuDetailsResult = withContext(IO) {
                billingClient.querySkuDetails(params.build())
            }

            Log.info(skuDetailsResult.skuDetailsList.toString())

            withContext(Main) {
                skuDetailsResult.skuDetailsList?.let { initProductAdapter(it) }
            }
        }
    }

    private fun checkPendingPurchases() {
        Log.info("checkPendingPurchases")
        CoroutineScope(IO).launch {
            val cachedPurchases = localCacheClient.purchaseDao().getPurchases().map { it.data }
            Log.info("processPurchases purchases in the lcl db ${cachedPurchases?.size}")

            val pendingPurchases = mutableListOf<Purchase>()
            cachedPurchases.forEach {
                val exists = Core.current?.engine?.checkGoogleIapOrder(it.purchaseToken)

                if (exists!!) {
                    Log.info("purchase exists delete local")
                    localCacheClient.purchaseDao().delete(it)
                } else {
                    pendingPurchases.add(it)
                }
            }

            if (pendingPurchases.isNotEmpty()) {
                Log.info("Has pending purchases ${pendingPurchases.size}")
                handleConsumablePurchases(pendingPurchases)
            } else {
                queryPurchases()
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
                    purchases?.apply { processPurchases(this) }
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
        Log.info("processPurchases")
        logPurchases(purchasesResult)

        val validPurchases = mutableListOf<Purchase>()
        purchasesResult.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                validPurchases.add(purchase)
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Log.info("Received a pending purchase of SKU: ${purchase.skus[0]}")
                // handle pending purchases, e.g. confirm with users about the pending
                // purchases, prompt them to complete it, etc.
            }
        }

        CoroutineScope(IO).launch {
            localCacheClient.purchaseDao().insert(*validPurchases.toTypedArray())
        }
        handleConsumablePurchases(validPurchases)
    }

    private fun handleConsumablePurchases(consumables: List<Purchase>) {
        Log.info("handleConsumablePurchases called")
        consumables.forEach {
            Log.info("handleConsumablePurchases foreach it is $it")

            val params = ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
            billingClient.consumeAsync(params) { billingResult, _ ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        Log.info("Consume OK")
                        listener?.disbursePylons(it)
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
                Log.info("sku: ${it.skus[0]}")
                Log.info("obfuscatedAccountId: ${it.accountIdentifiers?.obfuscatedAccountId}")
                Log.info("obfuscatedProfileId: ${it.accountIdentifiers?.obfuscatedProfileId}")
            }
        } else {
            Log.info("no purchase list")
        }
    }

    private fun queryHistory() {
        CoroutineScope(IO).launch {
            val result = billingClient.queryPurchaseHistory(BillingClient.SkuType.INAPP)
            Log.info("queryPurchaseHistory")
            Log.info(result.toString())
            val purchases = result.purchaseHistoryRecordList
            Log.info(purchases.toString())
            if (purchases != null && purchases.isNotEmpty()) {
                purchases.forEach {
                    Log.info("sig: ${it.signature}")
                    Log.info("developerPayload: ${it.developerPayload}")
                    Log.info("originalJson: ${it.originalJson}")
                    Log.info("purchaseToken: ${it.purchaseToken}")
                    Log.info("sku: ${it.skus[0]}")
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