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
        const val USER_TYPE = "userType"
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

    fun persistUserType(userType: String) {
        val mContext = MyApplication.getContext()
        val sharedPreferences =
            mContext.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)
        with(sharedPreferences!!.edit()) {
            putString(USER_TYPE, userType)
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


    fun getUserType(): String {
        val mContext = MyApplication.getContext()
        val sharedPreferences =
            mContext.getSharedPreferences(USER_DATA_RESOURCE_KEY, Context.MODE_PRIVATE)

        return sharedPreferences?.getString(USER_TYPE, "").toString()
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
            remove(USER_TYPE)
            apply()
        }
    }

}
