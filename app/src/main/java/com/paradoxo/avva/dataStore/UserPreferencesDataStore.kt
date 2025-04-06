package com.paradoxo.avva.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    object PreferencesKey {
        val apiKey = stringPreferencesKey("customApiKey")
        val needShowAccessibilityDialog = booleanPreferencesKey("needShowAccessibilityDialog")
    }

    suspend fun saveApiKey(score: String) {
        dataStore.edit { edit ->
            edit[PreferencesKey.apiKey] = score
        }
    }

    fun getApiKey(): Flow<String> {
        return dataStore.data.map {
            it[PreferencesKey.apiKey] ?: ""
        }
    }

    suspend fun saveNeedShowAccessibilityDialog(show: Boolean) {
        dataStore.edit { edit ->
            edit[PreferencesKey.needShowAccessibilityDialog] = show
        }
    }

    fun getNeedShowAccessibilityDialog(): Flow<Boolean> {
        return dataStore.data.map {
            it[PreferencesKey.needShowAccessibilityDialog] != false
        }
    }
}