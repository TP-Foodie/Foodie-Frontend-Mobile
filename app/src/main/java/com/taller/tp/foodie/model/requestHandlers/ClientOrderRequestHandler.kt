package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ClientMainActivity
import org.json.JSONObject


class ClientOrderRequestHandler(private val activity: ClientMainActivity) : RequestHandler {
    override fun begin() {}

    override fun onError() {
        ErrorHandler.handleError(activity.findViewById<View>(R.id.map_choice_context))
    }

    override fun onSuccess(response: JSONObject?) {
        activity.saveAndChooseDelivery(response!!)
    }
}