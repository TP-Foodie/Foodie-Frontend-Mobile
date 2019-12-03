package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.ui.OrdersFragment
import org.json.JSONArray
import org.json.JSONObject


class ListOrdersRequestHandler(private val fragment: OrdersFragment) : RequestHandler {


    override fun begin() {
    }

    override fun onError(error: VolleyError) {
        Log.e("ListOrdersReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(fragment.activity?.findViewById(R.id.container)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        val userId = response!!.getString("user_id")
        val orders: ArrayList<Order> = buildOrderList(response.getJSONArray("orders"))
        fragment.populateData(userId, orders)
    }

    private fun buildOrderList(response: JSONArray): ArrayList<Order> {
        val orders: ArrayList<Order> = ArrayList()
        for (i in 0 until response.length()) {
            val orderJson = response.getJSONObject(i)
            orders.add(OrderService.fromOrderJson(orderJson, withDetail = false))
        }
        return orders
    }
}