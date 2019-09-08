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
    private val setEmail = { text: String -> emailField.text = text }
    private val onClick = { activity.findViewById<Button>(R.id.register_submit_btn).performClick() }

    @Test
    fun should_show_email_errors() {
        setEmail("")
        onClick()

        assertEquals("Por favor ingrese un email válido", emailField.error)
    }

    @Test
    fun should_not_show_errors_if_its_valid_email() {
        setEmail("unemail@undominio.com")
        onClick()

        assertEquals(null, emailField.error)
    }
}