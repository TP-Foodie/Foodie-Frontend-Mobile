package com.taller.tp.foodie.model

class OrderOwner(val name: String){
    private var email: String? = null
    private var phone: String? = null
    private var image: String? = null

    fun setEmail(email: String): OrderOwner{
        this.email = email
        return this
    }

    fun setPhone(phone: String): OrderOwner{
        this.phone = phone
        return this
    }

    fun setImage(image: String): OrderOwner{
        this.image = image
        return this
    }
}