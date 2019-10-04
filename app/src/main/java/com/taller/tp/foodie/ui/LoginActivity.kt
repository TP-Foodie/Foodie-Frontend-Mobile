package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.common.auth.AuthErrors.INVALID_EMAIL_ERROR
import com.taller.tp.foodie.model.common.auth.AuthErrors.INVALID_PASSWORD_ERROR
import com.taller.tp.foodie.model.requestHandlers.EmailAuthFromLoginRequestHandler
import com.taller.tp.foodie.model.requestHandlers.FederatedAuthRequestHandler
import com.taller.tp.foodie.services.AuthService
import com.taller.tp.foodie.utils.emailIsValid
import com.taller.tp.foodie.utils.passwordIsValid
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.ref.WeakReference

@Suppress("UNUSED_PARAMETER")
class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 9001
    }

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupGotoRegister()

        configureGoogleSignIn()
        configureFacebookSignIn()
        configurePasswordLogin()
    }

    private fun configureGoogleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("218514362361-nchqu6j59rcskl1vmadfp6gl6ud8a0oo.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient?.signOut()

        google_signInButton.setSize(SignInButton.SIZE_WIDE)
        google_signInButton.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun configureFacebookSignIn() {
        logOutFacebookButton()

        mCallbackManager = CallbackManager.Factory.create()

        facebook_SignInButton.setPermissions("email", "public_profile")
        facebook_SignInButton.registerCallback(
            mCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // send token to backend
                    authenticateWithBackend(loginResult.accessToken.token)
                }

                override fun onCancel() {
                    // facebook:onCancel
                }

                override fun onError(error: FacebookException) {
                    // facebook:onError
                    Log.e("Error Facebook Login", error.message)

                    ErrorHandler.handleError(login_layout)
                }
            })
    }

    private fun logOutFacebookButton() {
        LoginManager.getInstance().logOut()
    }

    private fun configurePasswordLogin() {
        btn_login.setOnClickListener {
            email_field_layout.error = null
            password_field_layout.error = null

            val email = email_field.text.toString()
            val password = password_field.text.toString()

            if (!passwordIsValid(password)) {
                password_field_layout.error = INVALID_PASSWORD_ERROR
                return@setOnClickListener
            }

            if (!emailIsValid(email)) {
                email_field_layout.error = INVALID_EMAIL_ERROR
                return@setOnClickListener
            }

            // send email and password to backend
            authenticateWithBackend(email, password)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // get account, this can throw exception
                val account = task.getResult(ApiException::class.java)

                // Google Sign In was successful
                Log.d(LoginActivity::class.java.simpleName, "Google Sign In was successful")

                // send token to backend
                authenticateWithBackend(account?.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(
                    LoginActivity::class.java.simpleName,
                    "Error Google Sign In, Status Code: " + e.statusCode
                )

                ErrorHandler.handleError(login_layout)
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun authenticateWithBackend(token: String?) {
        if (token.isNullOrEmpty()) {
            Log.e(
                LoginActivity::class.java.simpleName,
                "Error Google Sign In, token is null or empty"
            )

            ErrorHandler.handleError(login_layout)
            return
        }

        AuthService(this, FederatedAuthRequestHandler(WeakReference(this)))
            .federatedAuthenticationWithBackend(token)
    }

    private fun authenticateWithBackend(email: String, password: String) {
        AuthService(this, EmailAuthFromLoginRequestHandler(WeakReference(this)))
            .emailAndPasswordAuthenticationWithBackend(email, password)
    }

    private fun setupGotoRegister() {
        val resources = resources
        val gotoRegister = resources.getString(R.string.goto_register)
        val phraseFormatted = resources.getString(R.string.tv_user_not_registered)

        val phrase = String.format(phraseFormatted, gotoRegister)
        val gotoRegisteStart = phrase.indexOf(gotoRegister)

        val spannable = SpannableStringBuilder(phrase)
        spannable.setSpan(
            MyClickableSpan(), gotoRegisteStart,
            gotoRegisteStart + gotoRegister.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        tv_goto_register.text = spannable
        tv_goto_register.movementMethod = LinkMovementMethod.getInstance()
    }

    private inner class MyClickableSpan internal constructor() :
        ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        }

        override fun onClick(view: View) {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
    }
}
