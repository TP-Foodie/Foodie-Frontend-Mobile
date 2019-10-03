package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.DeliveryUserService
import com.taller.tp.foodie.services.SERVICE_ARRAY_RESPONSE
import com.taller.tp.foodie.ui.ChooseDeliveryActivity
import org.json.JSONObject


class AvailableDeliveryRequestHandler(private val activity: ChooseDeliveryActivity) : RequestHandler {
    override fun begin() {}

    override fun onError() {
        ErrorHandler.handleError(activity.findViewById<View>(R.id.choose_delivery_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val deliveriesResponse = response!!.getJSONArray(SERVICE_ARRAY_RESPONSE)
        val deliveries : ArrayList<DeliveryUser> = ArrayList()
        for (i in 0..deliveriesResponse.length()-1) {
            val deliveryJson = deliveriesResponse.getJSONObject(i)
            deliveries.add(DeliveryUserService.fromAvailableDeliveryJson(deliveryJson))
        }
        activity.configureMapWithDeliveries(deliveries)
    }
}