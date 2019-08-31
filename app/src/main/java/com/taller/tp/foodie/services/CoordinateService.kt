package com.taller.tp.foodie.services

import android.content.Context
import com.taller.tp.foodie.model.Coordinate
import org.json.JSONObject

class CoordinateService(ctx: Context): BackService(ctx) {

    companion object {
        fun fromCoordinateJson(json:JSONObject) : Coordinate{
            val latitude = json.getDouble("latitude")
            val longitude = json.getDouble("longitude")
            return Coordinate(latitude,longitude)
        }

        fun toCoordinateJson(coordinate: Coordinate): JSONObject {
            val json = JSONObject()
            json.put("latitude", coordinate.latitude)
            json.put("longitude", coordinate.longitude)
            return json
        }
    }


}