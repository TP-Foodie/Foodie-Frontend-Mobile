package com.taller.tp.foodie.services

import android.content.Context
import com.android.volley.Response
import com.taller.tp.foodie.model.Place
import org.json.JSONObject

class PlaceService(ctx: Context): BackService(ctx) {

    fun list(function: (ArrayList<Place>) -> Unit){
        val listener = Response.Listener<JSONObject> { response ->
            val places : ArrayList<Place> = ArrayList()
            val jsonArray = response.getJSONArray("places")
            for (i in 0..jsonArray.length()-1) {
                val placeJson = jsonArray.getJSONObject(i)
                places.add(fromPlaceJson(placeJson))
            }
            function.invoke(places)
        }
        doGet("/place/list", listener)
    }

    fun choosePlace(place: Place, onResponse: (String) -> Unit) {
        val listener = Response.Listener<JSONObject> { response ->
            onResponse(fromPlaceJson(response).name)
        }
        val toPlaceJson = toPlaceJson(place)
        doPost("/place/choose", listener, toPlaceJson)
    }

    companion object {
        fun fromPlaceJson(json:JSONObject) : Place {
            val id = json.getString("id")
            val name = json.getString("name")
            val coordinateJson = json.getJSONObject("coordinate")
            val coordinate = CoordinateService.fromCoordinateJson(coordinateJson)
            return Place(id,name,coordinate)
        }
        fun toPlaceJson(place: Place) : JSONObject{
            val json = JSONObject()
            json.put("id", place.id)
            json.put("name", place.name)
            json.put("coordinate",CoordinateService.toCoordinateJson(place.coordinate))
            return json
        }
    }


}