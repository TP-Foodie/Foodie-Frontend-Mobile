package com.taller.tp.foodie.model

open class User(val id: String?, val name: String, val image: String?){
    enum class USER_TYPE { CUSTOMER, DELIVERY }
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

    var type: USER_TYPE = USER_TYPE.CUSTOMER
        get() = field
    fun setType(type: String) : User {
        this.type= USER_TYPE.valueOf(type)
        return this
    }
}