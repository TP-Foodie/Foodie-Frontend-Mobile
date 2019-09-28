package com.taller.tp.foodie.ui

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.OrderProduct
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.requestHandlers.ClientOrderRequestHandler
import com.taller.tp.foodie.model.requestHandlers.CreatePlaceRequestHandler
import com.taller.tp.foodie.model.requestHandlers.ListPlacesRequestHandler
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.PlaceService
import org.json.JSONObject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


const val REQUEST_CODE_LOCATION = 123
const val INIT_ZOOM_LEVEL = 13f
const val PRODUCT_EMPTY_ERROR = "Por favor, ingrese el producto que desea ordenar"
const val PLACE_EMPTY_ERROR = "Por favor, elija un lugar donde debemos retirarlo"
const val CLIENT_NEW_ORDER_KEY = "CLIENT_NEW_ORDER"

class ClientMainActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener,
    OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastSelectedMarker: Marker? = null
    private var markerPlaceMap: HashMap<Marker, Place> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val profileButton = findViewById<Button>(R.id.profile_button)
        profileButton.setOnClickListener { profileButtonListener() }

        val makeOrderButton = findViewById<Button>(R.id.make_order_button)
        makeOrderButton.setOnClickListener { makeOrderButtonListener() }
    }

    private fun createMarker(place: Place): MarkerOptions? {
        val placeLocation = LatLng(place.coordinate.latitude, place.coordinate.longitude)
        return MarkerOptions().position(placeLocation)
                    .title(place.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .infoWindowAnchor(0.5F, 0F)
                    .zIndex(0F)
    }

    private fun profileButtonListener(){
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun makeOrderButtonListener(){
        val validProduct = validateProduct()
        val validPlace = validatePlace()

        if (validProduct && validPlace) {
            val marker = lastSelectedMarker!!
            val place = markerPlaceMap[marker]
            if (place == null){
                val createPlaceRequestHandler = CreatePlaceRequestHandler(this)
                val placePosition = Coordinate(marker.position.latitude, marker.position.longitude)
                val name = findViewById<EditText>(R.id.delivery_place_input).text.toString()
                PlaceService(this, createPlaceRequestHandler).create(placePosition, name)
            } else {
                doOrder(place)
            }
        }
    }

    fun doOrder(place: Place) {
        val isFavour= findViewById<CheckBox>(R.id.delivery_favour_check).isChecked
        val product = findViewById<TextView>(R.id.delivery_what_input)

        val requestHandler = ClientOrderRequestHandler(this)
//        val ownerId = Session.getCurrentUser ? TODO AFTER AUTHENTICATION
        val ownerId = "1" // TODO AFTER AUTHENTICATION

        val orderProduct = OrderProduct(product.text.toString(), place)
        val order = Order(isFavour, ownerId, orderProduct)
        OrderService(this, requestHandler).makeOrder(order)
    }

    private fun validateProduct(): Boolean {
        val productField = findViewById<TextView>(R.id.delivery_what_input)

        productField.error = null

        if (productField.text.isEmpty()) {
            productField.error = PRODUCT_EMPTY_ERROR
            return false
        }

        return true
    }

    private fun validatePlace(): Boolean {
        val placeField = findViewById<TextView>(R.id.delivery_place_input)
        placeField.error = null
        val marker = lastSelectedMarker

        if (placeField.text.isEmpty() || marker == null) {
            placeField.error = PLACE_EMPTY_ERROR
            return false
        }

        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val listPlacesRequestHandler = ListPlacesRequestHandler(this)
        PlaceService(this, listPlacesRequestHandler).list()
    }

    // Called when the places are ready from the ListPlacesRequestHandler
    @SuppressWarnings("MissingPermission")
    fun configureMapWithPlaces(places: ArrayList<Place>){
        enableMyLocation()
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val current = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(current).draggable(true))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, INIT_ZOOM_LEVEL))
            }
        }

        places.forEach { place ->
            val marker = mMap.addMarker(createMarker(place))
            markerPlaceMap[marker] = place
        }

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

            setOnMarkerClickListener(this@ClientMainActivity)
            setOnMarkerDragListener(this@ClientMainActivity)
        }
    }

    override fun onMarkerClick(marker : Marker): Boolean {
        lastSelectedMarker = marker
        val place = markerPlaceMap[marker]
        val deliveryPlaceInput = findViewById<EditText>(R.id.delivery_place_input)
        if (place != null){
            deliveryPlaceInput.setText(place.name)
        } else {
            deliveryPlaceInput.isEnabled = true
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


    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    @SuppressWarnings("MissingPermission")
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            mMap.isMyLocationEnabled = true
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onMarkerDragStart(marker : Marker) {
        lastSelectedMarker = null
    }

    override fun onMarkerDragEnd(marker : Marker) {
        lastSelectedMarker = marker
    }

    override fun onMarkerDrag(marker : Marker) {}

    fun saveOrder(response: JSONObject) {
        val intent = Intent(this, ClientMainActivity::class.java).apply {
            putExtra(CLIENT_NEW_ORDER_KEY, response.toString())
        }
        startActivity(intent)
    }
}
