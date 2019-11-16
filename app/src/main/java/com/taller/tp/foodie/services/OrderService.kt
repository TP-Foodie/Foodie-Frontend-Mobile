package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.*
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val ORDER_RESOURCE = "/orders/"
const val ORDER_PLACED_RESOURCE = "/orders/placed"
const val ORDER_QUOTATION_RESOURCE = "/quotation"

class OrderService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

    fun makeOrder(orderRequest: OrderRequest) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        client.doPost(ORDER_RESOURCE, listener, toOrderRequestJson(orderRequest), errorListener)
    }

    fun confirmOrder(order: Order, deliveryUser: DeliveryUser) {
        val jsonRequest = JSONObject()
        jsonRequest.put("status",Order.STATUS.TAKEN_STATUS.key)
        jsonRequest.put("delivery", deliveryUser.id)
        jsonRequest.put("quotation", order.getQuotation())
        update(order, jsonRequest)
    }

    fun updateStatus(order: Order, status: Order.STATUS) {
        val jsonRequest = JSONObject()
        jsonRequest.put("status", status.key)
        jsonRequest.put("delivery", order.getDelivery()!!.id)
        update(order, jsonRequest)
    }

    fun cancelOrder(order: Order) {
        val jsonRequest = JSONObject()
        jsonRequest.put("status", Order.STATUS.CANCELLED_STATUS.key)
        update(order, jsonRequest)
    }

    fun unassignOrder(order: Order) {
        val jsonRequest = JSONObject()
        jsonRequest.put("status", Order.STATUS.WAITING_STATUS.key)
        update(order, jsonRequest)
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

    fun getPrice(orderId: String){
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }
        val resource = ORDER_RESOURCE+orderId+ ORDER_QUOTATION_RESOURCE
        client.doGetObject(resource, listener, errorListener)
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

            // Quotation
            if (!json.isNull("quotation"))
                order.setQuotation(json.getDouble("quotation"))

            return order.setProduct(orderProduct).setOwner(owner).setDelivery(deliveryUser)
        }

        fun toJson(order: Order) : JSONObject {
            val orderJson = JSONObject()
            orderJson.put("id", order.id)
            orderJson.put("type", order.getType())
            orderJson.put("status", order.getStatus().key)
            orderJson.put("number", order.getNumber())
            orderJson.put("id_chat", order.getIdChat())
            orderJson.put("quotation", order.getQuotation())

            if (order.getDelivery() != null){
                val delivery = order.getDelivery()!!
                val deliveryJson = JSONObject()
                deliveryJson.put("email", delivery.email)
                deliveryJson.put("id", delivery.id)
                deliveryJson.put("last_name", delivery.lastName)
                deliveryJson.put("name", delivery.name)
                deliveryJson.put("phone", delivery.phone)
                deliveryJson.put("profile_image", delivery.image)
                deliveryJson.put("type", delivery.type)
                orderJson.put("delivery", deliveryJson)
            }

            if (order.getOwner() != null){
                val owner = order.getOwner()!!
                val ownerJson = JSONObject()
                ownerJson.put("email", owner.email)
                ownerJson.put("id", owner.id)
                ownerJson.put("last_name", owner.lastName)
                ownerJson.put("name", owner.name)
                ownerJson.put("phone", owner.phone)
                ownerJson.put("profile_image", owner.image)
                ownerJson.put("type", owner.type)
                orderJson.put("owner", ownerJson)
            }

            val productJson = JSONObject()
            productJson.put("name", order.getProduct())
            val placeJson = JSONObject()
            val coordinatesJson = JSONObject()
            coordinatesJson.put("latitude", order.getPlace().coordinate.latitude)
            coordinatesJson.put("longitude", order.getPlace().coordinate.longitude)
            placeJson.put("coordinates", coordinatesJson)
            placeJson.put("name", order.getPlace().name)
            productJson.put("place", placeJson)
            orderJson.put("product", productJson)
            return orderJson
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

        private fun buildAssignChatRequest(chat: ChatFetched): JSONObject {
            val jsonRequest = JSONObject()
            jsonRequest.put("status", Order.STATUS.TAKEN_STATUS.key)
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