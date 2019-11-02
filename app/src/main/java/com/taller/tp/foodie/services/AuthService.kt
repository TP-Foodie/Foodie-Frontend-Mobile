package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class AuthService(ctx: Context, private val requestHandler: RequestHandler) {

    private val client = BackService(ctx)

    companion object {
        // endpoints
        const val GOOGLE_AUTH_RESOURCE = "/auth/google"
        const val AUTH_RESOURCE = "/auth/"
        const val RECOVERY_TOKEN = "/auth/recovery_token"
        const val UPDATE_PASSWORD = "/auth/password"
        const val USERS_RESOURCE = "/users/"
        const val ME_RESOURCE = "/users/me"

        // federated auth
        const val GOOGLE_TOKEN_FIELD = "google_token"

        // email - password auth
        const val EMAIL_FIELD = "email"
        const val PASSWORD_FIELD = "password"
        const val TOKEN_FIELD = "recovery_token"
    }

    fun federatedAuthenticationWithBackend(token: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        // build json request
        val requestObject = JSONObject().put(GOOGLE_TOKEN_FIELD, token)

        client.doPost(GOOGLE_AUTH_RESOURCE, listener, requestObject, errorListener)
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

    fun checkIfFederatedIsRegistered(userToken: String?) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response: JSONObject? ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        client.doGetObject(ME_RESOURCE, listener, errorListener)
    }

    fun checkIfUserIsRegistered(userToken: String) {
        checkIfFederatedIsRegistered(userToken)
    }

    fun sendToken(email: String) {
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

        client.doPost(RECOVERY_TOKEN, listener, requestObject, errorListener)
    }

    fun updatePassowrd(email: String, password: String, token: String) {
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
        requestObject.put(TOKEN_FIELD, token)

        client.doPost(UPDATE_PASSWORD, listener, requestObject, errorListener)
    }
}