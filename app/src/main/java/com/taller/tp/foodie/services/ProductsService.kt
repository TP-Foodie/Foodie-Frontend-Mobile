package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class ProductsService(private val requestHandler: RequestHandler) {

    companion object {
        const val PRODUCTS_RESOURCE = "/products/"
    }

    private val client = BackService.getInstance()

    fun list(placeId: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        val resource = String.format("%s%s", PRODUCTS_RESOURCE, buildURIParams(placeId))
        client.doGetArray(resource, listener, errorListener)
    }

    private fun buildURIParams(placeId: String): String {
        return "?id_place=$placeId"
    }
}