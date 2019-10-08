package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.DeliveryUserService
import com.taller.tp.foodie.ui.ChooseDeliveryActivity
import org.json.JSONObject


class AvailableDeliveryRequestHandler(private val activity: ChooseDeliveryActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("AvailableDeliveryReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.choose_delivery_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val deliveriesResponse = response!!.getJSONArray("body")
        val deliveries : ArrayList<DeliveryUser> = ArrayList()
        for (i in 0 until deliveriesResponse.length()) {
            val deliveryJson = deliveriesResponse.getJSONObject(i)
            deliveries.add(DeliveryUserService.fromAvailableDeliveryJson(deliveryJson))
        }
        activity.configureMapWithDeliveries(deliveries)
    }
}