package com.taller.tp.foodie.ui

import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.taller.tp.foodie.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        //updateUI(account)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupGotoRegister()

        auth = FirebaseAuth.getInstance()

        configureGoogleSignIn()

        google_signInButton.setSize(SignInButton.SIZE_WIDE)
        google_signInButton.setOnClickListener {
            signInGoogle()
        }
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
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful
                val account = task.getResult(ApiException::class.java)

                // TODO: authenticate with Firebase (es necesario?)
                firebaseAuthWithGoogle(account)

                // send token id to backend server
                sendTokenIdToBackendServer(account?.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Error Google Sign In", "Status Code: " + e.statusCode)
                //Snackbar.make(main_layout, "Authentication Failed: ${e.message}",
                //    Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendTokenIdToBackendServer(idToken: String?) {
        TODO("not implemented")
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Google Sign In Success", "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Error Google Sign In", "signInWithCredential:failure", task.exception)
                    //Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
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
            MyClickableSpan(applicationContext), gotoRegisteStart,
            gotoRegisteStart + gotoRegister.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        tv_goto_register.text = spannable
        tv_goto_register.movementMethod = LinkMovementMethod.getInstance()
    }

    private inner class MyClickableSpan internal constructor(applicationContext: Context) :
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
