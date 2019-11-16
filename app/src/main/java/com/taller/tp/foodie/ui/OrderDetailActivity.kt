package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
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
            OrderService(OrderDetailRequestHandler(this)).find(orderId)
        }

        with(findViewById<Button>(R.id.order_actions_button)){
            registerForContextMenu(this)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val deliverOption = menu!!.getItem(0).setVisible(false)
        val unassignOption = menu.getItem(1).setVisible(false)
        val assignOption = menu.getItem(2).setVisible(false)
        val cancelOption = menu.getItem(3).setVisible(false)
        val chatOption = menu.getItem(4).setVisible(false)
        when(order!!.getStatus()){
            Order.STATUS.WAITING_STATUS -> {
                if (userType == User.USER_TYPE.CUSTOMER) {
                    cancelOption.setVisible(true)
                } else {
                    assignOption.setVisible(true)
                }
            }
            Order.STATUS.TAKEN_STATUS -> {
                chatOption.setVisible(true)
                if (userType == User.USER_TYPE.CUSTOMER)
                    cancelOption.setVisible(true)
                else {
                    deliverOption.setVisible(true)
                    unassignOption.setVisible(true)
                }
            }
            Order.STATUS.DELIVERED_STATUS -> {}
        }
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.order_action_menu, menu)
        onPrepareOptionsMenu(menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.deliver_order_option -> {
                OrderService(OrderDetailRequestHandler(this).forUpdate())
                    .updateStatus(order!!, Order.STATUS.DELIVERED_STATUS)
                return true
            }
            R.id.unassign_order_option -> {
                return true
            }
            R.id.assign_order_option -> {
                return true
            }
            R.id.cancel_order_option -> {
                return true
            }
            R.id.chat_order_option -> {
                val intent = Intent(applicationContext, ChatActivity::class.java)
                intent.putExtra(ChatActivity.CHAT_ID, order?.getIdChat())
                startActivity(intent)
                return true
            }
            else -> return false
        }
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
    }
}
