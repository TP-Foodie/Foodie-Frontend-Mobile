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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.common.AuthErrors.EMAIL_ERROR
import com.taller.tp.foodie.model.common.AuthErrors.PASSWORD_ERROR
import com.taller.tp.foodie.utils.emailIsValid
import com.taller.tp.foodie.utils.passwordIsValid
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupGotoRegister()

        auth = FirebaseAuth.getInstance()

        configureGoogleSignIn()
        configureFacebookSignIn()
        configurePasswordLogin()
    }

    private fun configureGoogleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
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
                    // authenticate with Firebase
                    authenticateWithFirebase(
                        FacebookAuthProvider.getCredential(loginResult.accessToken.token),
                        loginResult.accessToken.token
                    )
                }

                override fun onCancel() {
                    // facebook:onCancel
                }

                override fun onError(error: FacebookException) {
                    // facebook:onError
                    Log.w("Error Facebook Login", error.message)
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
                password_field_layout.error = PASSWORD_ERROR
                return@setOnClickListener
            }

            if (!emailIsValid(email)) {
                email_field_layout.error = EMAIL_ERROR
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(this.localClassName, "signInWithEmail:success")

                        // send email and password to backend server
                        sendEmailAndPasswordToBackendServer(email, password)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(this.localClassName, "signInWithEmail:failure", task.exception)
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful
                val account = task.getResult(ApiException::class.java)

                // authenticate with Firebase
                authenticateWithFirebase(
                    GoogleAuthProvider.getCredential(account?.idToken, null),
                    account?.idToken
                )
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Error Google Sign In", "Status Code: " + e.statusCode)
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun sendTokenToBackendServer(token: String?) {
        TODO("not implemented")
    }

    private fun sendEmailAndPasswordToBackendServer(email: String, password: String) {
        TODO("not implemented")
    }

    private fun authenticateWithFirebase(credential: AuthCredential, token: String?) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Firebase sign in success
                    Log.d(this.localClassName, "signInWithCredential:success")

                    // send token to backend server
                    sendTokenToBackendServer(token)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(this.localClassName, "signInWithCredential:failure", task.exception)
                }
            }
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
