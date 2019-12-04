package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.OrderDetailActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class RateUserRequestHandler(val activity: WeakReference<OrderDetailActivity>) :
    RequestHandler {

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("RateUserReqHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.order_detail_context)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        activity.get()?.onRateUserSuccess()
    }
}