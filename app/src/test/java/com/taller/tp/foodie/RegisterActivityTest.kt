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

    val activity = Robolectric.buildActivity<RegisterActivity>(RegisterActivity::class.java).setup().get()

    @Test
    fun should_show_email_errors() {
        val emailField = activity.findViewById<TextView>(R.id.email_field)
        emailField.text = "saraza"

        activity.findViewById<Button>(R.id.register_submit_btn).performClick()

        assertEquals("Por favor ingrese un email valido", emailField.error)
    }

}