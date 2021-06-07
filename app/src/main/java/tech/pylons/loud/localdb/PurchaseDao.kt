package tech.pylons.loud.localdb

import androidx.room.*
import com.android.billingclient.api.Purchase

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchase_table")
    fun getPurchases(): List<CachedPurchase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(purchase: CachedPurchase)

    @Transaction
    fun insert(vararg purchases: Purchase) {
        purchases.forEach {
            insert(CachedPurchase(purchaseToken = it.purchaseToken, data = it))
        }
    }

    @Delete
    fun delete(vararg purchases: CachedPurchase)

    @Query("DELETE FROM purchase_table")
    fun deleteAll()

    @Query("DELETE FROM purchase_table WHERE data = :purchase")
    fun delete(purchase: Purchase)
}