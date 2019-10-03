package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.Coordinate
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONArray
import org.json.JSONObject

const val AVAILABLE_DELIVERY_RESOURCE = "/available_delivery/"

class DeliveryUserService(ctx: Context, private val requestHandler: RequestHandler) {
    private val client : BackService = BackService(ctx)

    fun availableDeliveries(coordinates: Coordinate) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError() }

        client.doGetArray(AVAILABLE_DELIVERY_RESOURCE, listener, buildRequest(coordinates), errorListener)
    }

    private fun buildRequest(coordinates: Coordinate) : JSONObject {
        val requestObject = JSONObject()
        requestObject.put("radius", 15)
        val requestArray = JSONArray()
        requestArray.put(coordinates.latitude)
        requestArray.put(coordinates.longitude)
        requestObject.put("coordinates", requestArray)
        return requestObject
    }

    companion object{
        fun fromAvailableDeliveryJson(json: JSONObject): DeliveryUser{
            val id = json.getString("_id")
            val name = json.getString("name")
            val imageUrl = json.getString("profile_image")
            val coordinatesJSON = json.getJSONObject("coordinates")
            val coordinates = CoordinateService.fromCoordinateJson(coordinatesJSON)
            val deliveryUser = DeliveryUser(id, name, imageUrl)
            deliveryUser.coordinates = coordinates
            return deliveryUser
        }
    }
}
