package com.taller.tp.foodie.ui

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_order_detail.*


class OrderDetailActivity : AppCompatActivity() {

    private var order: Order? = null
    private lateinit var userType: User.USER_TYPE

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
            OrderService(OrderDetailRequestHandler(this)).find(orderId)
        }

        val confirmDeliveryButton = findViewById<Button>(R.id.confirm_delivery_button)
        confirmDeliveryButton.setOnClickListener { confirmDeliveryButtonListener() }

        btn_chat.setOnClickListener {
            val intent = Intent(applicationContext, ChatActivity::class.java)
            intent.putExtra(ChatActivity.CHAT_ID, order?.getIdChat())
            intent.putExtra(ChatActivity.ORDER_STATUS, order?.getStatus()?.key)
            startActivity(intent)
        }
    }

    private fun confirmDeliveryButtonListener() {
        OrderService(OrderDetailRequestHandler(this).forUpdate())
            .updateStatus(order!!, Order.STATUS.DELIVERED_STATUS)
    }

    fun populateFields(order: Order) {
        this.order = order
        val orderNumber = findViewById<TextView>(R.id.order_number)
        orderNumber.text = String.format("Pedido Nro. %s", order.getNumber().toString())
        val orderOwner = findViewById<TextView>(R.id.order_owner)
        val owner = if (order.getOwner() == null) "" else
            String.format("%s %s", order.getOwner()!!.name, order.getOwner()!!.lastName)
        orderOwner.text = String.format("%s", owner)
        val orderType= findViewById<TextView>(R.id.order_type)
        orderType.text = String.format("%s", order.getType())
        val orderStatus = findViewById<TextView>(R.id.order_status)
        orderStatus.text = String.format("%s", order.getStatus())
        val orderProduct = findViewById<TextView>(R.id.order_product)
        orderProduct.text = String.format("%s", order.getProduct())
        val orderPlace = findViewById<TextView>(R.id.order_place)
        orderPlace.text = String.format("%s", order.getPlace().name)
        setupActions()
    }

    private fun setupActions() {
        if (userType == User.USER_TYPE.DELIVERY && order?.getStatus() == Order.STATUS.TAKEN_STATUS) {
            confirm_delivery_button.visibility = View.VISIBLE
        }

        if (order?.getStatus() != Order.STATUS.WAITING_STATUS) {
            btn_chat.visibility = View.VISIBLE
        }
    }

}
