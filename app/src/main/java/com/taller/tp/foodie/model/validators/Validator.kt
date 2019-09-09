package com.taller.tp.foodie.model.validators

import android.widget.TextView

abstract class Validator(error: String) {
    private val errorMessage = error

    fun validate(field: TextView) {
        if (!isValid(field.text.toString())) field.error = errorMessage
    }

    protected abstract fun isValid(value: String) : Boolean
}