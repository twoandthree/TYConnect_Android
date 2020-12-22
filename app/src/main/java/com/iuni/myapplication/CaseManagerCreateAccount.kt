package com.iuni.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_case_manager_create_account.*
import kotlinx.android.synthetic.main.case_manager_firstlast_name.*
import kotlinx.android.synthetic.main.user_about_me.done_button_image

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

            if (verification_code.text.toString() == "0t1y1c235") {
                userAboutMe(email, password)
            }

        }

       already_have_an_account_case_manager.setOnClickListener {
           val intent = Intent(this, CaseManagerLogin::class.java)
           startActivity(intent) 
       }
    }

    private fun userAboutMe(email: String, password: String) {
        setContentView(R.layout.case_manager_firstlast_name)

        done_button_image.setOnClickListener {
            val case_manager_name = cm_name_edittext_create_account_page.text.toString().capitalize()
            val case_manager_last_name = cm_lastname_edittext_create_account_page.text.toString().capitalize()

            if (case_manager_name.isEmpty() || case_manager_last_name.isEmpty()) {
                Toast.makeText(this, "Please complete the fields name/last name.", Toast.LENGTH_LONG).show()
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d("CreateAccountActivity", "Created user with uid: ${it.result?.user?.uid}")

                    saveUserToFirebaseDatabase(case_manager_name, case_manager_last_name)

                    val intent = Intent(this, MenuMain::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
        }
    }

    private fun saveUserToFirebaseDatabase(
        case_manager_name: String,
        case_manager_last_name: String,
    ) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/case_managers/$uid")

        val caseManager = CaseManager(uid, email_edittext_case_manager_create_account_page.text.toString())

        ref.setValue(caseManager)
            .addOnSuccessListener {
                Log.d("CreteAccountActivity", "Saved user to Firebase Database.")
            }
        updateUser(case_manager_name, case_manager_last_name)
    }
}

private fun updateUser(name: String, last_name: String) {
    val updateUser = FirebaseAuth.getInstance().currentUser?.uid
    val refForUpdate = FirebaseDatabase.getInstance().reference

    val full_name = "$name $last_name"

    if (updateUser != null) {
        refForUpdate.child("/case_managers").child(updateUser).child("full_name").setValue(full_name)
        refForUpdate.child("/case_managers").child(updateUser).child("user_type").setValue("case_manager")
    }
}

class CaseManager(val uid: String, val email: String)