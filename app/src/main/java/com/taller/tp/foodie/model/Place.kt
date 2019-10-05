package com.taller.tp.foodie.model

class Place(val name: String, val coordinate: Coordinate){
    private var id: String? = null

    fun setId(id: String): Place{
        this.id = id
        return this
    }

    fun getId(): String{
        return id!!
    }

}