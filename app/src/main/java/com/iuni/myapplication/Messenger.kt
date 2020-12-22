package com.iuni.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log_with_case_manager.*
import kotlinx.android.synthetic.main.activity_messenger.*
import kotlinx.android.synthetic.main.chat_from_case_manager.*
import kotlinx.android.synthetic.main.chat_from_case_manager.view.*
import kotlinx.android.synthetic.main.create_appointment.*
import kotlinx.android.synthetic.main.new_message_row.view.*


class Messenger : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        typeOfUser()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun typeOfUser() {
        val userUID = FirebaseAuth.getInstance().currentUser!!.uid
        val checkRef = FirebaseDatabase.getInstance().reference
        checkRef.child("users").child(userUID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Log.d("Messenger", (dataSnapshot.value.toString() == "null").toString())
//
                    if (dataSnapshot.value.toString() == "null") {
                        var currentTypeOfUser = "users"
                        fetchUsers(currentTypeOfUser)
                    } else {
                        var currentTypeOfUser = "case_managers"
                        fetchUsers(currentTypeOfUser)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun fetchUsers(userType: String) {
//        Log.d("Messenger", currentTypeOfUser)
        val ref = FirebaseDatabase.getInstance().getReference("/$userType")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogWithCaseManagerActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                }
                recyclerview_messenger_messages.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        val userUID = FirebaseAuth.getInstance().currentUser!!.uid
        val checkRef = FirebaseDatabase.getInstance().reference

        val imageURL = checkRef.child(userUID).child("image_url")
        val targetImageView = viewHolder.itemView.image_messenger

        viewHolder.itemView.case_manager_name.text = user.full_name.substringBeforeLast("@").capitalize()
//        viewHolder.itemView.case_manager_name_messenger.text = user.full_name
    }

    override fun getLayout(): Int {
        return R.layout.new_message_row
    }
}
