package name.eraxillan.airinganimeschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import name.eraxillan.airinganimeschedule.model.AiringAnime

@Database(
    entities = [AiringAnime::class],
    version = 2
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AiringAnimeDatabase : RoomDatabase() {
    abstract fun airingAnimeDao(): AiringAnimeDao

    // Singleton object
    companion object {
        private var instance: AiringAnimeDatabase? = null

        fun getInstance(context: Context): AiringAnimeDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AiringAnimeDatabase::class.java,
                    "airing_anime_db"
                ).fallbackToDestructiveMigration().build()
            }
            return instance as AiringAnimeDatabase
        }
    }
}