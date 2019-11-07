package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.ui.ChooseDeliveryActivity
import org.json.JSONObject


class AssignOrderDeliveryRequestHandler(private val activity: ChooseDeliveryActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("AssignOrderDeliveryReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.choose_delivery_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val order = Gson().fromJson(response.toString(), Order::class.java)
        activity.createChat(order)
    }
}