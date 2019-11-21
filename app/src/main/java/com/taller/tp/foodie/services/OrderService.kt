package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.*
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val ORDER_RESOURCE = "/orders/"
const val ORDER_PLACED_RESOURCE = "/orders/placed"

class OrderService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

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

    fun assignChat(chat: ChatFetched) {
        updateWithOrderId(chat.id_order, buildAssignChatRequest(chat))
    }

    private fun updateWithOrderId(orderId: String, body: JSONObject) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        val resource = String.format("%s%s", ORDER_RESOURCE, orderId)
        client.doPatch(resource, listener, body, errorListener)
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

    fun listByUser(userType: User.USER_TYPE){
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }
        // TODO TEMPORAL HASTA Q LO PUEDA FILTRAR EL SERVER
        if (userType == User.USER_TYPE.DELIVERY)
            client.doGetArray(ORDER_RESOURCE, listener, errorListener)
        else
            client.doGetArray(ORDER_PLACED_RESOURCE, listener, errorListener)

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

            // Delivery
            var deliveryUser: DeliveryUser? = null
            if (!json.isNull("delivery")){
                val deliveryJson = json.getJSONObject("delivery")
                val delivery = UserService.fromUserJson(deliveryJson)
                deliveryUser = DeliveryUser(delivery.id!!, delivery.name, delivery.image)
            }

            val order = Order(id).setType(orderType)
                .setStatus(status).setNumber(number).setDelivery(deliveryUser)

            if (!withDetail)
                return order
            // Product
            val productJson = json.getJSONObject("product")
            val orderProduct = fromOrderProductJson(productJson)

            // Owner
            val ownerJson = json.getJSONObject("owner")
            val owner = UserService.fromUserJson(ownerJson)

            // Chat
            order.setIdChat(json.getString("id_chat"))

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

        private fun buildAssignChatRequest(chat: ChatFetched): JSONObject {
            val jsonRequest = JSONObject()
            jsonRequest.put("id_chat", chat.id)
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
            val paymentMethod: String?
            if (orderRequest.paymentMethod == null)
                paymentMethod = null
            else
                paymentMethod = orderRequest.paymentMethod.name
            jsonOrder.put("payment_method", paymentMethod)
            return jsonOrder
        }
    }

    class OrderRequest(val orderType: String,
                       val orderProduct: OrderProductRequest,
                       val paymentMethod: Order.PAYMENT_METHOD?)

    class OrderProductRequest(val product: String, val placeId: String)
}