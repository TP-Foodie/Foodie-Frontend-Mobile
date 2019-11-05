package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ChatActivity
import org.json.JSONObject

class SendMessageRequestHandler(private val activity: ChatActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("SendMessagesReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.chat_layout))
        activity.chatMessageNotSentUpdateUI()
    }

    override fun onSuccess(response: JSONObject?) {
        activity.chatMessageSentUpdateUI()
    }
}