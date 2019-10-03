package com.taller.tp.foodie.model

const val NORMAL_TYPE: String = "NT"
const val FAVOUR_TYPE: String = "FT"

class Order(val orderType: String, val owner: String, val orderProduct: OrderProduct){

}