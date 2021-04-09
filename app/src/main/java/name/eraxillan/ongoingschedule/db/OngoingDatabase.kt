package name.eraxillan.ongoingschedule.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import name.eraxillan.ongoingschedule.model.Ongoing

@Database(
    entities = [Ongoing::class],
    version = 1
)
//@TypeConverters(Converters::class)
abstract class OngoingDatabase : RoomDatabase() {
    abstract fun ongoingDao(): OngoingDao

    // Singleton object
    companion object {
        private var instance: OngoingDatabase? = null

        fun getInstance(context: Context): OngoingDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    OngoingDatabase::class.java,
                    "ongoing_db"
                ).build()
            }
            return instance as OngoingDatabase
        }
    }
}