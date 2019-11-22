package com.taller.tp.foodie.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.google.android.material.snackbar.Snackbar
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.CleanFcmTokenRequestHandler
import com.taller.tp.foodie.model.requestHandlers.ClientMainUserRequestHandler
import com.taller.tp.foodie.model.requestHandlers.ListPlacesRequestHandler
import com.taller.tp.foodie.services.PlaceService
import com.taller.tp.foodie.services.ProfileService
import com.taller.tp.foodie.services.UserService
import kotlinx.android.synthetic.main.activity_client_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference
import kotlin.collections.set


const val REQUEST_CODE_LOCATION = 123
const val INIT_ZOOM_LEVEL = 13f
const val CLIENT_TYPE_KEY = "CLIENT_TYPE_KEY"

class ClientMainActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener,
    OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastSelectedMarker: Marker? = null
    private var markerPlaceMap: HashMap<Marker, Place> = HashMap()
    private lateinit var userType: User.USER_TYPE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)

        ProfileService(ClientMainUserRequestHandler(WeakReference(this))).getUserProfile()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buildListeners()

        showSuccessfullOrderMessage()
    }

    private fun buildListeners() {
        val signOutButton = findViewById<Button>(R.id.btn_signout)
        signOutButton.setOnClickListener { signOut() }

        val profileButton = findViewById<Button>(R.id.profile_button)
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val orderListButton = findViewById<Button>(R.id.orders_button)
        orderListButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java).apply {
                putExtra(CLIENT_TYPE_KEY, userType.name)
            }
            startActivity(intent)
        }

        btn_products.setOnClickListener {
            val intent = Intent(applicationContext, ProductsActivity::class.java)
            intent.putExtra(ProductsActivity.PLACE, markerPlaceMap[lastSelectedMarker])
            startActivity(intent)
        }
    }

    private fun signOut() {
        // clean fcm token
        UserService(CleanFcmTokenRequestHandler(WeakReference(this)))
            .updateUserFcmToken("")
    }

    fun onCleanFcmTokenSuccess() {
        // clean user backend data
        UserBackendDataHandler.getInstance().deleteUserBackendData()

        // go to login and clear task
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showSuccessfullOrderMessage() {
        if (intent.getBooleanExtra(SUCCESSFUL_ORDER_KEY, false)){
            val context = findViewById<View>(R.id.map_choice_context)
            val snackbar = Snackbar.make(context, R.string.general_success, Snackbar.LENGTH_SHORT)
            snackbar.view.setBackgroundColor(Color.GREEN)
            snackbar.show()
        }
    }

    private fun createMarker(place: Place): MarkerOptions? {
        val placeLocation = LatLng(place.coordinate.latitude, place.coordinate.longitude)
        return MarkerOptions().position(placeLocation)
                    .title(place.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .infoWindowAnchor(0.5F, 0F)
                    .zIndex(0F)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val listPlacesRequestHandler = ListPlacesRequestHandler(this)
        PlaceService(listPlacesRequestHandler).list()
    }

    // Called when the places are ready from the ListPlacesRequestHandler
    @SuppressWarnings("MissingPermission")
    fun configureMapWithPlaces(places: ArrayList<Place>){
        enableMyLocation()
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val current = LatLng(location.latitude, location.longitude)
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
        choose_place_hint.visibility = View.GONE

        if (userType == User.USER_TYPE.CUSTOMER)
            btn_products.visibility = View.VISIBLE

        lastSelectedMarker = marker

        val place = markerPlaceMap[marker]
        if (place != null){
            place_name.visibility = View.VISIBLE
            place_name.text = place.name
            place_image.visibility = View.VISIBLE
            place_image.setImageURI(place.image)
            place_reputation.visibility = View.VISIBLE
            val current = LatLng(place.coordinate.latitude, place.coordinate.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current))
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

    fun loadUserTypeComponents(user: User) {
        userType = user.type

        if (userType == User.USER_TYPE.CUSTOMER) {
            place_layout.visibility = View.VISIBLE
        }
    }
}
