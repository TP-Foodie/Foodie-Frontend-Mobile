package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.*
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val ORDER_RESOURCE = "/orders/"

class OrderService(ctx: Context, private val requestHandler: RequestHandler) {
    private val client : BackService = BackService(ctx)

    fun makeOrder(orderRequest: OrderRequest) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        client.doPost(ORDER_RESOURCE, listener, toOrderRequestJson(orderRequest), errorListener)
    }

    fun assignDelivery(order: Order, deliveryUser: DeliveryUser) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        val resource = String.format("%s%s", ORDER_RESOURCE, order.id)
        client.doPatch(resource, listener, buildAssignDeliveryRequest(deliveryUser), errorListener)
    }

    companion object {
        fun fromOrderJson(json:JSONObject) : Order {
            // General
            val id = json.getString("id")
            val orderType = json.getString("type")
            val status = json.getString("status")

            // Product
            val productJson = json.getJSONObject("product")
            val orderProduct = fromOrderProductJson(productJson)

            return Order(id).setType(orderType)
                                    .setStatus(status)
                                    .setProduct(orderProduct)
        }

        private fun fromOrderProductJson(json: JSONObject) : OrderProduct {
            val productName = json.getString("name")
            val placeJson = json.getJSONObject("place")
            val coordinates =
                CoordinateService.fromCoordinateJson(placeJson.getJSONObject("coordinates"))
            val placeName = placeJson.getString("name")
            val place = Place(placeName, coordinates)
            return OrderProduct(productName, place)
        }

        private fun buildAssignDeliveryRequest(deliveryUser: DeliveryUser) : JSONObject{
            val jsonRequest = JSONObject()
            jsonRequest.put("status",Order.STATUS.TAKEN_STATUS.key)
            jsonRequest.put("delivery", deliveryUser.id)
            return jsonRequest
        }

        private fun toOrderRequestJson(orderRequest: OrderRequest) : JSONObject{
            val jsonOrder = JSONObject()
            jsonOrder.put("order_type", orderRequest.orderType)
            val jsonOrderProduct = JSONObject()
            jsonOrderProduct.put("name", orderRequest.orderProduct.product)
            jsonOrderProduct.put("place", orderRequest.orderProduct.placeId)
            jsonOrder.put("product",jsonOrderProduct)
            return jsonOrder
        }
    }

    class OrderRequest(val orderType: String, val orderProduct: OrderProductRequest)

    class OrderProductRequest(val product: String, val placeId: String)
}