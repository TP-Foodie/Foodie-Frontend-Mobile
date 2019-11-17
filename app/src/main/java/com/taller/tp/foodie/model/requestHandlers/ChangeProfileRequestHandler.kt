package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ChangeProfileActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class ChangeProfileRequestHandler(private val activity: WeakReference<ChangeProfileActivity>) :
    RequestHandler {

    private var text: CharSequence? = null
    private val button = activity.get()?.findViewById<TextView>(R.id.btn_confirm_changes)
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
        Log.e("ChangeProfileReqHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.welcome_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        activity.get()?.finish()
    }
}