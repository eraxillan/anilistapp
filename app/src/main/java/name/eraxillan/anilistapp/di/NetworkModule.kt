package name.eraxillan.anilistapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import name.eraxillan.anilistapp.api.AnilistApi
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideAnilistApi(): AnilistApi {
        val client = AnilistApi.createClient()
        return AnilistApi.create(client)
    }
}
