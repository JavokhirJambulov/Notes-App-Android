package uz.javokhirjambulov.notes.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R

private const val TAG = "SignUp Activity"
class SignUp : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnSignUp: Button
    private lateinit var auth: FirebaseAuth
    companion object{
        private const val TAG = "SignInActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar?.hide()
        username= findViewById(R.id.username)
        email= findViewById(R.id.email)
        password=findViewById(R.id.password)
        btnSignUp=findViewById(R.id.btnSignUp)
        auth = Firebase.auth

        btnSignUp.setOnClickListener{
            if(TextUtils.isEmpty(email.text.toString())&&TextUtils.isEmpty(password.text.toString())) {
                email.error = "Please, enter your info"
                password.error="Please, enter your info"
                return@setOnClickListener
            }else{
                val email = email.text.toString()
                val password=password.text.toString()
                signUp(email,password)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            Log.e(TAG,"User is null")
            return
        }
        startActivity((Intent(this, MainActivity::class.java)))

        finish()

    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }

    }
}