package com.iuni.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_menu_main.*
import org.w3c.dom.Text

class MenuMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        verifyUserIsLoggedIn()
        performLogOut()
        sendMessage()
        setAppointmentUser()
        displayUserName()
    }

    private fun displayUserName() {
        var user = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("users").child(user).child("full_name").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                user_name_main_menu.text = dataSnapshot.value.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
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
        signout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun sendMessage() {
        messages_main_menu.setOnClickListener {
            val intent = Intent(this, Messenger::class.java)
            startActivity(intent)
        }
    }

    private fun setAppointmentUser() {
        appointments_main_menu.setOnClickListener {
//            val intent = Intent(this, SetAppointment::class.java)
//            startActivity(intent)
        }
    }
}
