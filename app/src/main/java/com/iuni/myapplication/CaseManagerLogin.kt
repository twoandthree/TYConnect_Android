package com.iuni.myapplication


import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_case_manager_login.*
import kotlinx.android.synthetic.main.activity_login.*

class CaseManagerLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_manager_login)

        login_true_button_case_manager.setOnClickListener {
            val email_at_login_case_manager = email_edittext_case_manager_login_page.text.toString()
            val password_at_login_case_manager = password_edittext_case_manager_login_page.text.toString()

            if (email_at_login_case_manager.isEmpty() || password_at_login_case_manager.isEmpty()) return@setOnClickListener

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email_at_login_case_manager, password_at_login_case_manager)
                .addOnCompleteListener {
                    val uid = FirebaseAuth.getInstance().uid ?: ""

                    val checkRef = FirebaseDatabase.getInstance().reference
                    val caseManagerMenuMainIntent = Intent(this, CaseManagerMenuMain::class.java)
                    val createAccountIntent = Intent(this, CreateAccountActivity::class.java)

                    checkRef.child("case_managers").child(uid).child("email")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                when {
                                    dataSnapshot.value.toString() == "null" -> startActivity(createAccountIntent)
                                    dataSnapshot.value.toString() == email_at_login_case_manager -> startActivity(caseManagerMenuMainIntent)

                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {}
                        })

                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("LoginActivity", "User has signed in.")
                }
        }

        create_account_case_manager_login.setOnClickListener {
            val intent = Intent(this, CaseManagerCreateAccount::class.java)
            startActivity(intent)
        }

    }
}