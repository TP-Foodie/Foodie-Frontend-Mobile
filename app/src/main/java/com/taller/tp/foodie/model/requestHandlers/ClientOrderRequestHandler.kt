package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.ui.OrderDataActivity
import org.json.JSONObject


class ClientOrderRequestHandler(private val activity: OrderDataActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        if (error.networkResponse.statusCode == 400){
            ErrorHandler.handleError(activity.findViewById<View>(R.id.order_data_layout), "No posee suficientes puntos de gratitud")
            return
        }
        Log.e("ClientOrderReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.order_data_layout))
    }

    override fun onSuccess(response: JSONObject?) {
        val order = OrderService.fromOrderJson(response!!)
        if (order.isFavour())
            activity.saveAndReturnMain()
        else
            activity.saveAndChooseDelivery(response)
    }
}