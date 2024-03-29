package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ChatFetched
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.ProfileService
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.OrderDetailActivity
import org.json.JSONObject


open class OrderDetailRequestHandler(private val activity: OrderDetailActivity) : RequestHandler {

    private enum class OPERATION { GET, UPDATE, GET_OWNER, UPDATE_FAVOUR, CHAT_CREATION }
    private var op = OPERATION.GET
    override fun begin() {}

    fun forUpdate() : OrderDetailRequestHandler{
        op = OPERATION.UPDATE
        return this
    }

    fun getOwner() : OrderDetailRequestHandler{
        op = OPERATION.GET_OWNER
        return this
    }

    override fun onError(error: VolleyError) {
        Log.e("OrderDetailReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.findViewById<View>(R.id.order_detail_context))
    }

    override fun onSuccess(response: JSONObject?) {
        when(op){
            OPERATION.GET -> {
                val order = OrderService.fromOrderJson(response!!, withDetail = true)
                activity.populateFields(order, response)
            }
            OPERATION.UPDATE -> {
                activity.onUpdateSuccess()
            }
            OPERATION.GET_OWNER -> {
                val user = UserService.fromUserJson(response!!)
                activity.setUserData(user)
                activity.populateOrder()
            }
            OPERATION.UPDATE_FAVOUR -> {
                val order = Gson().fromJson(response.toString(), Order::class.java)
                activity.createChat(order)
            }
            OPERATION.CHAT_CREATION -> {
                val chat = Gson().fromJson(response.toString(), ChatFetched::class.java)
                activity.assignChat(chat)
            }
        }
    }

    fun forUpdateFavour(): OrderDetailRequestHandler {
        this.op = OPERATION.UPDATE_FAVOUR
        return this
    }

    fun forChatCreation(): OrderDetailRequestHandler {
        this.op = OPERATION.CHAT_CREATION
        return this
    }

}