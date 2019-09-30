package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.LoginActivity
import com.taller.tp.foodie.ui.MainActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class AuthRequestHandler(private val activity: WeakReference<LoginActivity>) : RequestHandler {

    private val button = activity.get()?.findViewById<Button>(R.id.btn_register)
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
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.context_view)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        val intent = Intent(activity.get(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.get()?.startActivity(intent)

        activity.get()?.finish()
    }
}