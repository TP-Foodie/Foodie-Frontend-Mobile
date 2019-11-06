package com.taller.tp.foodie.model.common

import android.content.Context
import com.taller.tp.foodie.MyApplication

class UserBackendDataHandler {

    companion object {
        @Volatile
        private var INSTANCE: UserBackendDataHandler? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserBackendDataHandler().also {
                    INSTANCE = it
                }
            }

        const val USER_DATA_RESOURCE_KEY = "userData"

        const val TOKEN = "token"
    }

    /*
     * Persist Data
     */

    fun persistUserBackendData(token: String?) {
        val mContext = MyApplication.getContext()
        val sharedPreferences =
            mContext.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            putString(TOKEN, token)
            apply()
        }
    }

    /*
     * Fetch Data
     */

    fun getBackendToken(): String {
        val mContext = MyApplication.getContext()
        val sharedPreferences =
            mContext.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)

        return sharedPreferences?.getString(TOKEN, "").toString()
    }


    /*
     * Delete Data
     */

    fun deleteUserBackendData() {
        val mContext = MyApplication.getContext()
        val sharedPreferences =
            mContext.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            remove(TOKEN)
            apply()
        }
    }

}
