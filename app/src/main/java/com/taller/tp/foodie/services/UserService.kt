package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class UserService(ctx: Context, private val requestHandler: RequestHandler) {

    private val client : BackService = BackService(ctx)

    companion object {
        // endpoint
        const val USERS_RESOURCE = "/users/"

        // email - password register
        const val EMAIL_FIELD = "email"
        const val PASSWORD_FIELD = "password"
        const val NAME_FIELD = "name"
        const val PHONE_FIELD = "phone"
    }

    fun register(email: String, password: String, name: String, phone: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response: JSONObject? ->
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
        requestObject.put(PHONE_FIELD, phone)

        client.doPost(USERS_RESOURCE, listener, requestObject, errorListener)
    }
}