package com.iuni.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log_with_case_manager.*
import kotlinx.android.synthetic.main.chat_from_case_manager.view.*
import kotlinx.android.synthetic.main.chat_to_case_manager.view.*

class ChatLogWithCaseManagerActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log_with_case_manager)

        supportActionBar?.title = "Chat Log"

        recyclerview_chatlog_casemanagers.adapter = adapter

        toUser = intent.getParcelableExtra<User>(Messenger.USER_KEY)

        listenForMessages()

        user_send_button.setOnClickListener {
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text))
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {
        val text = user_input_message.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(Messenger.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d("ChatLogWithCaseManager", "Saved our chat message: ${reference.key}")
            user_input_message.text.clear()
            recyclerview_chatlog_casemanagers.scrollToPosition(adapter.itemCount - 1)
        }
        toReference.setValue(chatMessage)

    }
}


class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_from_case_manager.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_case_manager
    }
}

class ChatToItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_to_case_manager.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_case_manager
    }
}

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timeStamp: Long) {
    constructor() : this(
        "",
        "",
        "",
        "",
        -1
    )
}