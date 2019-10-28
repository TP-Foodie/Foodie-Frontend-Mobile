package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ChatFetched
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ChatActivity
import org.json.JSONObject

class GetChatRequestHandler(private val activity: ChatActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("GetChatReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.chat_layout))
    }

    override fun onSuccess(response: JSONObject?) {
        if (response == null) {
            ErrorHandler.handleError(activity.findViewById<View>(R.id.chat_layout))
            return
        }

        activity.updateChat(Gson().fromJson(response.toString(), ChatFetched::class.java))
    }
}