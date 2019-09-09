package com.taller.tp.foodie.model.validators


class LengthValidator : Validator() {
    private val minLen = 4

    init {
        this.errorMessage = "La contraseña debe ser mayor a 4 caracteres"
    }

    override fun isValid(value: String): Boolean {
        return value.length < minLen
    }
}