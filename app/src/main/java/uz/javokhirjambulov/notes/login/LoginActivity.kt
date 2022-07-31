package uz.javokhirjambulov.notes.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.commons.Constants
import uz.javokhirjambulov.notes.ui.MainIntroActivity


@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnGoogleSignIn: SignInButton
    private lateinit var preferencesPrivate: SharedPreferences
    private lateinit var skip:TextView


    companion object{
        private const val RC_SIGN_IN=2008
        private const val TAG = "LoginActivity"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        btnGoogleSignIn=findViewById(R.id.btnGoogleSingIn)
        skip = findViewById(R.id.textViewSkip)
        preferencesPrivate = this.getSharedPreferences(
            this.packageName + "_private_preferences",
            Context.MODE_PRIVATE
        )
        if (isFirstRun()) {
            // show app intro
            val i = Intent(this, MainIntroActivity::class.java)
            startActivity(i)
            consumeFirstRun()
        }
        if (!haveNetworkConnection()){
            startActivity((Intent(this, MainActivity::class.java)))
            finish()
        }

        // Initialize Firebase Auth
        auth = Firebase.auth


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        skip.setOnClickListener{
            startActivity((Intent(this, MainActivity::class.java)))

            finish()
        }
        btnGoogleSignIn.setOnClickListener{
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }


    }
    override fun onStart() {
        super.onStart()


        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            Log.e(TAG,"User is null")
            return
        }
        startActivity((Intent(this, MainActivity::class.java)))

        finish()
    }
    private fun haveNetworkConnection():Boolean{
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm =  getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for(i in netInfo){
            if(i.typeName.equals("WIFI",true)){
                if(i.isConnected){
                    haveConnectedWifi =true
                }
            }
            if(i.typeName.equals("MOBILE",true)){
                if(i.isConnected){
                    haveConnectedMobile =true
                }
            }
        }
        return haveConnectedMobile||haveConnectedWifi
    }
    private fun isFirstRun() = preferencesPrivate.getBoolean(Constants.FIRST_RUN_OF_LOGIN, true)

    private fun consumeFirstRun() =
        preferencesPrivate.edit().putBoolean(Constants.FIRST_RUN_OF_LOGIN, false).apply()


}