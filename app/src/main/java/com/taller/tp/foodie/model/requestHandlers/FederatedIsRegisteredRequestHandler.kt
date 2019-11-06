package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import com.android.volley.VolleyError
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.LoginActivity
import com.taller.tp.foodie.ui.WelcomeActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class FederatedIsRegisteredRequestHandler(private val activity: WeakReference<LoginActivity>) :
    RequestHandler {

    companion object {
        const val TYPE_FIELD = "type"
        const val SUBSCRIPTION_FIELD = "subscription"

        const val FIELD_IS_NULL = "null"
    }

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("FedIsRegRequestHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.login_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        if (response == null) {
            Log.e("FedIsRegRequestHandler", "Response is null")
            stopLoading()
            ErrorHandler.handleError(activity.get()?.findViewById(R.id.login_layout)!!)
            return
        }

        if (isRegistered(response)) {
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
        } else {
            val intent = Intent(activity.get(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.get()?.startActivity(intent)
        }

        activity.get()?.finish()
    }

    private fun isRegistered(response: JSONObject): Boolean {
        if (!response.has(TYPE_FIELD) || !response.has(SUBSCRIPTION_FIELD)) {
            return false
        }

        if (response.getString(TYPE_FIELD) == FIELD_IS_NULL ||
            response.getString(SUBSCRIPTION_FIELD) == FIELD_IS_NULL
        ) {
            return false
        }

        return true
    }
}