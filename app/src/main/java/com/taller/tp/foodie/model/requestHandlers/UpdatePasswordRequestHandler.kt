package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.RecoveryPasswordActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class UpdatePasswordRequestHandler
    (private val activity: WeakReference<RecoveryPasswordActivity>) : RequestHandler {

    private val button = activity.get()?.findViewById<Button>(R.id.btn_btn_send_token)
    private var text: CharSequence? = null
    private val progressBar = activity.get()?.findViewById<ProgressBar>(R.id.loading_bar)


    override fun begin() {
        text = button?.text
        button?.text = null
        progressBar?.visibility = View.VISIBLE
    }

    override fun onSuccess(response: JSONObject?) {
        activity.get()?.loginPage()
    }

    override fun onError(error: VolleyError) {
        Log.e("RegisterRequestHandler", "Volley error: " + error.localizedMessage)
        progressBar?.visibility = View.INVISIBLE
        button?.text = text

        if (error.networkResponse.statusCode == 404) {
            ErrorHandler.handleUserNotFound(activity.get()?.findViewById(R.id.recovery_password_layout)!!)
        } else if (error.networkResponse.statusCode == 401) {
            ErrorHandler.invalidToken(activity.get()?.findViewById(R.id.recovery_password_layout)!!)
        } else {
            ErrorHandler.handleError(activity.get()?.findViewById(R.id.recovery_password_layout)!!)
        }
    }

}