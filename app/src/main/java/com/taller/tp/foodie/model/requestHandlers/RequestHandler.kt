package com.taller.tp.foodie.model.requestHandlers

import com.android.volley.VolleyError
import org.json.JSONObject


interface RequestHandler {
    fun begin()

    fun onSuccess(response: JSONObject?)

    fun onError(error: VolleyError)
}