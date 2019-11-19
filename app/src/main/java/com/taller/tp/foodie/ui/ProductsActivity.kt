package com.taller.tp.foodie.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ProductFetched
import com.taller.tp.foodie.model.requestHandlers.ListProductsRequestHandler
import com.taller.tp.foodie.services.ProductsService
import com.taller.tp.foodie.ui.ui_adapters.ProductsAdapter
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : AppCompatActivity() {

    companion object {
        const val PLACE_ID = "placeId"
        const val PLACE_NAME = "placeName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        place_name.text = intent.getStringExtra(PLACE_NAME)

        getProductFromPlace(intent.getStringExtra(PLACE_ID))

        setupUI()
    }

    private fun setupUI() {
        val manager = LinearLayoutManager(applicationContext)
        manager.reverseLayout = true
        products_list.layoutManager = manager
    }

    private fun getProductFromPlace(placeId: String) {
        ProductsService(ListProductsRequestHandler(this)).list(placeId)
    }

    fun onSuccessListProducts(products: MutableList<ProductFetched>) {
        Log.e("ProdcuctsList", products.toString())

        products_list.adapter = ProductsAdapter(products)
    }
}
