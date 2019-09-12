package com.taller.tp.foodie.model

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.taller.tp.foodie.R

class ErrorHandler {
    companion object {
        fun handleError(contextView: View) {
            val snackbar = Snackbar.make(contextView, R.string.general_error, Snackbar.LENGTH_SHORT)
            snackbar.view.setBackgroundColor(Color.RED)
            snackbar.show()
        }
    }
}