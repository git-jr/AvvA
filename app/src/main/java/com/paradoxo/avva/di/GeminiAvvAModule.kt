package com.paradoxo.avva.di

import com.paradoxo.avva.gemini.GeminiAvvA
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class GeminiAvvAModule {
    @Provides
    fun provideGeminiAvvA(): GeminiAvvA {
        return GeminiAvvA(com.paradoxo.avva.BuildConfig.apiKey)
    }
}