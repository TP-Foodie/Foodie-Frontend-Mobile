package com.taller.tp.foodie.model

class RecoveryPassword(
    val email: String?,
    val password: String?,
    val confirmedPassword: String?,
    val token: String?
) {
    fun isValid(): Boolean {
        if (email == null || password == null || confirmedPassword == null || token == null) {
            return false
        }

        return password == confirmedPassword
    }

    fun get_error_message(): String? {
        if (email == null) {
            return "empty_email"
        }


        return null
    }
}