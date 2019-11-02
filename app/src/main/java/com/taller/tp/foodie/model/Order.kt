package com.taller.tp.foodie.model

class Order(val id: String){
    private var status: String? = null
    private var type: String? = null
    private var number: Int? = null
    private var product: OrderProduct? = null
    private var owner: User? = null

    enum class STATUS(val key: String) {
        TAKEN_STATUS("TS"),
        WAITING_STATUS("WS"),
        DELIVERED_STATUS("DS")
    }
    enum class TYPE(val key: String){
        NORMAL_TYPE("NT"),
        FAVOR_TYPE("FT")
    }

    fun setStatus(status: String): Order{
        this.status = status
        return this
    }

    fun getStatus(): String{
        return status!!
    }

    fun setOwner(owner: User): Order{
        this.owner = owner
        return this
    }

    fun getOwner(): User?{
        return owner
    }

    fun setType(type: String): Order{
        this.type = type
        return this
    }

    fun getType(): String{
        return type!!
    }

    fun getNumber(): Int?{
        return number
    }

    fun setNumber(number: Int): Order{
        this.number = number
        return this
    }

    fun setProduct(product: OrderProduct): Order{
        this.product = product
        return this
    }

    fun getProduct(): String{
        return product!!.name
    }

    fun getPlace(): Place{
        return product!!.place
    }
}