package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.OrderedProduct
import com.taller.tp.foodie.model.Place
import com.taller.tp.foodie.model.ProductFetched
import com.taller.tp.foodie.model.common.HeavyDataTransferingHandler
import com.taller.tp.foodie.model.requestHandlers.ListProductsRequestHandler
import com.taller.tp.foodie.services.ProductsService
import com.taller.tp.foodie.ui.ui_adapters.ProductsAdapter
import kotlinx.android.synthetic.main.activity_products.*
import java.lang.ref.WeakReference

interface ClickListener {
    fun onIncrementQuantity(productPosition: Int)
    fun onDecrementQuantity(productPosition: Int)
}

class ProductsActivity : AppCompatActivity(), ClickListener {

    companion object {
        const val PLACE = "place"
    }

    private lateinit var products: MutableList<ProductFetched>
    private var orderedProducts = hashMapOf<String, OrderedProduct>()
    private lateinit var place: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        place = intent.getParcelableExtra(PLACE)

        place_name.text = place.name

        getProductsFromPlace(place.id)

        setupUI()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        btn_buy_product.setOnClickListener {
            val intent = Intent(applicationContext, OrderDataActivity::class.java)

            val listProducts = mutableListOf<OrderedProduct>()
            for ((_, prod) in orderedProducts) {
                if (prod.quantity != 0)
                    listProducts.add(prod)
            }

            HeavyDataTransferingHandler.getInstance().saveOrderedProducts(listProducts)

            startActivity(intent)
        }
    }

    private fun setupUI() {
        val manager = LinearLayoutManager(applicationContext)
        manager.reverseLayout = true
        products_list.layoutManager = manager
    }

    private fun getProductsFromPlace(placeId: String) {
        ProductsService(ListProductsRequestHandler(this)).list(placeId)
    }

    fun onSuccessListProducts(products: MutableList<ProductFetched>) {
        this.products = products
        for (product in products) {
            orderedProducts[product.id] = OrderedProduct(0, product)
        }

        products_list.adapter = ProductsAdapter(products, orderedProducts, WeakReference(this))
    }


    override fun onIncrementQuantity(productPosition: Int) {
        val product = products[productPosition]

        val orderedProduct = orderedProducts[product.id]
        if (orderedProduct != null) {
            orderedProduct.quantity += 1
        }

        updateBuyButton()

        updateProductList()
    }

    @SuppressLint("SetTextI18n")
    private fun updateBuyButton() {
        var totalQuantity = 0
        var totalPrice = 0
        for ((_, prod) in orderedProducts) {
            totalQuantity += prod.quantity
            totalPrice += (prod.quantity * prod.productFetched.price)
        }

        if (totalQuantity != 0) {
            btn_buy_product.visibility = View.VISIBLE
            btn_buy_product.text = "Pedir $totalQuantity por $$totalPrice"
        } else {
            btn_buy_product.visibility = View.INVISIBLE
        }
    }

    override fun onDecrementQuantity(productPosition: Int) {
        val product = products[productPosition]

        val orderedProduct = orderedProducts[product.id]
        if (orderedProduct != null) {
            orderedProduct.quantity -= 1
        }

        updateBuyButton()

        updateProductList()
    }

    private fun updateProductList() {
        products_list.adapter = ProductsAdapter(products, orderedProducts, WeakReference(this))
    }
}
