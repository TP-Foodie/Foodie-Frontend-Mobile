package com.taller.tp.foodie.model

class Order(val id: String){
    private var status: STATUS? = null
    private var type: String? = null
    private var number: Int? = null
    private var product: OrderProduct? = null
    private var owner: User? = null
    private var delivery: DeliveryUser? = null
    private var chat: ChatFetched? = null
//    private var paymentMethod: PAYMENT_METHOD = PAYMENT_METHOD.CPM

    enum class PAYMENT_METHOD {
        CPM,
        CRPM
    }
    enum class STATUS(val key: String) {
        TAKEN_STATUS("TS"),
        WAITING_STATUS("WS"),
        DELIVERED_STATUS("DS");

        companion object{
            fun fromKey(key: String?): STATUS {
                when(key){
                    "TS" -> return TAKEN_STATUS
                    "WS" -> return WAITING_STATUS
                    "DS" -> return DELIVERED_STATUS
                }
                throw RuntimeException("Unreachable")
            }
        }
    }
    enum class TYPE(val key: String){
        NORMAL_TYPE("NT"),
        FAVOR_TYPE("FT")
    }

    fun setStatus(status: String?): Order{
        if (!status.isNullOrEmpty()){
            this.status = STATUS.fromKey(status)
        }
        return this
    }

    fun getStatus(): STATUS{
        return status!!
    }

    fun setOwner(owner: User): Order{
        this.owner = owner
        return this
    }

    fun getOwner(): User?{
        return owner
    }

    fun setDelivery(delivery: DeliveryUser?): Order{
        this.delivery = delivery
        return this
    }

    fun getDelivery(): DeliveryUser?{
        return delivery
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

    fun setChat(chat: ChatFetched?): Order {
        this.chat = chat
        return this
    }

    fun getChat(): ChatFetched? {
        return this.chat
    }

//    fun getPaymentMethod(): String{
//        return paymentMethod.label
//    }
//
//    fun setPaymentMethod(paymentMethod: String): Order{
//        this.paymentMethod = PAYMENT_METHOD.valueOf(paymentMethod)
//        return this
//    }
}