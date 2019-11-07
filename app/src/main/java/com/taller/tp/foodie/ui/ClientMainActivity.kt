package com.taller.tp.foodie.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.*
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
import com.taller.tp.foodie.model.Coordinate
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.*
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.PlaceService
import com.taller.tp.foodie.services.ProfileService
import com.taller.tp.foodie.services.UserService
import org.json.JSONObject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference
import kotlin.collections.set


const val REQUEST_CODE_LOCATION = 123
const val INIT_ZOOM_LEVEL = 13f
const val PRODUCT_EMPTY_ERROR = "Por favor, ingrese el producto que desea ordenar"
const val PLACE_EMPTY_ERROR = "Por favor, elija un lugar donde debemos retirarlo"
const val CLIENT_NEW_ORDER_KEY = "CLIENT_NEW_ORDER"
const val CLIENT_TYPE_KEY = "CLIENT_TYPE_KEY"

class ClientMainActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener,
    OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastSelectedMarker: Marker? = null
    private var markerPlaceMap: HashMap<Marker, Place> = HashMap()
    private var paymentMethod: Order.PAYMENT_METHOD? = null
    lateinit var userType: User.USER_TYPE

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
        val paymentRadio = findViewById<RadioGroup>(R.id.payment_method_radio)
        paymentRadio.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1){
                when (checkedId){
                    R.id.cash_option -> paymentMethod = Order.PAYMENT_METHOD.CPM
                    R.id.card_option -> paymentMethod = Order.PAYMENT_METHOD.CRPM
                }
            }
        }
        val favourCheck = findViewById<CheckBox>(R.id.delivery_favour_check)
        favourCheck.setOnCheckedChangeListener{ _, checked ->
            val paymentLayout = findViewById<LinearLayout>(R.id.payment_layout)
            if (checked){
                paymentLayout.visibility = View.INVISIBLE
                paymentRadio.clearCheck()
            } else {
                paymentLayout.visibility = View.VISIBLE
            }
        }

        val signOutButton = findViewById<Button>(R.id.btn_signout)
        signOutButton.setOnClickListener { signOut() }

        val profileButton = findViewById<Button>(R.id.profile_button)
        profileButton.setOnClickListener { profileButtonListener() }

        val makeOrderButton = findViewById<Button>(R.id.make_order_button)
        makeOrderButton.setOnClickListener { makeOrderButtonListener() }

        val orderListButton = findViewById<Button>(R.id.orders_button)
        orderListButton.setOnClickListener { orderListButtonListener() }
    }

    fun loadUserTypeComponents(user: User) {
        userType = user.type
        val makeOrderLayout = findViewById<LinearLayout>(R.id.make_order_layout)
        when(userType){
            User.USER_TYPE.DELIVERY -> makeOrderLayout.visibility = View.INVISIBLE
            User.USER_TYPE.CUSTOMER -> makeOrderLayout.visibility = View.VISIBLE
        }
    }

    private fun orderListButtonListener() {
        val intent = Intent(this, OrdersActivity::class.java).apply {
            putExtra(CLIENT_TYPE_KEY, userType.name)
        }
        startActivity(intent)
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

    private fun profileButtonListener(){
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun makeOrderButtonListener(){
        val validProduct = validateProduct()
        val validPlace = validatePlace()

        if (validProduct && validPlace) {
            val marker = lastSelectedMarker!!
            val place = markerPlaceMap[marker]
            if (place == null){ // Crear el place si no existe en el server?
                val createPlaceRequestHandler = CreatePlaceRequestHandler(this)
                val placePosition = Coordinate(marker.position.latitude, marker.position.longitude)
                val name = findViewById<EditText>(R.id.delivery_place_input).text.toString()
                PlaceService(createPlaceRequestHandler).create(
                    placePosition,
                    name
                )
            } else {
                doOrder(place)
            }
        }
    }

    fun doOrder(place: Place) {
        val isFavour= findViewById<CheckBox>(R.id.delivery_favour_check).isChecked
        if (isFavour)
            paymentMethod = null

        val product = findViewById<TextView>(R.id.delivery_what_input)

        val requestHandler = ClientOrderRequestHandler(this)

        val orderProduct = OrderService.OrderProductRequest(product.text.toString(), place.getId())
        val orderType: Order.TYPE
        orderType = if (isFavour) Order.TYPE.FAVOR_TYPE else Order.TYPE.NORMAL_TYPE
        val orderRequest = OrderService.OrderRequest(orderType.key, orderProduct, paymentMethod)
        OrderService(requestHandler).makeOrder(orderRequest)
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
        lastSelectedMarker = marker
        val place = markerPlaceMap[marker]
        val deliveryPlaceInput = findViewById<EditText>(R.id.delivery_place_input)
        if (place != null){
            deliveryPlaceInput.setText(place.name)
            val current = LatLng(place.coordinate.latitude, place.coordinate.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current))
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

    fun saveAndChooseDelivery(response: JSONObject) {
        val intent = Intent(this, ChooseDeliveryActivity::class.java).apply {
            putExtra(CLIENT_NEW_ORDER_KEY, response.toString())
        }
        startActivity(intent)
    }
}
