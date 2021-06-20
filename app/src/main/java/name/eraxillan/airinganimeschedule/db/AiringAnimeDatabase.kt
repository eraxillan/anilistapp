package name.eraxillan.airinganimeschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import name.eraxillan.airinganimeschedule.model.AiringAnime
import name.eraxillan.airinganimeschedule.utilities.DATABASE_NAME
import name.eraxillan.airinganimeschedule.workers.AiringAnimeDatabaseWorker

/**
 * The Room database for this app
 */
@Database(
    entities = [AiringAnime::class],
    version = 2
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AiringAnimeDatabase : RoomDatabase() {
    abstract fun airingAnimeDao(): AiringAnimeDao

    // Singleton object
    companion object {
        @Volatile
        private var instance: AiringAnimeDatabase? = null

        fun getInstance(context: Context): AiringAnimeDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AiringAnimeDatabase {
            return Room.databaseBuilder(context, AiringAnimeDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            /*val request = OneTimeWorkRequestBuilder<AiringAnimeDatabaseWorker>()
                                .build()
                            WorkManager.getInstance(context).enqueue(request)*/
                        }
                    }
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
