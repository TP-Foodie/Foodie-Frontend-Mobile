package com.taller.tp.foodie.model.validators

import android.widget.TextView

abstract class Validator {
    protected var errorMessage = ""

    fun validate(field: TextView) {
        if (!isValid(field.text.toString())) field.error = errorMessage
    }

    protected abstract fun isValid(value: String) : Boolean
}