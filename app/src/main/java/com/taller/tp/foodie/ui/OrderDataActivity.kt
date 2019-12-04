package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.OrderedProduct
import com.taller.tp.foodie.model.common.HeavyDataTransferingHandler
import com.taller.tp.foodie.model.requestHandlers.ClientOrderRequestHandler
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.ui.ui_adapters.OrderDetailProductsAdapter
import kotlinx.android.synthetic.main.activity_order_data.*
import org.json.JSONObject

class OrderDataActivity : AppCompatActivity() {

    private lateinit var orderedProducts: MutableList<OrderedProduct>
    private var paymentMethod: Order.PAYMENT_METHOD? = null
    private var isFavour: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_data)

        orderedProducts = HeavyDataTransferingHandler.getInstance().getOrderedProducts()

        setupListeners()

        setupProductsLayout()
        products_list.adapter = OrderDetailProductsAdapter(orderedProducts)
    }

    private fun setupListeners() {
        val paymentRadio = findViewById<RadioGroup>(R.id.payment_method_radio)
        paymentRadio.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                when (checkedId) {
                    R.id.cash_option -> {
                        paymentMethod = Order.PAYMENT_METHOD.CPM
                        isFavour = false
                    }
                    R.id.card_option -> {
                        paymentMethod = Order.PAYMENT_METHOD.CRPM
                        isFavour = false
                    }
                    R.id.favour_option -> {
                        paymentMethod = Order.PAYMENT_METHOD.GPPM
                        isFavour = true
                    }
                }
                updateFavourVisibility()
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

            if (order_name.text.isNullOrEmpty()) {
                ErrorHandler.handleError(
                    order_data_layout,
                    "Por favor indique un nombre para su pedido"
                )
                return@setOnClickListener
            }

            if (isFavour && order_gratitude_points.text.isNullOrEmpty()){
                ErrorHandler.handleError(
                    order_data_layout,
                    "Por favor indique los puntos de gratitud"
                )
                return@setOnClickListener
            }

            val requestHandler = ClientOrderRequestHandler(this)

            val orderProduct = OrderService.OrderProductRequest(orderedProducts)
            val orderType = if (isFavour) Order.TYPE.FAVOR_TYPE else Order.TYPE.NORMAL_TYPE
            val orderGratitudePoints = if (isFavour) Integer.parseInt(order_gratitude_points.text.toString()) else 0
            val orderRequest = OrderService.OrderRequest(
                order_name.text.trim().toString(),
                orderType.key, orderProduct, paymentMethod,
                orderGratitudePoints
            )

            OrderService(requestHandler).makeOrder(orderRequest)
        }
    }

    private fun updateFavourVisibility() {
        val gratitudePointsRL = findViewById<RelativeLayout>(R.id.rl_gratitude_points)
        gratitudePointsRL.visibility = if (isFavour) View.VISIBLE else View.INVISIBLE
    }

    fun saveAndChooseDelivery(response: JSONObject) {
        val intent = Intent(this, ConfirmOrderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        HeavyDataTransferingHandler.getInstance().saveOrderJson(response.toString())
        startActivity(intent)
    }

    private fun setupProductsLayout() {
        val manager = LinearLayoutManager(applicationContext)
        manager.reverseLayout = true
        products_list.layoutManager = manager
    }

    fun saveAndReturnMain() {
        val intent = Intent(this, ClientMainActivity::class.java).apply {
            putExtra(SUCCESSFUL_ORDER_KEY, true)
        }
        startActivity(intent)
    }
}
