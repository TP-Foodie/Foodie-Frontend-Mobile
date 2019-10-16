package com.taller.tp.foodie.model.common

import android.content.Context
import java.lang.ref.WeakReference

class UserBackendDataHandler(context: Context) {

    companion object Keys {
        const val USER_DATA_RESOURCE_KEY = "userData"

        const val TOKEN = "token"
    }

    private val context = WeakReference(context)

    /*
     * Persist Data
     */

    fun persistUserBackendData(token: String?) {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            putString(TOKEN, token)
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


    /*
     * Delete Data
     */

    fun deleteUserBackendData() {
        val sharedPreferences =
            context.get()?.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            remove(TOKEN)
            apply()
        }
    }

}
