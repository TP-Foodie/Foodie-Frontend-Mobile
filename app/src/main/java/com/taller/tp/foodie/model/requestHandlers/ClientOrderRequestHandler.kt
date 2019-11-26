package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.OrderDataActivity
import org.json.JSONObject


class ClientOrderRequestHandler(private val activity: OrderDataActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("ClientOrderReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.order_data_layout))
    }

    override fun onSuccess(response: JSONObject?) {
        activity.saveAndChooseDelivery(response!!)
    }
}