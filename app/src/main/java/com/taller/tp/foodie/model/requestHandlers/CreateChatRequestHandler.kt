package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ChatFetched
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ConfirmOrderActivity
import org.json.JSONObject

class CreateChatRequestHandler(private val activity: ConfirmOrderActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("CreateChatReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.confirm_order_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val chat = Gson().fromJson(response.toString(), ChatFetched::class.java)
        activity.assignChat(chat)
    }
}