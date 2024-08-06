package com.paradoxo.avva.di

import android.content.Context
import com.paradoxo.avva.util.BitmapUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class BitmapUtilModule {
    @Provides
    fun provideGeminiAvvA(@ApplicationContext context: Context): BitmapUtil {
        return BitmapUtil(context)
    }
}