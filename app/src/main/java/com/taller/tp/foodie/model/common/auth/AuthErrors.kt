package com.taller.tp.foodie.model.common.auth

object AuthErrors {

    const val NAME_ERROR = "Por favor ingrese un nombre válido"
    const val LAST_NAME_ERROR = "Por favor ingrese un apellido válido"
    const val PHONE_ERROR = "Por favor ingrese un numero de telefono valido (11-XXXX-XXXX)"
    const val INVALID_EMAIL_ERROR = "Por favor ingrese un email válido"
    const val INVALID_PASSWORD_ERROR =
        "Por favor ingrese una contraseña válida (minimo 6 caracteres)"
    const val PASSWORD_CONFIRM_ERROR = "Las contraseñas no coinciden"

    const val EMAIL_OR_PASSWORD_VALUE_ERROR = "El email y/o la contraseña son incorrectos"
}