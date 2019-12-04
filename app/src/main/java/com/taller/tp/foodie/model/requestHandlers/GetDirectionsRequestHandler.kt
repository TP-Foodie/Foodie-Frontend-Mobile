package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.DeliveryNavigationActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class GetDirectionsRequestHandler(private val activity: WeakReference<DeliveryNavigationActivity>) :
    RequestHandler {

    override fun begin() {}

    override fun onSuccess(response: JSONObject?) {
        //val userProfile = Gson().fromJson(response.toString(), UserProfile::class.java)

        activity.get()?.onUpdateDirections(response!!)
    }

    override fun onError(error: VolleyError) {
        Log.e("GetDirectionsReqHandler", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.delivery_navigation_layout)!!)
    }
}