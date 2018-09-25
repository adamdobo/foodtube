package hu.doboadam.howtube

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*


class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN: Int = 100
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"
        private const val USER_FRIENDS = "user_friends"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        val googleSingInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        callbackManager = CallbackManager.Factory.create()
        facebookLoginButton.setReadPermissions(listOf(EMAIL, PUBLIC_PROFILE))
        facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onCancel() {
                //Do nothing
            }

            override fun onError(error: FacebookException?) {
                Snackbar.make(mainLayout, "Login failed :(", Snackbar.LENGTH_LONG).show()
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }

        })
        googleSignInClient = GoogleSignIn.getClient(this, googleSingInOptions)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
        facebookSignInButton.setOnClickListener {
            facebookLoginButton.performClick()
        }
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                advance()
            } else {
                Snackbar.make(mainLayout, "Login failed :(", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            advance()
        }
    }

    private fun signInWithGoogle(){
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.result
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e(this@LoginActivity.localClassName, "Google Sign-in failed", e)
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        advance()
                    } else {
                        Snackbar.make(mainLayout, "Login failed :(", Snackbar.LENGTH_LONG).show()
                    }
                }
    }

    private fun advance() {
        startActivity(Intent(this, ContentActivity::class.java))
    }
}
