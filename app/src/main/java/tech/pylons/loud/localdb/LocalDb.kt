package tech.pylons.loud.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CachedPurchase::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(PurchaseTypeConverter::class)
abstract class LocalDb : RoomDatabase() {
    abstract fun purchaseDao(): PurchaseDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDb? = null
        private val DATABASE_NAME = "local_db"

        fun getInstance(context: Context): LocalDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(appContext: Context): LocalDb  {
            return Room.databaseBuilder(appContext, LocalDb::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration() // Data is cache, so it is OK to delete
                .build()
        }
    }
}