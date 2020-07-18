package com.pylons.loud.fragments.screens.pyloncentral.purchasepylon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
import com.pylons.loud.R
import kotlinx.android.synthetic.main.purchase_item.view.*
import kotlinx.android.synthetic.main.purchase_pylon_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import java.util.logging.Logger

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

class PurchasePylonFragment : Fragment() {
    private val Log = Logger.getLogger(PurchasePylonFragment::class.java.name)

    private val viewModel: PurchasePylonViewModel by viewModels()
    private lateinit var billingClient: BillingClient
    private lateinit var productsAdapter: ProductsAdapter

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

    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.info("purchaseUpdateListener")
            Log.info(billingResult.debugMessage)
            Log.info(billingResult.responseCode.toString())
            Log.info(purchases.toString())
            logPurchases(purchases)
        }

    private fun setupBillingClient() {
        val c = context
        if (c != null) {
            billingClient = BillingClient
                .newBuilder(c)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build()

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.info(billingResult.debugMessage)
                    Log.info(billingResult.responseCode.toString())
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        querySkuDetails()
                        queryPurchases()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.info("onBillingServiceDisconnected")

                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
        }
    }

    private fun logPurchases(purchases: MutableList<Purchase>?) {
        if (purchases != null && purchases.isNotEmpty()) {
            purchases.forEach {
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

    private fun queryPurchases() {
        Log.info("queryPurchases")
        val result = billingClient.queryPurchases("pylons_1000")
        Log.info(result.responseCode.toString())
        logPurchases(result.purchasesList)

        CoroutineScope(IO).launch {
            val result2 = billingClient.queryPurchaseHistory("pylons_1000")
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

    private fun querySkuDetails() {
        val c = activity
        if (c != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val skuList = ArrayList<String>()
                skuList.add("pylons_1000")
//                skuList.add("android.test.purchased")
//                skuList.add("android.test.canceled")
//                skuList.add("android.test.refunded")
//                skuList.add("android.test.item_unavailable")
                val params = SkuDetailsParams.newBuilder()
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                val skuDetailsResult = withContext(IO) {
                    billingClient.querySkuDetails(params.build())
                }

                Log.info(skuDetailsResult.skuDetailsList.toString())

                withContext(Dispatchers.Main) {
                    skuDetailsResult.skuDetailsList?.let { initProductAdapter(it) }
                }
            }
        }
    }

    private fun initProductAdapter(skuDetailsList: List<SkuDetails>) {
        val a = activity
        if (a != null) {
            productsAdapter = ProductsAdapter(skuDetailsList) {
                val billingFlowParams = BillingFlowParams
                    .newBuilder()
                    .setSkuDetails(it)
                    .build()
                val result = billingClient.launchBillingFlow(a, billingFlowParams)
                Log.info("resposneCode: ${result?.debugMessage}")

                if (result.responseCode == ITEM_ALREADY_OWNED) {
                    CoroutineScope(IO).launch {
                        val consumeParams =
                            ConsumeParams.newBuilder()
                                .setPurchaseToken("inapp:com.pylons.loud:android.test.purchased")
                                .build()
                        val consumeResult = withContext(IO) {
                            billingClient.consumePurchase(consumeParams)
                        }
                        Log.info(consumeResult.toString())
                        Log.info(consumeResult.billingResult.responseCode.toString())
                    }
                }
            }
            products.adapter = productsAdapter
        }

    }

}