package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ProfileFragment
import org.json.JSONObject
import java.lang.ref.WeakReference

class CleanFcmTokenRequestHandler(val fragment: WeakReference<ProfileFragment>) :
    RequestHandler {

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("CleanFcmTokenReqHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(fragment.get()?.activity?.findViewById(R.id.container)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        fragment.get()?.onCleanFcmTokenSuccess()
    }
}