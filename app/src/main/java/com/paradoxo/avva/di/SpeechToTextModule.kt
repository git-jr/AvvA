package com.paradoxo.avva.di

import android.content.Context
import com.paradoxo.avva.speechToText.SpeechToText
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SpeechToTextModule {

    @Singleton
    @Provides
    fun provideSpeechToText(@ApplicationContext context: Context): SpeechToText {
        return SpeechToText(context)
    }
}