package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.android.volley.VolleyError
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.common.auth.AuthErrors
import com.taller.tp.foodie.model.common.auth.ResponseData
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.LoginActivity
import com.taller.tp.foodie.ui.RegisterActivity
import com.taller.tp.foodie.ui.WelcomeActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class FederatedAuthRequestHandler(private val activity: WeakReference<LoginActivity>) :
    RequestHandler {

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("AuthRequestHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.login_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        // persist user data
        UserBackendDataHandler.getInstance().persistUserBackendData(
            response?.getString(ResponseData.TOKEN_FIELD)
        )

        activity.get()?.checkIfFederatedIsRegistered()
    }
}

class EmailAuthFromLoginRequestHandler(private val activity: WeakReference<LoginActivity>) :
    RequestHandler {

    companion object {
        const val UNAUTHORIZED = 401
    }

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("AuthRequestHandler", "Volley error: " + error.message)
        stopLoading()

        // print value error only if unauthorized request
        if (error.networkResponse != null && error.networkResponse.statusCode == UNAUTHORIZED) {
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
        UserBackendDataHandler.getInstance().persistUserBackendData(
            response?.getString(ResponseData.TOKEN_FIELD)
        )

        // get fcm token for device
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful || task.result == null) {
                    Log.w("AuthRequestHandler", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // set fcm token in backend
                UserService(SetFcmTokenLoginRequestHandler(activity)).updateUserFcmToken(token!!)
            })
    }
}

class EmailAuthFromRegisterRequestHandler(private val activity: WeakReference<RegisterActivity>) :
    RequestHandler {

    private var text: CharSequence? = null
    private val button = activity.get()?.findViewById<Button>(R.id.btn_register)
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
        Log.e("AuthRequestHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.register_layout)!!)

    }

    override fun onSuccess(response: JSONObject?) {
        // persist user data
        UserBackendDataHandler.getInstance().persistUserBackendData(
            response?.getString(ResponseData.TOKEN_FIELD)
        )

        // go to welcome activity, clear activity task
        val intent = Intent(activity.get(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.get()?.startActivity(intent)

        activity.get()?.finish()
    }
}