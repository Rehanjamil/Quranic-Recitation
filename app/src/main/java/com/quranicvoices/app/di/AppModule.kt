package com.quranicvoices.app.di

import android.app.Application
import com.quranicvoices.rabiulqaloob.utils.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreference(
        context: Application
    ): PreferenceHelper {
        return PreferenceHelper(context)
    }
}