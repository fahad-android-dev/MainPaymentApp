package com.orbits.paymentapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.orbits.paymentapp.helper.DataStoreManager.PreferencesKeys.APP
import com.orbits.paymentapp.helper.DataStoreManager.PreferencesKeys.MASTER_KEY
import com.orbits.paymentapp.helper.DataStoreManager.PreferencesKeys.PASSWORD
import com.orbits.paymentapp.helper.DataStoreManager.PreferencesKeys.USER_REMEMBER_DATA
import com.orbits.paymentapp.helper.DataStoreManager.PreferencesKeys.USER_RESPONSE_DATA
import com.orbits.paymentapp.helper.helper_model.AppConfigModel
import com.orbits.paymentapp.helper.helper_model.AppMasterKeyModel
import com.orbits.paymentapp.helper.helper_model.DeepLinkModel
import com.orbits.paymentapp.helper.helper_model.PasswordModel
import com.orbits.paymentapp.helper.helper_model.StoreDataModel
import com.orbits.paymentapp.helper.helper_model.UserRememberDataModel
import com.orbits.paymentapp.helper.helper_model.UserResponseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

private val MERCHANT_DATASTORE = "OPENSLOT PARTNER"
private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = MERCHANT_DATASTORE)


class DataStoreManager(val context: Context) {
    private val instance = context.dataStore

    private object PreferencesKeys {
        val USER_RESPONSE_DATA = stringPreferencesKey("response_data")
        val USER_REMEMBER_DATA = stringPreferencesKey("user_remember_data")
        val MASTER_KEY = stringPreferencesKey("master_key")
        val PASSWORD = stringPreferencesKey("password")
        val STORE = stringPreferencesKey("store")
        val APP = stringPreferencesKey("application")
    }


    suspend fun saveAppConfig(responseModel: AppConfigModel) {
        instance.edit { preferences ->
            preferences[APP] = Gson().toJson(responseModel)
        }
    }

    suspend fun getAppConfig(): Flow<AppConfigModel> {
        return instance.data.map { preferences ->
            Gson().fromJson(preferences[APP] ?: "" , AppConfigModel::class.java)
        }
    }

    suspend fun saveAppPassword(responseModel: PasswordModel) {
        instance.edit { preferences ->
            preferences[PASSWORD] = Gson().toJson(responseModel)
        }
    }

    fun getAppPassword(): Flow<PasswordModel?> {
        return instance.data.map { preferences ->
            val gson = Gson()
            val responseData = preferences[PASSWORD] ?: ""
            val dataObject = gson.fromJson(responseData, PasswordModel::class.java)
            dataObject
        }
    }

    suspend fun saveMasterKey(responseModel: AppMasterKeyModel) {
        instance.edit { preferences ->
            preferences[MASTER_KEY] = Gson().toJson(responseModel)
        }
    }

    suspend fun saveUserData(responseModel: UserResponseModel?) {
        instance.edit { preferences ->
            preferences[USER_RESPONSE_DATA] = Gson().toJson(responseModel)
        }
    }


    fun getUserData(): Flow<UserResponseModel?> {
        return instance.data.map { preferences ->
            val gson = Gson()
            val responseData = preferences[USER_RESPONSE_DATA] ?: ""
            val dataObject = gson.fromJson(responseData, UserResponseModel::class.java)
            dataObject
        }
    }

    fun getMasterKey(): Flow<AppMasterKeyModel?> {
        return instance.data.map { preferences ->
            val gson = Gson()
            val responseData = preferences[MASTER_KEY] ?: ""
            val dataObject = gson.fromJson(responseData, AppMasterKeyModel::class.java)
            dataObject
        }
    }


    suspend fun clearDataStore() = instance.edit {
        it.remove(USER_RESPONSE_DATA)
    }

    suspend fun clearRememberDataStore() = instance.edit {
        it.remove(USER_REMEMBER_DATA)
    }
}