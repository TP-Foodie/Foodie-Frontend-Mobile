package com.taller.tp.foodie.model.validators

import android.widget.TextView

abstract class Validator(error: String) {
    val errorMessage = error

    abstract fun validate(field: TextView)

    protected abstract fun isValid(value: String) : Boolean
}