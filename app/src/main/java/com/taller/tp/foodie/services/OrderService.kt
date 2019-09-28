package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.Order
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
//        fun fromOrderJson(json:JSONObject) : Place {
//            val id = json.getString("id")
//            val name = json.getString("name")
//            val coordinateJson = json.getJSONObject("coordinate")
//            val coordinate = CoordinateService.fromCoordinateJson(coordinateJson)
//            return Place(id,name,coordinate)
//        }
        fun toOrderJson(order: Order) : JSONObject{
            val jsonOrder = JSONObject()
            jsonOrder.put("order_type", order.orderType)
            jsonOrder.put("owner", order.owner)
            val jsonOrderProduct = JSONObject()
            jsonOrderProduct.put("name", order.orderProduct.product)
            jsonOrderProduct.put("place", order.orderProduct.place.id)
            jsonOrder.put("product",jsonOrderProduct)
            return jsonOrder
        }
    }
}