package com.taller.tp.foodie.model

private const val NORMAL_TYPE: String = "NT"
private const val FAVOUR_TYPE: String = "FT"

class Order(val isFavour: Boolean, val owner: String, val orderProduct: OrderProduct){
    private lateinit var id: String

    var orderType: String

    init{
        if (isFavour)
            orderType =  FAVOUR_TYPE
        else
            orderType = NORMAL_TYPE
    }
}