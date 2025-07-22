package br.edu.utfpr.menfin.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStore(private val context: Context) {

    companion object {
        val USER_LOGGED_KEY = stringPreferencesKey("user_logged")
    }

    suspend fun saveUserName(user: UserLogged) {
        context.dataStore.edit { settings ->
            val jsonString = Json.encodeToString(user)
            settings[USER_LOGGED_KEY] = jsonString
        }
    }

    suspend fun removeUserLogged() {
        context.dataStore.edit { settings ->
            settings.remove(USER_LOGGED_KEY)
        }
    }

    val userLoggedFlow: Flow<UserLogged?> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[USER_LOGGED_KEY]
            if (jsonString != null) {
                try {
                    Json.decodeFromString<UserLogged>(jsonString)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
}