package com.taller.tp.foodie.model.common

import android.content.Context
import java.lang.ref.WeakReference

class UserBackendDataHandler(context: Context) {

    companion object Keys {
        const val USER_DATA_RESOURCE_KEY = "userData"

        const val TOKEN = "token"
        const val USER_ID = "userId"
    }

    private val context = WeakReference(context)

    /*
     * Persist Data
     */

    fun persistUserBackendData(token: String?, userId: String?) {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            putString(TOKEN, token)
            putString(USER_ID, userId)
            apply()
        }
    }

    fun persistBackendToken(token: String?) {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            putString(TOKEN, token)
            apply()
        }
    }

    fun persistUserId(userId: String?) {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            putString(USER_ID, userId)
            apply()
        }
    }

    /*
     * Fetch Data
     */

    fun getBackendToken(): String {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)

        return sharedPreferences?.getString(TOKEN, "").toString()
    }

    fun getUserId(): String {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)

        return sharedPreferences?.getString(USER_ID, "").toString()
    }

    /*
     * Delete Data
     */

    fun deleteUserBackendData() {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            remove(TOKEN)
            remove(USER_ID)
            apply()
        }
    }

}
