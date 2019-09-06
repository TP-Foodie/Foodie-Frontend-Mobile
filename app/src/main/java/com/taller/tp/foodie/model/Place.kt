package com.taller.tp.foodie.model

import android.content.Context
import com.taller.tp.foodie.services.PlaceService

class Place(val id: String, val name: String, val coordinate: Coordinate){
    companion object {
        fun listPlaces(ctx: Context, onResponse: (ArrayList<Place>) -> Unit){
            PlaceService(ctx).list(onResponse)
        }

        fun choosePlace(ctx: Context, place: Place, onResponse: (Any) -> Unit) {
            PlaceService(ctx).choosePlace(place, onResponse)
        }
    }

}