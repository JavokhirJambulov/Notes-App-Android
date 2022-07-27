package uz.javokhirjambulov.notes.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

  /*  private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var btnLogin:Button
    private lateinit var btnSignUp:Button*/
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
//        email= findViewById(R.id.email)
//        password=findViewById(R.id.password)
//        btnLogin=findViewById(R.id.btnLogin)
//        btnSignUp=findViewById(R.id.btnSignUp)
        btnGoogleSignIn=findViewById(R.id.btnGoogleSingIn)
        skip = findViewById(R.id.textViewSkip)
        preferencesPrivate = this.getSharedPreferences(
            this.packageName + "_private_preferences",
            Context.MODE_PRIVATE
        )




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


//            btnSignUp.setOnClickListener{
//            val intent = Intent(this, SignUp::class.java)
//            startActivity(intent)
//        }
//        btnLogin.setOnClickListener{
//            when {
//                TextUtils.isEmpty(email.text.toString()) -> {
//                    email.error = "Please, enter your email!"
//                }
//                TextUtils.isEmpty(password.text.toString()) -> {
//                    password.error="Please, enter your password!"
//                }
//                else -> {
//                    val email = email.text.toString()
//                    val password=password.text.toString()
//                    login(email, password)
//                }
//            }
//        }


    }
    override fun onStart() {
        super.onStart()
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
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
/*    private fun openActivityForResult(intent1: Intent){
        startForResult.launch(intent1)
    }
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result:ActivityResult->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
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
    }*/
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

//    private fun login(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    updateUI(user)
//            }
//            else{
//                // If sign in fails, display a message to the user.
//                Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                Toast.makeText(
//                    baseContext, "Authentication failed. User does not exist",
//                    Toast.LENGTH_SHORT
//                ).show()
//                 updateUI(null)
//
//            }
//        }
//
//    }


}