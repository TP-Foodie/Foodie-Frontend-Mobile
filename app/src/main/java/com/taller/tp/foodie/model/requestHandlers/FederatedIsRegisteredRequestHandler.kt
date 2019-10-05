package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.LoginActivity
import com.taller.tp.foodie.ui.MainActivity
import com.taller.tp.foodie.ui.WelcomeActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class FederatedIsRegisteredRequestHandler(private val activity: WeakReference<LoginActivity>) :
    RequestHandler {

    companion object {
        const val NOT_FOUND = 404
    }

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        // TODO: verificar que funcione
        if (error.networkResponse.statusCode == NOT_FOUND) {
            val intent = Intent(activity.get(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.get()?.startActivity(intent)
            activity.get()?.finish()
        } else {
            // error real
            Log.e("FedIsRegRequestHandler", "Volley error: " + error.localizedMessage)
            stopLoading()
            ErrorHandler.handleError(activity.get()?.findViewById(R.id.login_layout)!!)
        }
    }

    override fun onSuccess(response: JSONObject?) {
        // TODO: verificar que funcione
        val intent = Intent(activity.get(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.get()?.startActivity(intent)

        activity.get()?.finish()
    }
}