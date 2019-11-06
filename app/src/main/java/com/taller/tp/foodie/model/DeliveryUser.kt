package com.taller.tp.foodie.model

class DeliveryUser(id: String, name: String, image: String?) :
    User(id, name, image) {
    var coordinates: Coordinate ? = null
    init{
        type = USER_TYPE.DELIVERY
    }

}