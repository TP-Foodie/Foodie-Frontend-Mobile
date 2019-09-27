package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val ORDER_RESOURCE = "/orders/"

class OrderService(ctx: Context, private val requestHandler: RequestHandler) {
    private val client : BackService = BackService(ctx)

    fun makeOrder(owner: String, product: String, place: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess() }
        val errorListener = Response.ErrorListener { requestHandler.onError() }

        client.doPost(ORDER_RESOURCE, listener, buildRequest(owner,product, place),
                      errorListener)
    }

    private fun buildRequest(owner: String, product: String, place: String) : JSONObject {
        val requestObject = JSONObject()
        requestObject.put("order_type", "NT")
        requestObject.put("owner", owner)
        val productRequestObject = JSONObject()
        productRequestObject.put("name", product)
        productRequestObject.put("place", place)
        requestObject.put("product", productRequestObject)
        return requestObject
    }
}