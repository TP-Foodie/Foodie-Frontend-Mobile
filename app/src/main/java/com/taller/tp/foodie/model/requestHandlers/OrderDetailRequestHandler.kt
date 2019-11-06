package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.ui.ClientMainActivity
import com.taller.tp.foodie.ui.OrderDetailActivity
import org.json.JSONObject


open class OrderDetailRequestHandler(private val activity: OrderDetailActivity) : RequestHandler {

    private enum class OPERATION { GET, UPDATE }
    private var op = OPERATION.GET
    override fun begin() {}

    fun forUpdate() : OrderDetailRequestHandler{
        op = OPERATION.UPDATE
        return this
    }

    override fun onError(error: VolleyError) {
        Log.e("OrderDetailReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.order_detail_context))
    }

    override fun onSuccess(response: JSONObject?) {
        when(op){
            OPERATION.GET -> {
                val order = OrderService.fromOrderJson(response!!, withDetail = true)
                activity.populateFields(order)
            }
            OPERATION.UPDATE -> {
                activity.startActivity(Intent(activity, ClientMainActivity::class.java))
            }
        }
    }

}