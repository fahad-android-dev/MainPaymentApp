package com.orbits.paymentapp.helper

import android.content.Context
import com.orbits.paymentapp.helper.Extensions.asString
import com.orbits.paymentapp.helper.helper_model.AppConfigModel
import com.orbits.paymentapp.helper.helper_model.AppMasterKeyModel
import com.orbits.paymentapp.helper.helper_model.ClientListDataModel
import com.orbits.paymentapp.helper.helper_model.DeepLinkModel
import com.orbits.paymentapp.helper.helper_model.Device
import com.orbits.paymentapp.helper.helper_model.PasswordModel
import com.orbits.paymentapp.helper.helper_model.ServiceDataModel
import com.orbits.paymentapp.helper.helper_model.StoreDataModel
import com.orbits.paymentapp.helper.helper_model.UserRememberDataModel
import com.orbits.paymentapp.helper.helper_model.UserResponseModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object PrefUtils {

    /**  -----------------------      USER DATA ---------------------------------- */
    fun Context.setUserDataResponse(result: UserResponseModel?) {
        val dt = DataStoreManager(this)
        runBlocking { dt.saveUserData(result) }
    }

    fun Context.getUserDataResponse(): UserResponseModel? {
        val dt = DataStoreManager(this)
        return runBlocking {
            dt.getUserData().first()
        }
    }

    fun Context.getConnectionCode(): String? {
        val dt = DataStoreManager(this)
        return runBlocking {
            dt.getUserData().firstOrNull()?.data?.connection_code
        }
    }

    fun Context.getUserId(): String {
        return getUserDataResponse()?.data?.id.asString()
    }

    fun Context.getUserName(): String {
        val userModel = getUserDataResponse()
        return userModel?.data?.firstName ?: ""
    }

    fun Context.isUserLoggedIn(): Boolean {
        return this.getUserDataResponse()?.data?.id != null
    }
    fun Context.isCodeVerified(): Boolean {
        return this.getUserDataResponse()?.data?.isCodeVerified != false
    }

    fun Context.getDeviceModel(): Device {
        return Device(
            device_model = Constants.DEVICE_MODEL,
            device_token = Constants.DEVICE_TOKEN,
            device_type = Constants.DEVICE_TYPE,
            //app_version = Constants.APP_VERSION,
            os_version = Constants.OS_VERSION
        )
    }


    /**  -----------------------  ------------------------------------  ---------------------------------- */

    /**  -----------------------      APP CONFIG         ---------------------------------- */

    fun Context.setAppConfig(result: AppConfigModel) {
        val dt = DataStoreManager(this)
        runBlocking { dt.saveAppConfig(result) }
    }

    fun Context.getAppConfig(): AppConfigModel? {
        val dt = DataStoreManager(this)
        return runBlocking { dt.getAppConfig().firstOrNull() }
    }

    fun Context.setAppPassword(result: PasswordModel) {
        val dt = DataStoreManager(this)
        runBlocking { dt.saveAppPassword(result) }
    }

    fun Context.getAppPassword(): PasswordModel? {
        val dt = DataStoreManager(this)
        return runBlocking { dt.getAppPassword().first() }
    }

    fun Context.setMasterKey(result: AppMasterKeyModel) {
        val dt = DataStoreManager(this)
        runBlocking { dt.saveMasterKey(result) }
    }

    fun Context.getMasterKey(): AppMasterKeyModel? {
        val dt = DataStoreManager(this)
        return runBlocking { dt.getMasterKey().first() }
    }

    fun Context.setServiceData(result: ServiceDataModel) {
        val dt = DataStoreManager(this)
        runBlocking { dt.saveServiceData(result) }
    }

    fun Context.getService(): ServiceDataModel? {
        val dt = DataStoreManager(this)
        return runBlocking { dt.getServiceData().first() }
    }

    fun Context.setClientsData(result: ClientListDataModel) {
        val dt = DataStoreManager(this)
        runBlocking { dt.saveClientsData(result) }
    }

    fun Context.getClients(): ClientListDataModel? {
        val dt = DataStoreManager(this)
        return runBlocking { dt.getClientsData().first() }
    }

    fun Context.isEnglishLanguage(): Boolean {
        return getAppConfig()?.lang == "en"
    }

    /**  -----------------------  ------------------------------------  ---------------------------------- */


}
