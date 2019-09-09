package com.taller.tp.foodie.model.validators


class EmptyValidator : Validator() {
    init {
        this.errorMessage = "Por favor ingrese un email válido"
    }

    override fun isValid(value: String): Boolean {
        return !value.isEmpty()
    }
}