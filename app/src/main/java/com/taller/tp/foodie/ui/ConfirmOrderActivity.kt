package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.taller.tp.foodie.model.common.HeavyDataTransferingHandler
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

        btn_confirm_delivery.setOnClickListener { confirmDeliveryButtonListener() }

        val pendingOrderJson = HeavyDataTransferingHandler.getInstance().getOrderJson()
        pendingOrder = OrderService.fromOrderJson(JSONObject(pendingOrderJson))
        placeCoordinate = pendingOrder!!.getPlace().coordinate
        val confirmOrderRequestHandler = ConfirmOrderRequestHandler(this)
        confirmOrderRequestHandler.forGetPrice()
        OrderService(confirmOrderRequestHandler).getPrice(pendingOrder!!.id)

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
        val marker = lastSelectedMarker!!
        val deliveryUser = markerPlaceMap[marker]
        val assignDelivery = AssignOrderDeliveryRequestHandler(this)
        OrderService(assignDelivery).confirmOrder(pendingOrder!!, deliveryUser!!)
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
        ProfileService(getDeliveryDetail).getOtherUser(deliveryId)
        return false
    }

    fun onMarkerSetDetail(delivery: User) {
        choose_delivery_hint.visibility = View.GONE
        delivery_name.visibility = View.VISIBLE
        delivery_name.text = String.format("%s %s", delivery.name, delivery.lastName)
        delivery_reputation.visibility = View.VISIBLE
        delivery_reputation.rating = delivery.reputation?.toFloat()!!
        delivery_image.visibility = View.VISIBLE
        delivery_image.setImageURI(delivery.image)
        btn_confirm_delivery.visibility = View.VISIBLE
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
        order_price.visibility = View.VISIBLE
        val priceLabel = getString(R.string.order_price_label)
        order_price.text = String.format(priceLabel, price)
        pendingOrder!!.setQuotation(price)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, ClientMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
