package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.Coordinate
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val AVAILABLE_DELIVERY_RESOURCE = "/available_deliveries"

class DeliveryUserService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

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
            val id = json.getString("id")
            val name = json.getString("name")
            val imageUrl = json.getString("profile_image")
            val coordinatesJSON = json.getJSONObject("location")
            val coordinates = CoordinateService.fromCoordinateJson(coordinatesJSON)
            val deliveryUser = DeliveryUser(id, name, imageUrl)
            deliveryUser.coordinates = coordinates
            return deliveryUser
        }
    }
}
