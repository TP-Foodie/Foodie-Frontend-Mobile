package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import org.json.JSONObject

class UpdateFcmTokenRequestHandler() : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("UpdateFcmTokReqHand", "Volley error: " + error.localizedMessage)
    }

    override fun onSuccess(response: JSONObject?) {
        Log.d("UpdateFcmTokReqHand", "Fcm Token updated succesfully.")
    }
}