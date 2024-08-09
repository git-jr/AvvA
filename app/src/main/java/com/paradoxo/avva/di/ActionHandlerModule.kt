package com.paradoxo.avva.di

import android.content.Context
import com.paradoxo.avva.util.ActionHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ActionHandlerModule {
    @Provides
    fun provideActionHandler(@ApplicationContext context: Context): ActionHandler {
        return ActionHandler(context)
    }
}