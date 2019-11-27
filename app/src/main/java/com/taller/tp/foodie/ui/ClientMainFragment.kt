package com.taller.tp.foodie.ui

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.ListPlacesRequestHandler
import com.taller.tp.foodie.services.PlaceService
import kotlinx.android.synthetic.main.fragment_client_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

const val REQUEST_CODE_LOCATION = 123
const val INIT_ZOOM_LEVEL = 13f
const val CLIENT_TYPE_KEY = "CLIENT_TYPE_KEY"

class ClientMainFragment : Fragment(),
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener,
    OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastSelectedMarker: Marker? = null
    private var markerPlaceMap: HashMap<Marker, Place> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_client_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity?.applicationContext!!)

        buildListeners()

        if (UserBackendDataHandler.getInstance().getUserType() == User.USER_TYPE.CUSTOMER.name) {
            place_layout.visibility = View.VISIBLE
        }
    }

    private fun buildListeners() {
        btn_products.setOnClickListener {
            val intent = Intent(activity?.applicationContext, ProductsActivity::class.java)
            intent.putExtra(ProductsActivity.PLACE, markerPlaceMap[lastSelectedMarker])
            startActivity(intent)
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
    fun configureMapWithPlaces(places: ArrayList<Place>) {
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

            setOnMarkerClickListener(this@ClientMainFragment)
            setOnMarkerDragListener(this@ClientMainFragment)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        choose_place_hint.visibility = View.GONE

        val userType = UserBackendDataHandler.getInstance().getUserType()
        if (userType == User.USER_TYPE.CUSTOMER.name)
            btn_products.visibility = View.VISIBLE

        lastSelectedMarker = marker

        val place = markerPlaceMap[marker]
        if (place != null) {
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
            EasyPermissions.requestPermissions(
                this, getString(R.string.location),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            activity?.applicationContext!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onMarkerDragStart(marker: Marker) {
        lastSelectedMarker = null
    }

    override fun onMarkerDragEnd(marker: Marker) {
        lastSelectedMarker = marker
    }

    override fun onMarkerDrag(marker: Marker) {}

    companion object {
        fun newInstance(): ClientMainFragment = ClientMainFragment()
    }
}
