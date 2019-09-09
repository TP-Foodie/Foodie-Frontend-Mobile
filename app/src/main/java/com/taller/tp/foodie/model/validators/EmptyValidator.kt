package com.taller.tp.foodie.model.validators

class EmptyValidator(error: String) : Validator(error) {
    override fun isValid(value: String): Boolean {
        return value.isNotEmpty()
    }
}