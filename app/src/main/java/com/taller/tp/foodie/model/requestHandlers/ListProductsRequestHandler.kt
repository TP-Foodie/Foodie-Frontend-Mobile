package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.ProductFetched
import com.taller.tp.foodie.services.ProductsService
import com.taller.tp.foodie.services.SERVICE_ARRAY_RESPONSE
import com.taller.tp.foodie.ui.ProductsActivity
import org.json.JSONObject

class ListProductsRequestHandler(private val activity: ProductsActivity) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("ListProductsReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.products_layout))
    }

    override fun onSuccess(response: JSONObject?) {
        val productsResponse = response!!.getJSONArray(SERVICE_ARRAY_RESPONSE)
        val products = mutableListOf<ProductFetched>()
        for (i in 0 until productsResponse.length()) {
            products.add(ProductsService.fromProductJson(productsResponse.getJSONObject(i)))
        }
        activity.onSuccessListProducts(products)
    }
}