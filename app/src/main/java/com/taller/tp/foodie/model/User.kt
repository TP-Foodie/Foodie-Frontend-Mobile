package com.taller.tp.foodie.model

open class User(val id: String?, val name: String, val image: String?){
    var email: String? = null
        get() = field
    fun setEmail(email: String?) : User {
        this.email = email
        return this
    }
    var lastName: String? = null
        get() = field
    fun setLastName(lastName: String?) : User {
        this.lastName = lastName
        return this
    }
    var phone: String? = null
        get() = field
    fun setPhone(phone: String?) : User {
        this.phone = phone
        return this
    }
}