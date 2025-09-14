package dev.henriquehorbovyi.winkel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

/**
 *   Gets the singleton DataStore instance, creating it if necessary.
 */
fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

const val dataStoreFileName = "winkel.preferences_pb"

class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val isDarkModeKey = booleanPreferencesKey("is_dark_mode")
    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[isDarkModeKey] ?: false
    }

    suspend fun toggleTheme() {
        dataStore.edit { preferences ->
            preferences[isDarkModeKey] = !(preferences[isDarkModeKey] ?: false)
        }
    }
}
