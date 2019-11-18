package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.DeliveryUserService
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.ConfirmOrderActivity
import org.json.JSONObject


class ConfirmOrderRequestHandler(private val activity: ConfirmOrderActivity) : RequestHandler {
    private enum class OPERATION { LIST, GET_DETAIL, GET_PRICE }
    private var op: OPERATION = OPERATION.LIST
    private var deliveryId: String? = null

    override fun begin() {}

    fun forDetail(deliveryId: String) : ConfirmOrderRequestHandler{
        op = OPERATION.GET_DETAIL
        this.deliveryId = deliveryId
        return this
    }

    fun forGetPrice() : ConfirmOrderRequestHandler{
        op = OPERATION.GET_PRICE
        return this
    }

    override fun onError(error: VolleyError) {
        Log.e("AvailableDeliveryReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.confirm_order_context))
    }

    override fun onSuccess(response: JSONObject?) {
        when(op){
            OPERATION.LIST -> {
                val deliveriesResponse = response!!.getJSONArray("body")
                val deliveries : ArrayList<DeliveryUser> = ArrayList()
                for (i in 0 until deliveriesResponse.length()) {
                    val deliveryJson = deliveriesResponse.getJSONObject(i)
                    deliveries.add(DeliveryUserService.fromAvailableDeliveryJson(deliveryJson))
                }
                activity.configureMapWithDeliveries(deliveries)
            }
            OPERATION.GET_DETAIL -> {
                response!!.put("id", deliveryId)
                val delivery = UserService.fromUserJson(response)
                activity.onMarkerSetDetail(delivery)
            }
            OPERATION.GET_PRICE -> {
                val price = response!!.getDouble("price")
                activity.updatePrice(price)
            }
        }
    }
}