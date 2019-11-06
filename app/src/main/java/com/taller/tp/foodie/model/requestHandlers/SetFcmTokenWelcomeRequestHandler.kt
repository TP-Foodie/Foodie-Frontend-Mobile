package com.taller.tp.foodie.model.requestHandlers

import android.content.Intent
import android.util.Log
import com.android.volley.VolleyError
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.ClientMainActivity
import com.taller.tp.foodie.ui.WelcomeActivity
import org.json.JSONObject
import java.lang.ref.WeakReference

class SetFcmTokenWelcomeRequestHandler(private val activity: WeakReference<WelcomeActivity>) :
    RequestHandler {
    override fun begin() {}

    private fun stopLoading() {}

    override fun onError(error: VolleyError) {
        Log.e("SetFcmTokenWelReqHand", "Volley error: " + error.message)
        stopLoading()
        ErrorHandler.handleError(activity.get()?.findViewById(R.id.welcome_layout)!!)
    }

    override fun onSuccess(response: JSONObject?) {
        // go to main activity, clear activity task
        val intent = Intent(activity.get(), ClientMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.get()?.startActivity(intent)

        activity.get()?.finish()
    }
}