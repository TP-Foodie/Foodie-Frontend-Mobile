package com.taller.tp.foodie.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.requestHandlers.ClientOrderRequestHandler
import com.taller.tp.foodie.services.OrderService

const val PRODUCT_EMPTY_ERROR = "Por favor, ingrese el producto que desea ordenar"
const val PLACE_EMPTY_ERROR = "Por favor, elija un lugar donde debemos retirarlo"
//const val DELIVERY_EMPTY_ERROR = "Por favor, elija un delivery que retire su pedido"

class ClientOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_order)
        supportActionBar!!.hide()

        val makeOrderButton = findViewById<Button>(R.id.order_btn)

        makeOrderButton.setOnClickListener {
            val validProduct = validateProduct()
            val validPlace = validatePlace()
            val validDelivery = validateDelivery()

            if (validProduct && validPlace && validDelivery) {
                doOrder()
            }
        }

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

        if (placeField.text.isEmpty()) {
            placeField.error = PLACE_EMPTY_ERROR
            return false
        }

        return true
    }

    private fun validateDelivery(): Boolean {
        val deliveryField = findViewById<TextView>(R.id.delivery_by_who_input)

        deliveryField.error = null

//        if (deliveryField.text.isEmpty()) {
//            deliveryField.error = DELIVERY_EMPTY_ERROR
//            return false
//        }

        return true
    }


    private fun doOrder() {
        val product = findViewById<TextView>(R.id.delivery_what_input)
        val place = findViewById<TextView>(R.id.delivery_place_input)
//        val delivery = findViewById<TextView>(R.id.delivery_by_who_input)

        val requestHandler = ClientOrderRequestHandler(this)
//        val ownerId = Session.getCurrentUser ? TODO AFTER AUTHENTICATION
        val ownerId = "1" // TODO AFTER AUTHENTICATION

        OrderService(this, requestHandler).makeOrder(
            ownerId,
            product.text.toString(),
            place.text.toString()
        )
    }
}
