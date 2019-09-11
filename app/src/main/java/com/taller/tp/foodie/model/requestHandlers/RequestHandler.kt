package com.taller.tp.foodie.model.requestHandlers


interface RequestHandler {
    fun begin()

    fun onSuccess()

    fun onError()
}