package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import org.json.JSONObject

const val USERS_RESOURCE = "/users/"

class UserService(ctx: Context) {
    val client : BackService = BackService(ctx)

    fun register(email: String, password: String) {
        val listener = Response.Listener<JSONObject> { response ->
            handleRequest(response)
        }

        client.doPost(USERS_RESOURCE, listener, buildRequest(email, password))
    }

    private fun buildRequest(email: String, password: String) : JSONObject {
        val requestObject = JSONObject()
        requestObject.put("email", email)
        requestObject.put("password", password)
        return requestObject
    }

    private fun handleRequest(response: JSONObject) {}
}