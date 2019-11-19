package com.taller.tp.foodie.model

data class OrderedProduct(
    var quantity: Int, var productFetched: ProductFetched
)

data class OrderedProductFetched(
    var id: String, var quantity: Int, var productFetched: ProductFetched
)