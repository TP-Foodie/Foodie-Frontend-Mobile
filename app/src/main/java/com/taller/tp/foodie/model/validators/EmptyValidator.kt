package com.taller.tp.foodie.model.validators

import android.widget.TextView

class EmptyValidator(error: String) : Validator(error) {

    override fun isValid(value: String): Boolean {
        return !value.isEmpty()
    }

    override fun validate(field: TextView) {
        if (!isValid(field.text.toString())) field.error = errorMessage
    }
}