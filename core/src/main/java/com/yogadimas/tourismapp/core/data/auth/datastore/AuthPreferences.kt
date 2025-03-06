package com.yogadimas.tourismapp.core.data.auth.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthPreferences(private val dataStore: DataStore<Preferences>) {


    suspend fun savePIN(pin: String) {
        dataStore.edit { preferences ->
            preferences[PIN_KEY] = pin
        }
    }


    fun getPIN(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PIN_KEY]
        }
    }

    companion object {
        private val PIN_KEY = stringPreferencesKey("pin_data")
    }
}

