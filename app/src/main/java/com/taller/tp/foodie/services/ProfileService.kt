package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class ProfileService(ctx: Context, private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance(ctx)

    companion object {
        // endpoint
        const val ME_RESOURCE = "/users/me"
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
}