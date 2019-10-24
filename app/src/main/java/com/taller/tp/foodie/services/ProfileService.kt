package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject
import java.lang.ref.WeakReference

class ProfileService(ctx: Context, private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance(ctx)
    private val context = WeakReference(ctx)

    companion object {
        // endpoint
        const val PROFILE_RESOURCE = "/profiles/"
    }

    fun getUserProfile(userId: String?) {
        // setup request handler
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        if (userId.isNullOrEmpty()) {
            client.doGetObject(
                PROFILE_RESOURCE + UserBackendDataHandler(context.get()!!).getUserId(),
                listener, errorListener
            )
        } else {
            client.doGetObject(PROFILE_RESOURCE + userId, listener, errorListener)
        }
    }
}