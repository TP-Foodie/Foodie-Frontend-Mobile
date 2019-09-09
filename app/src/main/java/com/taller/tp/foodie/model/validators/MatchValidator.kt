package com.taller.tp.foodie.model.validators

class MatchValidator(private var valueToMatch: String, error: String): Validator(error) {

    override fun isValid(value: String): Boolean {
        return value == valueToMatch
    }
}