package com.iuni.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_case_manager_menu_main.*
import kotlinx.android.synthetic.main.activity_menu_main.*

class CaseManagerMenuMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_manager_menu_main)
        verifyUserIsLoggedIn()
        performLogOut()
        sendMessage()
        setAppointment()
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun performLogOut() {
        signout_cm.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, CaseManagerLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun sendMessage() {
        messages_main_menu_cm.setOnClickListener {
            val intent = Intent(this, Messenger::class.java)
            startActivity(intent)
        }
    }

    private fun setAppointment() {
        appointments_main_menu_cm.setOnClickListener {
            val intent = Intent(this, ViewAppointments::class.java)
            startActivity(intent)
        }
    }
}
