package com.taller.tp.foodie.ui

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.android.volley.VolleyError
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject
import android.content.Context
import com.taller.tp.foodie.services.ProfileService
import android.content.IntentFilter
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory


class FollowDeliveryActivity: FragmentActivity() {

    private lateinit var trackingDeliveryService: Intent
    private var map: GoogleMap? = null

    private lateinit var deliveryId: String

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            Log.i("latitude", latitude.toString())
            Log.i("longitude", longitude.toString())

            map!!.clear()
            val current = LatLng(latitude, longitude)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(current, INIT_ZOOM_LEVEL))
            map!!.addMarker(createMarker(latitude = latitude, longitude = longitude))
            startTrackingService(10000)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_delivery)

        deliveryId = intent.getStringExtra("delivery_id")

        if (map == null) {
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync { m: GoogleMap -> map = m }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("com.taller.tp.foodie.ui.FollowDeliveryActivity")
        registerReceiver(broadcastReceiver, intentFilter)

        startTrackingService()
    }

    private fun startTrackingService(delay: Long = 0) {
        trackingDeliveryService = Intent(this, TrackingDeliveryService::class.java)

        trackingDeliveryService.putExtra(
            "delivery_id",
            intent.getStringExtra("delivery_id")
        )

        trackingDeliveryService.putExtra("delay", delay)

        applicationContext.startService(trackingDeliveryService)
    }


    private fun createMarker(latitude: Double, longitude: Double): MarkerOptions? {
        val deliveryLocation = LatLng(latitude, longitude)
        return MarkerOptions().position(deliveryLocation)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .infoWindowAnchor(0.5F, 0F)
            .zIndex(0F)
    }
}


class TrackingDeliveryService : IntentService("TrackingDeliveryService") {

    private val profileService = ProfileService(DeliveryRequestHandler(this))

    override fun onHandleIntent(intent: Intent?) {
        val deliveryID = intent!!.getStringExtra("delivery_id")
        // 10 seconds
        Thread.sleep(intent.getLongExtra("delay", 0))
        profileService.getOtherUser(deliveryID)
    }

    fun sendLocation(latitude: Double, longitude: Double) {
        val intent1 = Intent()
        intent1.action = "com.taller.tp.foodie.ui.FollowDeliveryActivity"
        intent1.putExtra("latitude", latitude)
        intent1.putExtra("longitude", longitude)
        sendBroadcast(intent1)
    }

}

class DeliveryRequestHandler(
    private val trackingDeliveryService: TrackingDeliveryService) : RequestHandler {

    override fun begin() {

    }

    override fun onSuccess(response: JSONObject?) {
        val location = response!!.getJSONObject("location")
        this.trackingDeliveryService.sendLocation(location.getDouble("latitude"), location.getDouble("longitude"))
    }

    override fun onError(error: VolleyError) {
        Log.i("Delivery request", error.toString())
    }
}