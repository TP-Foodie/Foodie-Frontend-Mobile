package com.taller.tp.foodie.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Coordinate
import com.taller.tp.foodie.model.DeliveryUser
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.requestHandlers.AvailableDeliveryRequestHandler
import com.taller.tp.foodie.services.CoordinateService
import com.taller.tp.foodie.services.DeliveryUserService
import com.taller.tp.foodie.services.OrderService
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions

class ChooseDeliveryActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {

    private var pendingOrder: Order? = null
    private var placeCoordinate: Coordinate? = null
    private lateinit var mMap: GoogleMap
    private var lastSelectedMarker: Marker? = null
    private var markerPlaceMap: HashMap<Marker, DeliveryUser> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val confirmDeliveryButton = findViewById<Button>(R.id.confirm_delivery_button)
        confirmDeliveryButton.setOnClickListener { confirmDeliveryButtonListener() }

        val pendingOrderJson = intent.getStringExtra(CLIENT_NEW_ORDER_KEY)
        if (pendingOrderJson != null) {
            pendingOrder = OrderService.fromOrderJson(JSONObject(pendingOrderJson))
        }
        val placeCoordinateJson = intent.getStringExtra(COORDINATES_ORDER_KEY)
        if (placeCoordinateJson != null) {
            placeCoordinate = CoordinateService.fromCoordinateJson(JSONObject(placeCoordinateJson))
        }

        findViewById<TextView>(R.id.delivery_name).visibility = View.INVISIBLE
    }

    private fun createMarker(delivery: DeliveryUser): MarkerOptions? {
        val deliveryLocation = LatLng(delivery.coordinates!!.latitude, delivery.coordinates!!.longitude)
        return MarkerOptions().position(deliveryLocation)
                    .title(delivery.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .infoWindowAnchor(0.5F, 0F)
                    .zIndex(0F)
    }

    private fun confirmDeliveryButtonListener(){
//        val validProduct = validateProduct()
//        val validPlace = validatePlace()
//
//        if (validProduct && validPlace) {
//            val marker = lastSelectedMarker!!
//            val place = markerPlaceMap[marker]
//            if (place == null){
//                val createPlaceRequestHandler = CreatePlaceRequestHandler(this)
//                val placePosition = Coordinate(marker.position.latitude, marker.position.longitude)
//                val name = findViewById<EditText>(R.id.delivery_place_input).text.toString()
//                PlaceService(this, createPlaceRequestHandler).create(placePosition, name)
//            } else {
//                doOrder(place)
//            }
//        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val listPlacesRequestHandler = AvailableDeliveryRequestHandler(this)
        DeliveryUserService(this, listPlacesRequestHandler).availableDeliveries(placeCoordinate!!)
    }

    // Called when the deliveries are ready from the AvailableDeliveryRequestHandler
    fun configureMapWithDeliveries(deliveries: ArrayList<DeliveryUser>){
        deliveries.forEach { delivery ->
            val marker = mMap.addMarker(createMarker(delivery))
            markerPlaceMap[marker] = delivery
        }
        val current = LatLng(placeCoordinate!!.latitude, placeCoordinate!!.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, INIT_ZOOM_LEVEL))

        with(mMap) {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = false
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isScrollGesturesEnabled = true
            uiSettings.isTiltGesturesEnabled = true
            uiSettings.isRotateGesturesEnabled = true

            setOnMarkerClickListener(this@ChooseDeliveryActivity)
        }
    }

    override fun onMarkerClick(marker : Marker): Boolean {
        lastSelectedMarker = marker
        val delivery = markerPlaceMap[marker]
        val deliveryName = findViewById<TextView>(R.id.delivery_name)
        val chooseDeliveryLbl = findViewById<TextView>(R.id.choose_delivery_label)
        if (delivery != null){
            deliveryName.text = delivery.name
            deliveryName.visibility = View.VISIBLE
            chooseDeliveryLbl.visibility = View.INVISIBLE
        } else {
            deliveryName.isEnabled = true
        }

        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
