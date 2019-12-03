package com.taller.tp.foodie.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapquest.mapping.MapQuest
import com.mapquest.mapping.maps.MapView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.requestHandlers.GetDirectionsRequestHandler
import com.taller.tp.foodie.services.OrderService
import kotlinx.android.synthetic.main.activity_delivery_navigation.*
import org.json.JSONObject
import java.lang.ref.WeakReference

class DeliveryNavigationActivity : AppCompatActivity() {

    companion object {
        const val ORDER_ID = "order_id"
    }

    private lateinit var orderId: String

    private lateinit var mMapView: MapView
    private lateinit var mMapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapQuest.start(applicationContext)
        setContentView(R.layout.activity_delivery_navigation)

        mMapView = map_view
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapboxMap: MapboxMap) {
                mMapboxMap = mapboxMap
                mMapView.setStreetMode()
            }
        })

        orderId = intent.getStringExtra(ORDER_ID)

        getDirections()
    }

    private fun getDirections() {
        OrderService(GetDirectionsRequestHandler(WeakReference(this))).getDirections(orderId)
    }

    fun onUpdateDirections(response: JSONObject) {
        val route = response.getJSONObject("route")
        Log.e("RESPONSE", "response: " + route.toString())
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }
}
