package com.iuni.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_case_manager_create_account.*

class CaseManagerCreateAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_manager_create_account)
        performRegister()

    }

    private fun performRegister() {
        create_account_true_button_case_manager.setOnClickListener {
            val email = email_edittext_case_manager_create_account_page.text.toString()
            val password = password_edittext_case_manager_create_account_page.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter valid email/password.", Toast.LENGTH_LONG).show()
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d("CreateAccountActivity", "Created user with uid: ${it.result?.user?.uid}")

                    saveUserToFirebaseDatabase()

                    val intent = Intent(this, CaseManagerMenuMain::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
        }

       already_have_an_account_case_manager.setOnClickListener {
           val intent = Intent(this, CaseManagerLogin::class.java)
           startActivity(intent)
       }
    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/case_managers/$uid")

        val caseManager = CaseManager(uid, email_edittext_case_manager_create_account_page.text.toString())

        ref.setValue(caseManager)
            .addOnSuccessListener {
                Log.d("CreteAccountActivity", "Saved user to Firebase Database.")
            }
    }
}

class CaseManager(val uid: String, val email: String)