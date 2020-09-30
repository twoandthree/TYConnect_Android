package com.iuni.myapplication

import android.os.Bundle
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        true_login_button.setOnClickListener {
            val emailAtLogin = email_edittext_login.text.toString()
            val passwordAtLogin = password_edittext_login.text.toString()

            if (emailAtLogin.isEmpty() || passwordAtLogin.isEmpty()) return@setOnClickListener

            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailAtLogin, passwordAtLogin)
                .addOnCompleteListener {
                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    val checkRef = FirebaseDatabase.getInstance().reference
                    val mainMenuIntent = Intent(this, MenuMain::class.java)
                    val caseManagerCreateAccountIntent = Intent(this, CaseManagerCreateAccount::class.java)

                    checkRef.child("users").child(uid).child("email")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                when {
                                    dataSnapshot.value.toString() == "null" -> startActivity(caseManagerCreateAccountIntent)
                                    dataSnapshot.value.toString() == emailAtLogin -> startActivity(mainMenuIntent)
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {}
                        })

                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("LoginActivity", "User has signed in.")
                }
        }

        create_an_account.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}