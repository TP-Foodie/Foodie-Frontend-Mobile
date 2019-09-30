package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject


class AuthService(ctx: Context, private val requestHandler: RequestHandler) {

    private val client = BackService(ctx)

    companion object {
        // endpoint
        const val AUTH_RESOURCE = "/auth/"

        // federated auth
        const val TOKEN_FIELD = "token"

        // email - password auth
        const val EMAIL_FIELD = "email"
        const val PASSWORD_FIELD = "password"
    }

    fun federatedAuthenticationWithBackend(token: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response: JSONObject? ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        // build json request
        val requestObject = JSONObject()
        requestObject.put(TOKEN_FIELD, token)

        client.doPost(AUTH_RESOURCE, listener, requestObject, errorListener)
    }

    fun emailAndPasswordAuthenticationWithBackend(email: String, password: String) {
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

        client.doPost(AUTH_RESOURCE, listener, requestObject, errorListener)
    }
}