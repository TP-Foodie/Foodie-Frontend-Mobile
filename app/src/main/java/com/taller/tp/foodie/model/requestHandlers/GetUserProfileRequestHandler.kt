package com.taller.tp.foodie.model.requestHandlers

import android.util.Log
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.UserProfile
import com.taller.tp.foodie.ui.ProfileFragment
import org.json.JSONObject
import java.lang.ref.WeakReference

class GetUserProfileRequestHandler(private val fragment: WeakReference<ProfileFragment>) :
    RequestHandler {

    override fun begin() {}

    override fun onSuccess(response: JSONObject?) {
        val userProfile = Gson().fromJson(response.toString(), UserProfile::class.java)

        fragment.get()?.fillProfile(userProfile)
    }

    override fun onError(error: VolleyError) {
        Log.e("GetUserReqHandler", "Volley error: " + error.localizedMessage)
        ErrorHandler.handleError(fragment.get()?.activity?.findViewById(R.id.container)!!)
    }
}