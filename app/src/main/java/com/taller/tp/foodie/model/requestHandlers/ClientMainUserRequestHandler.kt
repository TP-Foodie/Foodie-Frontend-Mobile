package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.ClientMainActivity
import org.json.JSONObject
import java.lang.ref.WeakReference


class ClientMainUserRequestHandler(private val activity: WeakReference<ClientMainActivity>) : RequestHandler {
    override fun begin() {}

    override fun onError(error: VolleyError) {
        Log.e("CliMainUserReq", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.container)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        val user = UserService.fromUserJson(response!!)
        activity.get()?.loadUserTypeComponents(user)
    }
}