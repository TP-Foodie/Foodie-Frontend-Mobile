package com.taller.tp.foodie.model.common

import com.taller.tp.foodie.model.OrderedProduct

class HeavyDataTransferingHandler {

    companion object {
        @Volatile
        private var INSTANCE: HeavyDataTransferingHandler? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HeavyDataTransferingHandler().also {
                    INSTANCE = it
                }
            }
    }

    private lateinit var orderJson: String
    private lateinit var listOrderedProducts: MutableList<OrderedProduct>

    fun saveOrderJson(orderJson: String) {
        this.orderJson = orderJson
    }

    fun getOrderJson(): String {
        return orderJson
    }

    fun saveOrderedProducts(orderedProducts: MutableList<OrderedProduct>) {
        this.listOrderedProducts = orderedProducts
    }

    fun getOrderedProducts(): MutableList<OrderedProduct> {
        return listOrderedProducts
    }
}