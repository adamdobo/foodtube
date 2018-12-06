package hu.doboadam.szakdoga.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import androidx.slice.SliceManager
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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.model.Result
import hu.doboadam.szakdoga.repository.SharedPrefRepository
import hu.doboadam.szakdoga.ui.BaseViewModelActivity
import hu.doboadam.szakdoga.ui.content.ContentActivity
import kotlinx.android.synthetic.main.activity_main.*


class LoginActivity : BaseViewModelActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN: Int = 100
    private lateinit var callbackManager: CallbackManager
    private lateinit var sharedPrefRepository: SharedPrefRepository
    private lateinit var viewModel: LoginViewModel

    companion object {
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"
        private const val CONTENT_URI = "content://hu.doboadam.szakdoga/recipe"
        private const val GOOGLE_SEARCH_PACKAGE = "com.google.android.googlequicksearchbox"
        private const val GOOGLE_PLAY_PACKAGE = "com.google.android.gms"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        grantSlicePermissions()
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        observeViewModel()
        updateFirebaseIndex()
        firebaseAuth = FirebaseAuth.getInstance()
        val googleSingInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        callbackManager = CallbackManager.Factory.create()
        facebookLoginButton.setReadPermissions(listOf(EMAIL, PUBLIC_PROFILE))
        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                //Do nothing
            }

            override fun onError(error: FacebookException?) {
                Snackbar.make(mainLayout, "Login failed", Snackbar.LENGTH_LONG).show()
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

    private fun observeViewModel() {
        viewModel.indexingResult.observe(this, Observer { value ->
            value?.also {
                if (it == Result.Success) {
                    sharedPrefRepository.setFirstRun(false)
                }
            }
        })
    }

    private fun updateFirebaseIndex() {
        sharedPrefRepository = SharedPrefRepository(this)
        viewModel.updateAppIndex(sharedPrefRepository.isFirstRun())
    }

    private fun grantSlicePermissions() {
        val manager = SliceManager.getInstance(this)
        val mainUri = Uri.parse(CONTENT_URI)
        manager.grantSlicePermission(GOOGLE_SEARCH_PACKAGE, mainUri)
        manager.grantSlicePermission(GOOGLE_PLAY_PACKAGE, mainUri)
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                advance()
            } else {
                Snackbar.make(mainLayout, "Login failed", Snackbar.LENGTH_LONG).show()
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

    private fun signInWithGoogle() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                if (task.isSuccessful) {
                    val account = task.result
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Snackbar.make(mainLayout, "Login failed", Snackbar.LENGTH_LONG).show()
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        advance()
                    } else {
                        Snackbar.make(mainLayout, "Login failed", Snackbar.LENGTH_LONG).show()
                    }
                }
    }

    private fun advance() {
        finish()
        startActivity(Intent(this, ContentActivity::class.java))
    }

    override fun startListeningToDb() {
        viewModel.startListeningToDbChanges()
    }

    override fun stopListeningToDb() {
        viewModel.stopListeningToDbChanges()
    }
}
