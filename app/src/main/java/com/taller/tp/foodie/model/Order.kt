package com.taller.tp.foodie.model

class Order(val id: String){
    private var status: String? = null
    private var type: String? = null
    private var owner: OrderOwner? = null
    private var product: OrderProduct? = null

    enum class STATUS(val key: String) {
        TAKEN_STATUS("TS")
    }
    enum class TYPE(val key: String){
        NORMAL_TYPE("NT"),
        FAVOR_TYPE("FT")

    }

    fun setStatus(status: String): Order{
        this.status = status
        return this
    }

    fun setType(type: String): Order{
        this.type = type
        return this
    }

    fun setOwner(owner: OrderOwner): Order{
        this.owner = owner
        return this
    }

    fun setProduct(product: OrderProduct): Order{
        this.product = product
        return this
    }

    fun getPlace(): Place{
        return product!!.place
    }
}