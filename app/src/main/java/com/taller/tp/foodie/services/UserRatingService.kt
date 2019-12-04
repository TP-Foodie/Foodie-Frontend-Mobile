package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class UserRatingService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

    companion object {
        // endpoint
        const val USER_RATINGS_RESOURCE = "/user_ratings/"
    }

    fun rateUser(user: User, rating: Int, order: Order) {
        val description = ""

        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        val jsonRequest = JSONObject()
        jsonRequest.put("rating", rating)
        jsonRequest.put("user", user.id)
        jsonRequest.put("description", description)
        jsonRequest.put("order", order.id)

        client.doPost(USER_RATINGS_RESOURCE, listener, jsonRequest, errorListener)
    }
}