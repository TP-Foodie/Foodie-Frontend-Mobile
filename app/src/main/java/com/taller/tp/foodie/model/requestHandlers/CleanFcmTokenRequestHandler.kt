package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ClientMainActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class CleanFcmTokenRequestHandler(val activity: WeakReference<ClientMainActivity>) :
    RequestHandler {

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("CleanFcmTokenReqHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.map_choice_context)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        activity.get()?.onCleanFcmTokenSuccess()
    }
}