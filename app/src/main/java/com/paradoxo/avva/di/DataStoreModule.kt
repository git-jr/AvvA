package com.paradoxo.avva.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.paradoxo.avva.dataStore.UserPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATASTORE = "avva_datastore"

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Singleton
    @Provides
    fun provideDataStoreUserPreferences(@ApplicationContext context: Context): UserPreferencesDataStore {
        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile(
                    DATASTORE
                )
            }
        )
        return UserPreferencesDataStore(dataStore)
    }
}