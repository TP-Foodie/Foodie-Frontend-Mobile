package com.taller.tp.foodie.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.requestHandlers.OrderDetailRequestHandler
import com.taller.tp.foodie.services.OrderService


class OrderDetailActivity : AppCompatActivity() {

    private var order: Order? = null
    lateinit var userType: User.USER_TYPE

    private fun loadUserType() {
        val intentUserType = intent.getStringExtra(CLIENT_TYPE_KEY)
        this.userType = User.USER_TYPE.valueOf(intentUserType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        loadUserType()

        val orderId = intent.getStringExtra(DETAIL_ORDER_KEY)
        if (orderId != null) {
            OrderService(this, OrderDetailRequestHandler(this)).find(orderId)
        }

        val confirmDeliveryButton = findViewById<Button>(R.id.confirm_delivery_button)
        confirmDeliveryButton.setOnClickListener { confirmDeliveryButtonListener() }
    }

    private fun confirmDeliveryButtonListener() {
        OrderService(this, OrderDetailRequestHandler(this).forUpdate())
            .updateStatus(order!!, Order.STATUS.DELIVERED_STATUS)
    }

    fun populateFields(order: Order) {
        this.order = order
        val orderNumber = findViewById<TextView>(R.id.order_number)
        orderNumber.text = String.format("Perdido Nro. %s", order.getNumber().toString())
        val orderOwner = findViewById<TextView>(R.id.order_owner)
        val owner = if (order.getOwner() == null) "" else
            String.format("%s %s", order.getOwner()!!.name, order.getOwner()!!.lastName)
        orderOwner.text = String.format("Propietario: %s", owner)
        val orderType= findViewById<TextView>(R.id.order_type)
        orderType.text = String.format("Tipo de Pedido: %s", order.getType())
        val orderStatus = findViewById<TextView>(R.id.order_status)
        orderStatus.text = String.format("Estado: %s", order.getStatus())
        val orderProduct = findViewById<TextView>(R.id.order_product)
        orderProduct.text = String.format("Producto: %s", order.getProduct())
        val orderPlace = findViewById<TextView>(R.id.order_place)
        orderPlace.text = String.format("Lugar: %s", order.getPlace().name)
        setupActions()
    }

    private fun setupActions() {
        if (userType != User.USER_TYPE.DELIVERY || order!!.getStatus() != Order.STATUS.TAKEN_STATUS) {
            val confirmDeliveryButton = findViewById<Button>(R.id.confirm_delivery_button)
            confirmDeliveryButton.visibility = View.INVISIBLE
        }
    }

}
