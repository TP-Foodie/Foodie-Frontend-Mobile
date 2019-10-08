package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.services.PlaceService
import com.taller.tp.foodie.services.SERVICE_ARRAY_RESPONSE
import com.taller.tp.foodie.ui.ClientMainActivity
import org.json.JSONObject


class ListPlacesRequestHandler(private val activity: ClientMainActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("ListPlacesReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.map_choice_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val placesResponse = response!!.getJSONArray(SERVICE_ARRAY_RESPONSE)
        val places : ArrayList<Place> = ArrayList()
        for (i in 0 until placesResponse.length()) {
            val placeJson = placesResponse.getJSONObject(i)
            places.add(PlaceService.fromPlaceJson(placeJson))
        }
        activity.configureMapWithPlaces(places)
    }
}