package com.taller.tp.foodie.model.validators

const val AT_SYMBOL = "@"

class AtSymbolValidator(error: String) : Validator(error) {
    override fun isValid(value: String): Boolean {
        return value.contains(AT_SYMBOL)
    }
}