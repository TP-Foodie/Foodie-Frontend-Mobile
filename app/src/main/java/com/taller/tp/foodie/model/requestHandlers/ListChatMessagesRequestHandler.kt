package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ChatMessage
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.SERVICE_ARRAY_RESPONSE
import com.taller.tp.foodie.ui.ChatActivity
import org.json.JSONObject

class ListChatMessagesRequestHandler(private val activity: ChatActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("ListChatMessagesReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.chat_layout))
    }

    override fun onSuccess(response: JSONObject?) {
        if (response == null) {
            ErrorHandler.handleError(activity.findViewById<View>(R.id.chat_layout))
            return
        }

        val listResponse = response.getJSONArray(SERVICE_ARRAY_RESPONSE)
        val messages = mutableListOf<ChatMessage>()
        for (i in 0 until listResponse.length()) {
            messages.add(
                Gson().fromJson(
                    listResponse.getJSONObject(i).toString(),
                    ChatMessage::class.java
                )
            )
        }

        activity.updateChatMessages(messages)
    }
}