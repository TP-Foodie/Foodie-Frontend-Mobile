package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.Coordinate
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val AVAILABLE_DELIVERY_RESOURCE = "/available_deliveries"

class DeliveryUserService(ctx: Context, private val requestHandler: RequestHandler) {
    private val client : BackService = BackService(ctx)

    fun availableDeliveries(coordinates: Coordinate) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }

        val resource = String.format("%s%s", AVAILABLE_DELIVERY_RESOURCE,
                                                    buildURIParams(coordinates))
        client.doGetObject(resource, listener, errorListener)
    }

    private fun buildURIParams(coordinates: Coordinate) : String {
        val stringBuilder = StringBuilder("?")
        stringBuilder.append("radius=15")
        stringBuilder.append("&")
        stringBuilder.append(String.format("latitude=%f", coordinates.latitude))
        stringBuilder.append("&")
        stringBuilder.append(String.format("longitude=%f", coordinates.longitude))
        return stringBuilder.toString()
    }

    companion object{
        fun fromAvailableDeliveryJson(json: JSONObject): DeliveryUser{
            val id = json.getString("_id")
            val name = json.getString("name")
            val imageUrl = json.getString("profile_image")
            val coordinatesArrayJSON = json.getJSONArray("coordinates")
            val coordinatesJSON = JSONObject()
            coordinatesJSON.put("latitude", coordinatesArrayJSON.get(0))
            coordinatesJSON.put("longitude", coordinatesArrayJSON.get(1))
            val coordinates = CoordinateService.fromCoordinateJson(coordinatesJSON)
            val deliveryUser = DeliveryUser(id, name, imageUrl)
            deliveryUser.coordinates = coordinates
            return deliveryUser
        }
    }
}
