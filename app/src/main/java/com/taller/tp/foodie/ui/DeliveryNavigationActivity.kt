package com.taller.tp.foodie.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
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

    private lateinit var map: GoogleMap

    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private var isRouteDraw = false

    private var shouldCameraBeCentered = true

    private lateinit var lines: MutableList<Polyline>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_navigation)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync { m: GoogleMap ->
                map = m
                setMapUIDetails()
            }

        orderId = intent.getStringExtra(ORDER_ID)

        getDirections()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        locationRequest = LocationRequest.create()
            .setInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        lines = mutableListOf()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                val current = LatLng(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude
                )
                if (shouldCameraBeCentered) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 19f))
                }

                map.addMarker(
                    MarkerOptions().position(current)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .infoWindowAnchor(0.5F, 0F)
                        .zIndex(0F)
                )

                // check if is in route
                if (!isRouteDraw) {
                    return
                }

                var isInPolyline = false
                for (polyline in lines) {
                    if (PolyUtil.isLocationOnPath(
                            current,
                            polyline.points,
                            false,
                            100.toDouble()
                        )
                    ) {
                        isInPolyline = true
                        break
                    }
                }

                if (!isInPolyline) {
                    getDirections()
                }
            }
        }
    }

    private fun setMapUIDetails() {
        with(map) {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = false
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isScrollGesturesEnabled = true
            uiSettings.isTiltGesturesEnabled = true
            uiSettings.isRotateGesturesEnabled = true
        }

        map.setOnCameraMoveStartedListener {
            shouldCameraBeCentered = false
        }

        btn_center_camera.setOnClickListener {
            shouldCameraBeCentered = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val current = LatLng(location.latitude, location.longitude)
                if (shouldCameraBeCentered) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 19f))
                }
            }
        }
    }

    private fun getDirections() {
        OrderService(GetDirectionsRequestHandler(WeakReference(this))).getDirections(orderId)
    }

    fun onUpdateDirections(response: JSONObject) {
        val route = response.getJSONObject("route")
        val legs = route.getJSONArray("legs")
        val listRouteCoordinates = mutableListOf<JSONObject>()
        for (i in 0 until legs.length()) {
            val leg = legs.getJSONObject(i)
            val maneuvers = leg.getJSONArray("maneuvers")
            for (j in 0 until maneuvers.length()) {
                listRouteCoordinates.add(maneuvers.getJSONObject(j).getJSONObject("startPoint"))
            }
        }

        updateRouteCoordinates(listRouteCoordinates)
    }

    private fun updateRouteCoordinates(coordinates: List<JSONObject>) {
        map.clear()
        lines.clear()
        isRouteDraw = false

        coordinates.forEachIndexed { index, coordinate ->
            map.addMarker(createMarker(coordinate.getDouble("lat"), coordinate.getDouble("lng")))
            if (index != 0) {
                val polyline = map.addPolyline(
                    PolylineOptions().addAll(
                        mutableListOf(
                            LatLng(coordinate.getDouble("lat"), coordinate.getDouble("lng")),
                            LatLng(
                                coordinates[index - 1].getDouble("lat"),
                                coordinates[index - 1].getDouble("lng")
                            )
                        )
                    )
                )
                lines.add(polyline)
            }
        }

        isRouteDraw = true
    }

    private fun createMarker(latitude: Double, longitude: Double): MarkerOptions? {
        val deliveryLocation = LatLng(latitude, longitude)
        return MarkerOptions().position(deliveryLocation)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .infoWindowAnchor(0.5F, 0F)
            .zIndex(0F)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
