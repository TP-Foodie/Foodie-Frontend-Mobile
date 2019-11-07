package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.SERVICE_ARRAY_RESPONSE
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.OrdersActivity
import org.json.JSONObject


class ListOrdersRequestHandler(private val activity: OrdersActivity) : RequestHandler {

    private enum class OPERATION { LIST_BY_OWNER, LIST_BY_DELIVERY, FILTER_BY_DELIVERY }
    private var op = OPERATION.LIST_BY_OWNER

    override fun begin() {
        val loadingMessage = activity.getString(R.string.loading_orders)
        activity.findViewById<TextView>(R.id.empty_order_view).setText(loadingMessage)
    }

    fun byDelivery(): ListOrdersRequestHandler{
        op = OPERATION.LIST_BY_DELIVERY
        return this
    }
    fun forFilter(): ListOrdersRequestHandler{
        op = OPERATION.FILTER_BY_DELIVERY
        return this
    }

    override fun onError(error: VolleyError) {
        Log.e("ListOrderssReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.orders_context))
    }

    override fun onSuccess(response: JSONObject?) {
        when(op){
            OPERATION.LIST_BY_OWNER -> {
                val orders: ArrayList<Order> = buildOrderList(response)
                activity.setOrders(orders)
                activity.populateOrders()
            }
            OPERATION.LIST_BY_DELIVERY -> {
                val orders: ArrayList<Order> = buildOrderList(response)
                activity.setOrders(orders)
                activity.askForDelivery()
            }
            OPERATION.FILTER_BY_DELIVERY -> {
                val user = UserService.fromUserJson(response!!)
                activity.filterOrders(user.id!!)
                activity.populateOrders()
            }
        }
    }

    private fun buildOrderList(response: JSONObject?): ArrayList<Order> {
        val ordersResponse = response!!.getJSONArray(SERVICE_ARRAY_RESPONSE)
        val orders: ArrayList<Order> = ArrayList()
        for (i in 0 until ordersResponse.length()) {
            val orderJson = ordersResponse.getJSONObject(i)
            orders.add(OrderService.fromOrderJson(orderJson, withDetail = false))
        }
        return orders
    }
}