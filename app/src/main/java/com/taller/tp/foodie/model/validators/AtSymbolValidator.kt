package com.taller.tp.foodie.model.validators

const val AT_SYMBOL = "@"

class AtSymbolValidator : Validator() {
    init {
        this.errorMessage = "Por favor ingrese un email válido"
    }

    override fun isValid(value: String): Boolean {
        return value.contains(AT_SYMBOL)
    }
}