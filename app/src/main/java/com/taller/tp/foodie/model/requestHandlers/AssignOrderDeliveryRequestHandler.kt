package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ChooseDeliveryActivity
import org.json.JSONObject


class AssignOrderDeliveryRequestHandler(private val activity: ChooseDeliveryActivity) : RequestHandler {
    override fun begin() {}

    override fun onError() {
        ErrorHandler.handleError(activity.findViewById<View>(R.id.choose_delivery_context))
    }

    override fun onSuccess(response: JSONObject?) {
        activity.saveAndReturnMain()
    }
}