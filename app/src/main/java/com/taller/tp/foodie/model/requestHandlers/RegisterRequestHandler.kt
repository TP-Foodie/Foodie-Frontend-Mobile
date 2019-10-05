package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.RegisterActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class RegisterRequestHandler(private val activity: WeakReference<RegisterActivity>) :
    RequestHandler {

    private val button = activity.get()?.findViewById<Button>(R.id.btn_signout)
    private val progressBar = activity.get()?.findViewById<ProgressBar>(R.id.loading_bar)

    override fun begin() {
        button?.visibility = View.INVISIBLE
        progressBar?.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        progressBar?.visibility = View.INVISIBLE
        button?.visibility = View.VISIBLE
    }

    override fun onError(error: VolleyError) {
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.register_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        // authentica user with backend
        activity.get()?.authenticateWithBackend()
    }
}