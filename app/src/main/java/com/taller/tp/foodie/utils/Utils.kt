package com.taller.tp.foodie.utils

/*
 *
 *   Authentication Utilities
 *
 */

fun passwordIsValid(password: String?): Boolean {
    if (password.isNullOrEmpty()) {
        return false
    }

    // check length
    if (password.length < 6) {
        return false
    }

    return true
}

fun passwordsAreEqualAndNotEmpty(password: String?, confirmationPassword: String?): Boolean {
    // check nullity
    if (password.isNullOrEmpty() || confirmationPassword.isNullOrEmpty()) {
        return false
    }

    // check equal
    if (password == confirmationPassword) {
        return true
    }

    return false
}

fun emailIsValid(email: String?): Boolean {
    if (email.isNullOrEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return false
    }

    return true
}