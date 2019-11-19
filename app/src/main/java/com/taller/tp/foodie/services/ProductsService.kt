package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.OrderedProduct
import com.taller.tp.foodie.model.ProductFetched
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class ProductsService(private val requestHandler: RequestHandler) {

    companion object {
        const val PRODUCTS_RESOURCE = "/products/"

        fun fromProductJson(json: JSONObject): ProductFetched {
            val id = json.getString("id")
            val name = json.getString("name")
            val description = json.getString("description")
            val placeJson = json.getJSONObject("place")
            val place = PlaceService.fromPlaceJson(placeJson)
            val price = json.getInt("price")
            val image = json.getString("image")
            return ProductFetched(id, name, description, price, place, image)
        }

        fun fromOrderedProductJson(json: JSONObject): OrderedProduct {
            val quantity = json.getInt("quantity")

            val prodJson = json.getJSONObject("product")
            val prod = fromProductJson(prodJson)
            return OrderedProduct(quantity, prod)
        }
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