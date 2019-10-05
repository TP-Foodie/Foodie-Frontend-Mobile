package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.MainActivity
import com.taller.tp.foodie.ui.WelcomeActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class FinishRegisterRequestHandler(private val activity: WeakReference<WelcomeActivity>) :
    RequestHandler {

    private var text: CharSequence? = null
    private val button = activity.get()?.findViewById<Button>(R.id.btn_right)
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
        Log.e("FinishRegRequestHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.welcome_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        // go to main activity, clear activity task
        val intent = Intent(activity.get(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.get()?.startActivity(intent)

        activity.get()?.finish()
    }
}