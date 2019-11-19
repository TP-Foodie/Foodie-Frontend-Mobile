package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.ListOrderderProduct
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.OrderedProduct
import com.taller.tp.foodie.model.requestHandlers.ClientOrderRequestHandler
import com.taller.tp.foodie.services.OrderService
import kotlinx.android.synthetic.main.activity_order_data.*
import org.json.JSONObject

class OrderDataActivity : AppCompatActivity() {

    companion object {
        const val PRODUCTS = "products"
    }

    private lateinit var orderedProducts: MutableList<OrderedProduct>
    private var paymentMethod: Order.PAYMENT_METHOD? = null
    private var isFavour: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_data)

        orderedProducts =
            intent.getParcelableExtra<ListOrderderProduct>(PRODUCTS).orderedProductsList

        setupListeners()
    }

    private fun setupListeners() {
        val paymentRadio = findViewById<RadioGroup>(R.id.payment_method_radio)
        paymentRadio.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                when (checkedId) {
                    R.id.cash_option -> paymentMethod = Order.PAYMENT_METHOD.CPM
                    R.id.card_option -> paymentMethod = Order.PAYMENT_METHOD.CRPM
                    R.id.favour_option -> isFavour = true
                }
            }
        }

        make_order_button.setOnClickListener {
            if (paymentMethod == null && !isFavour) {
                ErrorHandler.handleError(
                    order_data_layout,
                    "Por favor seleccione un metodo de pago"
                )
                return@setOnClickListener
            }

            val requestHandler = ClientOrderRequestHandler(this)

            val orderProduct = OrderService.OrderProductRequest(orderedProducts)
            val orderType = if (isFavour) Order.TYPE.FAVOR_TYPE else Order.TYPE.NORMAL_TYPE
            val orderRequest = OrderService.OrderRequest(orderType.key, orderProduct, paymentMethod)
            OrderService(requestHandler).makeOrder(orderRequest)
        }
    }

    fun saveAndChooseDelivery(response: JSONObject) {
        val intent = Intent(this, ConfirmOrderActivity::class.java)
        intent.putExtra(CLIENT_NEW_ORDER_KEY, response.toString())
        startActivity(intent)
    }
}
