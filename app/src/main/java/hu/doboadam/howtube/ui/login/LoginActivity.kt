package hu.doboadam.howtube.ui.login

import android.app.slice.SliceManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.api.Context
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.Indexable
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import hu.doboadam.howtube.R
import hu.doboadam.howtube.ui.content.ContentActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN: Int = 100
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addAppToFirebaseIndex()
        grantSlicePermissions()
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

    private fun addAppToFirebaseIndex() {
        FirebaseAppIndex.getInstance().update(Indexable.Builder()
                .setUrl("https://doboadam.hu/start")
                .setName("FoodTube")
                .setKeywords("food", "recipe", "recipes", "cooking", "food videos", "recipe videos")
                .build())
    }

    private fun grantSlicePermissions() {
        val manager = androidx.slice.SliceManager.getInstance(this)
        val mainUri = Uri.parse("content://hu.doboadam.howtube/recipe")
        manager.grantSlicePermission("com.google.android.googlequicksearchbox", mainUri)
        manager.grantSlicePermission("com.google.android.gms", mainUri)
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
        } else {
            LoginManager.getInstance().logOut()
            googleSignInClient.signOut()
        }
    }

    private fun signInWithGoogle(){
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                if(task.isSuccessful) {
                    val account = task.result
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Timber.e("Google Sign-in failed with $e")
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
        finish()
        startActivity(Intent(this, ContentActivity::class.java))
    }
}
