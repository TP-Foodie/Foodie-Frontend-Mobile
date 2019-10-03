package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.services.PlaceService
import com.taller.tp.foodie.services.SERVICE_ARRAY_RESPONSE
import com.taller.tp.foodie.ui.ClientMainActivity
import org.json.JSONObject


class ListPlacesRequestHandler(private val activity: ClientMainActivity) : RequestHandler {
    override fun begin() {}

    override fun onError() {
        ErrorHandler.handleError(activity.findViewById<View>(R.id.map_choice_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val placesResponse = response!!.getJSONArray(SERVICE_ARRAY_RESPONSE)
        val places : ArrayList<Place> = ArrayList()
        for (i in 0..placesResponse.length()-1) {
            val placeJson = placesResponse.getJSONObject(i)
            places.add(PlaceService.fromPlaceJson(placeJson))
        }
        activity.configureMapWithPlaces(places)
    }
}