package com.taller.tp.foodie.model.validators

import android.widget.TextView

abstract class Validator(private val errorMessage: String) {
    fun validate(field: TextView) {
        if (!isValid(field.text.toString())) field.error = errorMessage
    }

    protected abstract fun isValid(value: String) : Boolean
}