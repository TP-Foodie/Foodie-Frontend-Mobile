package com.taller.tp.foodie

import android.widget.Button
import android.widget.TextView
import com.taller.tp.foodie.ui.RegisterActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegisterActivityTest {

    private val activity = Robolectric.buildActivity<RegisterActivity>(RegisterActivity::class.java).setup().get()!!

    private val emailField = activity.findViewById<TextView>(R.id.email_field)
    private val passwordField = activity.findViewById<TextView>(R.id.password_field)

    private val setEmail = { text: String -> emailField.text = text }
    private val setPassword = { text: String -> passwordField.text = text }

    private val onClick = { activity.findViewById<Button>(R.id.register_submit_btn).performClick() }

    private fun assertEmailError() {
        return assertEquals("Por favor ingrese un email válido", emailField.error)
    }

    @Test
    fun shouldShowEmailErrors() {
        setEmail("")
        onClick()

        assertEmailError()
    }

    @Test
    fun shouldNotShowErrorsIfItsValidEmail() {
        setEmail("unemail@undominio.com")
        onClick()

        assertEquals(null, emailField.error)
    }

    @Test
    fun shouldShowEmailErrorIfDoesNotContainAtSymbol() {
        setEmail("unemail")
        onClick()

        assertEmailError()
    }

    @Test
    fun shouldShowPasswordErrorIfItsShorterThanFourCharacters() {
        setPassword("123")
        onClick()

        assertEquals("La contraseña debe ser mayor a 4 caracteres", passwordField.error)
    }
}