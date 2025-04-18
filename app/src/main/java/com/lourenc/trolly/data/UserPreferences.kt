package com.lourenc.trolly.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



private val Context.dataStore by preferencesDataStore(name = "user_prefs")

data class User(
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val password: String
)

class UserPreferences (private val context: Context) {

    companion object {
        val FIRST_NAME_KEY = stringPreferencesKey("user_first_name")
        val LAST_NAME_KEY = stringPreferencesKey("user_last_name")
        val EMAIL_KEY = stringPreferencesKey("user_email")
        val PASSWORD_KEY = stringPreferencesKey("user_password")
    }

    suspend fun saveUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[FIRST_NAME_KEY] = firstName
            prefs[LAST_NAME_KEY] = lastName
            prefs[EMAIL_KEY] = email
            prefs[PASSWORD_KEY] = password

        }
    }

    val getUser: Flow<User?> = context.dataStore.data.map { prefs ->
        val firstName = prefs[FIRST_NAME_KEY]
        val lastName = prefs[LAST_NAME_KEY]
        val email = prefs[EMAIL_KEY]
        val password = prefs[PASSWORD_KEY]

        if (email != null && password != null) {
            User(firstName, lastName, email, password)
        } else {
            null
        }


    }
}