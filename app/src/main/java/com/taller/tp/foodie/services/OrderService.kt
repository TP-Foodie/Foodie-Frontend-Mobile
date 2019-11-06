package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.OrderProduct
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val ORDER_RESOURCE = "/orders/"

class OrderService(ctx: Context, private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance(ctx)

    fun makeOrder(orderRequest: OrderRequest) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        client.doPost(ORDER_RESOURCE, listener, toOrderRequestJson(orderRequest), errorListener)
    }

    fun assignDelivery(order: Order, deliveryUser: DeliveryUser) {
        update(order, buildAssignDeliveryRequest(deliveryUser))
    }

    fun updateStatus(order: Order, status: Order.STATUS) {
        update(order, buildUpdateStatusRequest(order,status))
    }

    fun update(order: Order, body: JSONObject) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        val resource = String.format("%s%s", ORDER_RESOURCE, order.id)
        client.doPatch(resource, listener, body, errorListener)
    }

    private fun buildUpdateStatusRequest(order: Order, status: Order.STATUS): JSONObject {
        val jsonRequest = JSONObject()
        jsonRequest.put("status", status.key)
        jsonRequest.put("delivery", order.getDelivery()!!.id)
        return jsonRequest
    }

    fun list(){
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }
        client.doGetArray(ORDER_RESOURCE, listener, errorListener)
    }

    fun find(orderId: String){
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }
        client.doGetObject(ORDER_RESOURCE+orderId, listener, errorListener)
    }

    companion object {
        fun fromOrderJson(json:JSONObject, withDetail: Boolean = true) : Order {
            // General
            val id = json.getString("id")
            val orderType = json.getString("type")
            val status = json.getString("status")
            val number = json.getInt("number")

            val order = Order(id).setType(orderType)
                .setStatus(status).setNumber(number)

            if (!withDetail)
                return order
            // Product
            val productJson = json.getJSONObject("product")
            val orderProduct = fromOrderProductJson(productJson)

            // Owner
            val ownerJson = json.getJSONObject("owner")
            val owner = UserService.fromUserJson(ownerJson)

            // Delivery
            var deliveryUser: DeliveryUser? = null
            if (!json.isNull("delivery")){
                val deliveryJson = json.getJSONObject("delivery")
                val delivery = UserService.fromUserJson(deliveryJson)
                deliveryUser = DeliveryUser(delivery.id!!, delivery.name, delivery.image)
            }

            return order.setProduct(orderProduct).setOwner(owner).setDelivery(deliveryUser)
        }

        private fun fromOrderProductJson(json: JSONObject): OrderProduct {
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
            // Payment method
//            val paymentMethod: String?
//            if (orderRequest.paymentMethod == null)
//                paymentMethod = null
//            else
//                paymentMethod = orderRequest.paymentMethod.name
//            jsonOrder.put("payment_method", paymentMethod)
            return jsonOrder
        }
    }

    class OrderRequest(val orderType: String,
                       val orderProduct: OrderProductRequest)
//                       val paymentMethod: Order.PAYMENT_METHOD?)

    class OrderProductRequest(val product: String, val placeId: String)
}