package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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
import com.taller.tp.foodie.model.*
import com.taller.tp.foodie.model.requestHandlers.AssignOrderChatRequestHandler
import com.taller.tp.foodie.model.requestHandlers.AssignOrderDeliveryRequestHandler
import com.taller.tp.foodie.model.requestHandlers.ConfirmOrderRequestHandler
import com.taller.tp.foodie.model.requestHandlers.CreateChatRequestHandler
import com.taller.tp.foodie.services.ChatService
import com.taller.tp.foodie.services.DeliveryUserService
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.ProfileService
import kotlinx.android.synthetic.main.activity_confirm_order.*
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions

const val SUCCESSFUL_ORDER_KEY = "SUCCESSFUL_ORDER_KEY"

const val DELIVERY_EMPTY_ERROR = "Por favor, elija un delivery para que le lleve el pedido"
class ConfirmOrderActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {

    private var pendingOrder: Order? = null
    private var placeCoordinate: Coordinate? = null
    private lateinit var mMap: GoogleMap
    private var lastSelectedMarker: Marker? = null
    private var markerPlaceMap: HashMap<Marker, DeliveryUser> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val deliveryLayout = findViewById<LinearLayout>(R.id.delivery_layout)
        deliveryLayout.visibility = View.INVISIBLE

        val confirmDeliveryButton = findViewById<Button>(R.id.confirm_delivery_button)
        confirmDeliveryButton.setOnClickListener { confirmDeliveryButtonListener() }

        val pendingOrderJson = intent.getStringExtra(CLIENT_NEW_ORDER_KEY)
        if (pendingOrderJson != null) {
            Log.e("OrderJson", "order json: " + pendingOrderJson)
            pendingOrder = OrderService.fromOrderJson(JSONObject(pendingOrderJson))
            placeCoordinate = pendingOrder!!.getPlace().coordinate
            val confirmOrderRequestHandler = ConfirmOrderRequestHandler(this)
            confirmOrderRequestHandler.forGetPrice()
            OrderService(confirmOrderRequestHandler).getPrice(pendingOrder!!.id)
        }

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
        val validDeliveryUser = validateDeliveryUser()

        if (validDeliveryUser) {
            val marker = lastSelectedMarker!!
            val deliveryUser = markerPlaceMap[marker]
            val assignDelivery = AssignOrderDeliveryRequestHandler(this)
            OrderService(assignDelivery)
                .confirmOrder(pendingOrder!!, deliveryUser!!)
        }
    }

    private fun validateDeliveryUser(): Boolean {
        val deliveryField = findViewById<TextView>(R.id.delivery_name)
        deliveryField.error = null
        val marker = lastSelectedMarker

        if (deliveryField.text.isEmpty() || marker == null) {
            deliveryField.error = DELIVERY_EMPTY_ERROR
            return false
        }

        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val listPlacesRequestHandler = ConfirmOrderRequestHandler(this)
        DeliveryUserService(listPlacesRequestHandler).availableDeliveries(placeCoordinate!!)
    }

    // Called when the deliveries are ready from the ConfirmOrderRequestHandler
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

            setOnMarkerClickListener(this@ConfirmOrderActivity)
        }
    }

    override fun onMarkerClick(marker : Marker): Boolean {
        lastSelectedMarker = marker
        val delivery = markerPlaceMap[marker]
        val deliveryId = delivery!!.id!!
        val getDeliveryDetail = ConfirmOrderRequestHandler(this).forDetail(deliveryId)
        ProfileService(getDeliveryDetail).getOtherUserForChat(deliveryId)
        return false
    }

    fun onMarkerSetDetail(delivery: User){
        val deliveryLayout = findViewById<LinearLayout>(R.id.delivery_layout)
        val deliveryName = findViewById<TextView>(R.id.delivery_name)
        val deliveryRep = findViewById<TextView>(R.id.delivery_rep)
        val layout = findViewById<LinearLayout>(R.id.order_layout)
        deliveryLayout.visibility = View.VISIBLE
        deliveryName.text = String.format("Nombre: %s", delivery.name)
        deliveryName.visibility = View.VISIBLE
        deliveryRep.text = String.format("Reputación: %d", delivery.reputation)
        deliveryRep.visibility = View.VISIBLE
        layout.visibility = View.VISIBLE
        if (delivery.image.isNullOrEmpty()) return
        runOnUiThread {
            delivery_photo.setImageURI(delivery.image)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun saveAndReturnMain() {
        val intent = Intent(this, ClientMainActivity::class.java).apply {
            putExtra(SUCCESSFUL_ORDER_KEY, true)
        }
        startActivity(intent)
    }

    fun createChat(order: Order?) {
        val chat = Chat(order?.getOwner()?.id!!, order.getDelivery()?.id!!, order.id)
        ChatService(CreateChatRequestHandler(this)).createChat(chat)
    }

    fun assignChat(chat: ChatFetched) {
        OrderService(AssignOrderChatRequestHandler(this)).assignChat(chat)
    }

    fun updatePrice(price: Double) {
        val orderPriceText = findViewById<TextView>(R.id.order_price)
        val priceLabel = getString(R.string.order_price_label)
        orderPriceText.text = String.format(priceLabel, price)
        pendingOrder!!.setQuotation(price)
    }
}
