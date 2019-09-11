package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val USERS_RESOURCE = "/users/"

class UserService(ctx: Context, private val requestHandler: RequestHandler) {
    private val client : BackService = BackService(ctx)

    fun register(email: String, password: String, userType: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess() }
        val errorListener = Response.ErrorListener { requestHandler.onError() }

        client.doPost(USERS_RESOURCE, listener, buildRequest(email, password, userType), errorListener)
    }

    private fun buildRequest(email: String, password: String, userType: String) : JSONObject {
        val requestObject = JSONObject()
        requestObject.put("email", email)
        requestObject.put("password", password)
        requestObject.put("type", userType)
        return requestObject
    }
}