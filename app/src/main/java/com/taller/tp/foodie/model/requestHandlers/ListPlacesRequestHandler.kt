package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.services.PlaceService
import com.taller.tp.foodie.ui.ClientMainActivity
import org.json.JSONObject


class ListPlacesRequestHandler(private val activity: ClientMainActivity) : RequestHandler {

    override fun begin() {}

    override fun onError() {
        ErrorHandler.handleError(activity.findViewById<View>(R.id.map_choice_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val places : ArrayList<Place> = ArrayList()
        val jsonArray = response!!.getJSONArray("places")
        for (i in 0..jsonArray.length()-1) {
            val placeJson = jsonArray.getJSONObject(i)
            places.add(PlaceService.fromPlaceJson(placeJson))
        }
        activity.configureMapWithPlaces(places)
    }
}