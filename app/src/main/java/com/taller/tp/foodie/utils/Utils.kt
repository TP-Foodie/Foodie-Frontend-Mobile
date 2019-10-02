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

fun nameIsValid(name: String?): Boolean {
    if (name.isNullOrEmpty()) {
        return false
    }

    return true
}

fun lastNameIsValid(lastName: String?): Boolean {
    if (lastName.isNullOrEmpty()) {
        return false
    }

    return true
}

fun phoneIsValid(phone: String?): Boolean {
    if (phone.isNullOrEmpty()) {
        return false
    }

    if (phone.length == 12 && phone[0] == '1' && phone[1] == '1') {
        return true
    }

    return false
}