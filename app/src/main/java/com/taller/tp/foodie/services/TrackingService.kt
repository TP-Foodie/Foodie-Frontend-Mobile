package com.taller.tp.foodie.services

import android.app.IntentService
import android.content.Intent
import android.location.Location
import android.util.Log
import com.android.volley.VolleyError
import com.google.android.gms.location.LocationServices
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject
import java.lang.Thread.sleep


class TrackingService : IntentService("TrackingService") {

    var error = false
    var processing = false

    var cachedLocation: Location? = null

    private val userService = UserService(TrackingRequestHandler(this))

    override fun onHandleIntent(intent: Intent?) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        while (UserService.isUserLoggedIn()) {
            try {
                if (!processing){
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null && location != cachedLocation) {
                            cachedLocation = location
                            userService.updateLocation(location)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TrackingService::class.java.toString(), "Error updating location", e)
            }
            // 10 seconds
            sleep(10000)
        }
    }


    private class TrackingRequestHandler(val trackingService: TrackingService) : RequestHandler {
        override fun begin() {
            this.trackingService.processing = true
        }

        override fun onSuccess(response: JSONObject?) {
            this.trackingService.error = false
            this.trackingService.processing = false
        }

        override fun onError(error: VolleyError) {
            this.trackingService.error = true
            this.trackingService.processing = false
        }
    }
}