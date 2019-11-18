package com.taller.tp.foodie.services

import android.graphics.Bitmap
import com.android.volley.Response
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.common.ImageStringConversor
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class UserService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

    private val listener = Response.Listener<JSONObject> { response ->
        requestHandler.onSuccess(response)
    }
    private val errorListener = Response.ErrorListener { error ->
        requestHandler.onError(error)
    }

    companion object {
        // endpoint
        const val USERS_RESOURCE = "/users/"
        const val ME_RESOURCE = "/users/me"

        // email - password register
        const val EMAIL_FIELD = "email"
        const val PASSWORD_FIELD = "password"
        const val NAME_FIELD = "name"
        const val LAST_NAME_FIELD = "last_name"
        const val PHONE_FIELD = "phone"
        const val PROFILE_IMAGE_FIELD = "profile_image"

        // type and subcription finish register
        const val TYPE_FIELD = "type"
        const val SUBSCRIPTION_FIELD = "subscription"

        const val FCM_TOKEN_FIELD = "fcmToken"

        fun fromUserJson(json: JSONObject): User{
            val id = json.getString("id")
            val email = json.getString("email")
            val lastName = json.getString("last_name")
            val type = json.getString("type")
            val name = json.getString("name")
            val phone = json.optString("phone", null)
            val image = json.getString("profile_image")

            return User(id, name, image)
                .setType(type)
                .setEmail(email)
                .setLastName(lastName)
                .setPhone(phone)
        }

        fun isUserLoggedIn(): Boolean {
            // check token and user id are not empty
            val token = UserBackendDataHandler.getInstance().getBackendToken()

            if (token.isEmpty()) {
                return false
            }

            return true
        }
    }

    fun register(
        email: String, password: String, name: String, lastName: String, phone: String,
        image: Bitmap?
    ) {
        requestHandler.begin()

        val requestObject = JSONObject()
        requestObject.put(EMAIL_FIELD, email)
        requestObject.put(PASSWORD_FIELD, password)
        requestObject.put(NAME_FIELD, name)
        requestObject.put(LAST_NAME_FIELD, lastName)
        requestObject.put(PHONE_FIELD, phone)

        if (image != null) {
            requestObject.put(
                PROFILE_IMAGE_FIELD,
                ImageStringConversor().imageToBase64String(image)
            )
        }

        client.doPost(USERS_RESOURCE, listener, requestObject, errorListener)
    }

    fun finishRegister(userType: String, subscription: String) {
        requestHandler.begin()

        val requestObject = JSONObject()
        requestObject.put(TYPE_FIELD, userType)
        requestObject.put(SUBSCRIPTION_FIELD, subscription)

        client.doPatch(ME_RESOURCE, listener, requestObject, errorListener)
    }

    fun updateUserFcmToken(fcmToken: String) {
        requestHandler.begin()

        val requestObject = JSONObject()
        requestObject.put(FCM_TOKEN_FIELD, fcmToken)

        client.doPatch(ME_RESOURCE, listener, requestObject, errorListener)
    }

    fun updateLocation() {
        requestHandler.begin()

        val requestObject = JSONObject(mapOf(
            "location" to mapOf(
                "latitude" to 1,
                "longitude" to 1
            )
        ))

        client.doPatch(ME_RESOURCE, listener, requestObject, errorListener)
    }


    fun changeUserProfile(data: Map<String, String>) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        // build json request
        val requestObject = JSONObject()
        for ((key, value) in data) {
            requestObject.put(key, value)
        }

        client.doPatch(ME_RESOURCE, listener, requestObject, errorListener)
    }
}
