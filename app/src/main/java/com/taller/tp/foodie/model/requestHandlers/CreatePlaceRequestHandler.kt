package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.PlaceService
import com.taller.tp.foodie.ui.ClientMainActivity
import org.json.JSONObject


class CreatePlaceRequestHandler(private val activity: ClientMainActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("CreatePlaceReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.map_choice_context))
    }

    override fun onSuccess(response: JSONObject?) {
        val place = PlaceService.fromPlaceJson(response!!)
        activity.doOrder(place)
    }
}