package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.ui.RegisterActivity
import org.json.JSONObject


class RegisterRequestHandler(private val activity: RegisterActivity) : RequestHandler {
    private lateinit var text : CharSequence

    private val button = activity.findViewById<Button>(R.id.register_submit_btn)
    private val progressBar = activity.findViewById<ProgressBar>(R.id.loading_bar)

    override fun begin() {
        text = button.text
        button.text = null
        progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        progressBar.visibility = View.INVISIBLE
        button.text = text
    }

    override fun onError() {
        stopLoading()
        ErrorHandler.handleError(activity.findViewById<View>(R.id.context_view))
    }

    override fun onSuccess(response: JSONObject?) {
        stopLoading()
        activity.finish()
    }

}