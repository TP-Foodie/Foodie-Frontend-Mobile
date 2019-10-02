package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.common.auth.AuthErrors
import com.taller.tp.foodie.model.common.auth.ResponseData
import com.taller.tp.foodie.ui.LoginActivity
import com.taller.tp.foodie.ui.MainActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class FederatedAuthRequestHandler(private val activity: WeakReference<LoginActivity>) :
    RequestHandler {

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
        Log.e("Voley Error", "Error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.login_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        val intent = Intent(activity.get(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.get()?.startActivity(intent)

        activity.get()?.finish()
    }
}

class EmailAuthRequestHandler(private val activity: WeakReference<LoginActivity>) : RequestHandler {

    companion object {
        const val UNAUTHORIZED = 401
    }

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

        // print value error only if unauthorized request
        if (error.networkResponse.statusCode == UNAUTHORIZED) {
            ErrorHandler.handleError(
                activity.get()?.findViewById(R.id.login_layout)!!,
                AuthErrors.EMAIL_OR_PASSWORD_VALUE_ERROR
            )
        } else {
            ErrorHandler.handleError(activity.get()?.findViewById(R.id.login_layout)!!)
        }
    }

    override fun onSuccess(response: JSONObject?) {
        // persist user data
        UserBackendDataHandler(activity.get()?.applicationContext!!)
            .persistUserBackendData(
                response?.getString(ResponseData.TOKEN_FIELD),
                response?.getString(ResponseData.USER_ID_FIELD)
            )

        // go to main activity, clear activity task
        val intent = Intent(activity.get(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.get()?.startActivity(intent)

        activity.get()?.finish()
    }
}