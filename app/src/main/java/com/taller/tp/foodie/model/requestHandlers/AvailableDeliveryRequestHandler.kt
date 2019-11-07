package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.DeliveryUserService
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.ChooseDeliveryActivity
import org.json.JSONObject


class AvailableDeliveryRequestHandler(private val activity: ChooseDeliveryActivity) : RequestHandler {
    private enum class OPERATION { LIST, GET_DETAIL }
    private var op: OPERATION = OPERATION.LIST
    private var deliveryId: String? = null

    override fun begin() {}

    fun forDetail(deliveryId: String) : AvailableDeliveryRequestHandler{
        op = OPERATION.GET_DETAIL
        this.deliveryId = deliveryId
        return this
    }

    override fun onError(error: VolleyError) {
        Log.e("AvailableDeliveryReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.choose_delivery_context))
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
                val deliveryUser = DeliveryUser(delivery.id!!, delivery.name, delivery.image)

                activity.onMarkerSetDetail(deliveryUser)
            }
        }
    }
}