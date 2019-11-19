package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val PLACE_RESOURCE = "/places/"

class PlaceService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

    fun list(){
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }
        client.doGetArray(PLACE_RESOURCE, listener, errorListener)
    }

    companion object {
        fun fromPlaceJson(json:JSONObject) : Place {
            val id = json.getString("id")
            val name = json.getString("name")
            val coordinateJson = json.getJSONObject("coordinates")
            val coordinate = CoordinateService.fromCoordinateJson(coordinateJson)
            val image = json.getString("image")
            return Place(name, coordinate, image).setId(id)
        }
    }

}