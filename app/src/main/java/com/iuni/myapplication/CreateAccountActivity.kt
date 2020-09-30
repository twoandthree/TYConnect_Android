package com.iuni.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.user_about_me.*

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        performRegister()

    }

    private fun performRegister() {
        create_account_true_button.setOnClickListener {
            val email = email_edittext_create_account_page.text.toString()
            val password = password_edittext_create_account_page.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Pleas enter valid email/password.", Toast.LENGTH_LONG).show()
            }
            userAboutMe(email, password)
        }

        already_have_an_account.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        case_manager_create_account_page.setOnClickListener {
            val intent = Intent(this, CaseManagerCreateAccount::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun userAboutMe(email: String, password: String) {
        setContentView(R.layout.user_about_me)

        done_button_image.setOnClickListener {
            val name = name_edittext_create_account_page.text.toString().capitalize()
            val last_name = lastname_edittext_create_account_page.text.toString().capitalize()
            val aboutme = aboutme_edittext.text.toString()

            if (name.isEmpty() || last_name.isEmpty()) {
                Toast.makeText(this, "Please complete the fields name/last name.", Toast.LENGTH_LONG).show()
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d("CreateAccountActivity", "Created user with uid: ${it.result?.user?.uid}")

                    saveUserToFirebaseDatabase(name, last_name, aboutme)

                    val intent = Intent(this, MenuMain::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
        }
    }

    private fun saveUserToFirebaseDatabase(name: String, last_name: String, aboutme: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, email_edittext_create_account_page.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("CreteAccountActivity", "Saved user to Firebase Database.")
            }

        updateUser(name, last_name, aboutme)
    }

    private fun updateUser(name: String, last_name: String, aboutme: String) {
        val updateUser = FirebaseAuth.getInstance().currentUser?.uid
        val refForUpdate = FirebaseDatabase.getInstance().reference

        val full_name = "$name $last_name"

        if (updateUser != null) {
            refForUpdate.child("/users").child(updateUser).child("full_name").setValue(full_name)
            refForUpdate.child("/users").child(updateUser).child("about_me").setValue(aboutme)
        }

    }
}
