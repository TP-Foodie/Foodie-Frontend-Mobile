package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.OrderProduct
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val ORDER_RESOURCE = "/orders/"

class OrderService(ctx: Context, private val requestHandler: RequestHandler) {
    private val client : BackService = BackService(ctx)

    fun makeOrder(order: Order) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError() }

        client.doPost(ORDER_RESOURCE, listener, toOrderJson(order), errorListener)
    }

    companion object {
        fun fromOrderJson(json:JSONObject) : Order {
//            val id = json.getString("id")
            val orderType = json.getString("order_type")
            val owner = json.getString("owner")
            val product = json.getJSONObject("product")
            val productName = product.getString("name")
            val productPlace = product.getString("place")
            val orderProduct = OrderProduct(productName, productPlace)
            return Order(orderType, owner, orderProduct)
        }
        fun toOrderJson(order: Order) : JSONObject{
            val jsonOrder = JSONObject()
            jsonOrder.put("order_type", order.orderType)
            jsonOrder.put("owner", order.owner)
            val jsonOrderProduct = JSONObject()
            jsonOrderProduct.put("name", order.orderProduct.product)
            jsonOrderProduct.put("place", order.orderProduct.placeId)
            jsonOrder.put("product",jsonOrderProduct)
            return jsonOrder
        }
    }
}