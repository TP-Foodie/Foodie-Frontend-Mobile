package com.taller.tp.foodie.services

import android.content.Context
import android.graphics.Bitmap
import com.android.volley.Response
import com.taller.tp.foodie.model.common.ImageStringConversor
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject
import java.lang.ref.WeakReference

class UserService(ctx: Context, private val requestHandler: RequestHandler) {

    private val client : BackService = BackService(ctx)
    private val context = WeakReference(ctx)

    companion object {
        // endpoint
        const val USERS_RESOURCE = "/users/"

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
    }

    fun register(
        email: String, password: String, name: String, lastName: String, phone: String,
        image: Bitmap?
    ) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        // build json request
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

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        // build json request
        val requestObject = JSONObject()
        requestObject.put(TYPE_FIELD, userType)
        requestObject.put(SUBSCRIPTION_FIELD, subscription)

        // add user id to the users endpoint
        val userId = UserBackendDataHandler(context.get()!!).getUserId()

        client.doPatch(USERS_RESOURCE + userId, listener, requestObject, errorListener)
    }
}