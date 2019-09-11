package com.taller.tp.foodie.model.requestHandlers

import android.view.View
import android.widget.ProgressBar

class RegisterRequestHandler(private val progressBar: ProgressBar) : RequestHandler {

    private fun stopLoading() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun onError() {
        stopLoading()
    }

    override fun onSuccess() {
        stopLoading()
    }
}