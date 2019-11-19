package com.taller.tp.foodie.model

data class ProductFetched(
    var id: String, var name: String, var description: String, var price: Int,
    var place: Place, var image: String
)