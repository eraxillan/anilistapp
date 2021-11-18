package name.eraxillan.anilistapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import name.eraxillan.anilistapp.data.room.dao.FavoriteMediaDao
import name.eraxillan.anilistapp.data.room.MediaDatabase
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideMediaDatabase(@ApplicationContext context: Context): MediaDatabase {
        return MediaDatabase.getInstance(context)
    }

    @Provides
    fun provideFavoriteMediaDao(mediaDatabase: MediaDatabase): FavoriteMediaDao {
        return mediaDatabase.favoriteDao()
    }
}
