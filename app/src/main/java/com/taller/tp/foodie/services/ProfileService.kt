package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class ProfileService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

    companion object {
        // endpoint
        const val ME_RESOURCE = "/users/me"
        const val USERS_RESOURCE = "/users/"
    }

    fun getUserForChat() {
        getUserProfile()
    }

    fun getUserProfile() {
        // setup request handler
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        client.doGetObject(ME_RESOURCE, listener, errorListener)
    }
  
    fun getOtherUserForChat(idUser: String) {
        // setup request handler
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        client.doGetObject(USERS_RESOURCE + idUser, listener, errorListener)
    }
}