package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import android.widget.Button
import android.widget.ProgressBar

class RegisterRequestHandler(private val progressBar: ProgressBar, private val button: Button) : RequestHandler {
    private lateinit var text : CharSequence

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
    }

    override fun onSuccess() {
        stopLoading()
    }
}