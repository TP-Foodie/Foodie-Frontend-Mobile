package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.Coordinate
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

const val PLACE_RESOURCE = "/places/"

class PlaceService(ctx: Context, private val requestHandler: RequestHandler){

    private val client = BackService.getInstance(ctx)

    fun list(){
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val errorListener = Response.ErrorListener { requestHandler.onError(it) }
        client.doGetArray(PLACE_RESOURCE, listener, errorListener)
    }

    fun create(coordinate: Coordinate, name: String){
        requestHandler.begin()

        val onSuccess = Response.Listener<JSONObject> { requestHandler.onSuccess(it) }
        val onError = Response.ErrorListener { requestHandler.onError(it) }
        client.doPost(PLACE_RESOURCE, onSuccess, buildRequest(coordinate, name), onError)
    }

    companion object {
        fun fromPlaceJson(json:JSONObject) : Place {
            val id = json.getString("id")
            val name = json.getString("name")
            val coordinateJson = json.getJSONObject("coordinates")
            val coordinate = CoordinateService.fromCoordinateJson(coordinateJson)
            return Place(name,coordinate).setId(id)
        }
    }

    private fun buildRequest(coordinate: Coordinate, name: String) : JSONObject{
        val place = JSONObject()
        place.put("name", name)
        place.put("coordinate",CoordinateService.toCoordinateJson(coordinate))
        return place
    }


}