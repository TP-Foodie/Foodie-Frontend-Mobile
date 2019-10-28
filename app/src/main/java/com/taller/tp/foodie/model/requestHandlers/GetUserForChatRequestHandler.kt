package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.UserProfileFetched
import com.taller.tp.foodie.ui.ChatActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class GetUserForChatRequestHandler(private val activity: WeakReference<ChatActivity>) :
    RequestHandler {

    override fun begin() {}

    override fun onSuccess(response: JSONObject?) {
        val userProfile = Gson().fromJson(response.toString(), UserProfileFetched::class.java)

        activity.get()?.updateMyData(userProfile)
    }

    override fun onError(error: VolleyError) {
        Log.e("GetUserChatReqHandler", "Volley error: " + error.message)
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.chat_layout)!!)
    }
}