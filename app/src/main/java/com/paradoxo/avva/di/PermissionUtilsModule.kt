package com.paradoxo.avva.di

import android.content.Context
import com.paradoxo.avva.util.PermissionUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PermissionUtilsModule {
    @Provides
    fun providePermissionUtils(@ApplicationContext context: Context): PermissionUtils {
        return PermissionUtils(context)
    }
}