package com.taller.tp.foodie.model.requestHandlers

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.TokenActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class SendTokenRequestHandler(private val activity: WeakReference<TokenActivity>) :
    RequestHandler {

    private var text: CharSequence? = null
    private val button = activity.get()?.findViewById<Button>(R.id.btn_send_token)
    private val progressBar = activity.get()?.findViewById<ProgressBar>(R.id.loading_bar)

    override fun begin() {
        text = button?.text
        button?.text = null
        progressBar?.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        progressBar?.visibility = View.INVISIBLE
        button?.text = text
    }

    override fun onError(error: VolleyError) {
        Log.e("RegisterRequestHandler", "Volley error: " + error.localizedMessage)
        stopLoading()

        if (error.networkResponse.statusCode == 404) {
            ErrorHandler.handleUserNotFound(activity.get()?.findViewById(R.id.token_layout)!!)
        } else {
            ErrorHandler.handleError(activity.get()?.findViewById(R.id.token_layout)!!)
        }
    }

    override fun onSuccess(response: JSONObject?) {
        activity.get()?.goToRecoveryPasswordActivity()
    }
}