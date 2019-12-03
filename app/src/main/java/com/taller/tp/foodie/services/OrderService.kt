package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.*
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONArray
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

    fun deliverOrder(order: Order) {
        val jsonRequest = JSONObject()
        jsonRequest.put("status", Order.STATUS.DELIVERED_STATUS.key)
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
        val jsonRequest = JSONObject()
        jsonRequest.put("id_chat", chat.id)
        updateWithOrderId(chat.id_order, jsonRequest)
    }

    private fun updateWithOrderId(orderId: String, body: JSONObject) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        val resource = String.format("%s%s", ORDER_RESOURCE, orderId)
        client.doPatch(resource, listener, body, errorListener)
    }

    private fun update(order: Order, body: JSONObject) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        val resource = String.format("%s%s", ORDER_RESOURCE, order.id)
        client.doPatch(resource, listener, body, errorListener)
    }

    fun listByUser(userType: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }
        // TODO TEMPORAL HASTA Q LO PUEDA FILTRAR EL SERVER
        if (userType == User.USER_TYPE.DELIVERY.name)
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
            val name = json.getString("name")

            // Delivery
            var deliveryUser: DeliveryUser? = null
            if (!json.isNull("delivery")){
                val deliveryJson = json.getJSONObject("delivery")
                val delivery = UserService.fromUserJson(deliveryJson)
                deliveryUser = DeliveryUser(delivery.id!!, delivery.name, delivery.image)
            }

            val order = Order(id).setType(orderType)
                .setStatus(status).setNumber(number).setDelivery(deliveryUser)
                .setName(name)

            if (!withDetail)
                return order
            // Product
            val productsJson = json.getJSONArray("ordered_products")
            val prodsList = mutableListOf<OrderedProduct>()
            for (i in 0 until productsJson.length()) {
                val orderedProd = productsJson.getJSONObject(i)
                prodsList.add(ProductsService.fromOrderedProductJson(orderedProd))
            }

            // Owner
            val ownerJson = json.getJSONObject("owner")
            val owner = UserService.fromUserJson(ownerJson)

            // Chat
            order.setIdChat(json.getString("id_chat"))

            // Quotation
            if (!json.isNull("quotation"))
                order.setQuotation(json.getDouble("quotation"))

            // reputation
            order.setIsDeliveryRated(json.getBoolean("delivery_rated"))
            order.setIsOwnerRated(json.getBoolean("owner_rated"))

            return order.setProducts(prodsList).setOwner(owner).setDelivery(deliveryUser)
        }

        private fun toOrderRequestJson(orderRequest: OrderRequest) : JSONObject{
            val jsonOrder = JSONObject()
            jsonOrder.put("name", orderRequest.name)
            jsonOrder.put("order_type", orderRequest.orderType)
            val jsonOrderProductArray = JSONArray()
            for (prod in orderRequest.orderProduct.products) {
                val prodJson = JSONObject()
                prodJson.put("quantity", prod.quantity)
                prodJson.put("product", prod.productFetched.id)
                jsonOrderProductArray.put(prodJson)
            }

            jsonOrder.put("ordered_products", jsonOrderProductArray)

            // Payment method
            val paymentMethod: String? = if (orderRequest.paymentMethod == null)
                null
            else
                orderRequest.paymentMethod.name
            jsonOrder.put("payment_method", paymentMethod)
            return jsonOrder
        }
    }

    class OrderRequest(
        val name: String,
        val orderType: String,
        val orderProduct: OrderProductRequest,
        val paymentMethod: Order.PAYMENT_METHOD?
    )

    class OrderProductRequest(val products: MutableList<OrderedProduct>)
}