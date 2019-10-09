package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.services.AuthService.Companion.ID_FIELD
import com.taller.tp.foodie.ui.CLIENT_ID_KEY
import com.taller.tp.foodie.ui.ClientMainActivity
import com.taller.tp.foodie.ui.LauncherActivity
import com.taller.tp.foodie.ui.WelcomeActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class CheckRegistrationRequestHandler(private val activity: WeakReference<LauncherActivity>) :
    RequestHandler {

    companion object {
        const val TYPE_FIELD = "type"
        const val SUBSCRIPTION_FIELD = "subscription"
    }

    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("CheckRegRequestHandler", "Volley error: " + error.localizedMessage)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.launcher_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        if (response == null) {
            Log.e("CheckRegRequestHandler", "Response is null")
            stopLoading()
            ErrorHandler.handleError(activity.get()?.findViewById(R.id.launcher_layout)!!)
            return
        }

        if (response.has(TYPE_FIELD) && response.has(SUBSCRIPTION_FIELD)) {
            val intent = Intent(activity.get(), ClientMainActivity::class.java).apply{
                val userId = response.getString(ID_FIELD)
                putExtra(CLIENT_ID_KEY,userId)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.get()?.startActivity(intent)
        } else {
            val intent = Intent(activity.get(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.get()?.startActivity(intent)
        }

        activity.get()?.finish()
    }
}