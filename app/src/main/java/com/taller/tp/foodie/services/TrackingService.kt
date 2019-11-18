package com.taller.tp.foodie.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject
import java.lang.Thread.sleep


class TrackingService : IntentService("TrackingService") {

    var error = false
    var processing = false

    private val userService = UserService(TrackingRequestHandler(this))

    override fun onHandleIntent(intent: Intent?) {
        while (UserService.isUserLoggedIn()) {
            try {
                Log.e("Info", "Processing: $processing")
                if (!processing){
                    userService.updateLocation()
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
            Log.e("Info", "Setting processing to true")
            this.trackingService.processing = true
        }

        override fun onSuccess(response: JSONObject?) {
            Log.e("Info", "Setting processing to false")
            this.trackingService.error = false
            this.trackingService.processing = false
        }

        override fun onError(error: VolleyError) {
            Log.e("Info", "Setting processing to false")
            this.trackingService.error = true
            this.trackingService.processing = false
        }
    }
}