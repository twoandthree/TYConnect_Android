package com.iuni.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.get
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log_with_case_manager.*
import kotlinx.android.synthetic.main.activity_messenger.*
import kotlinx.android.synthetic.main.chat_from_case_manager.view.*
import kotlinx.android.synthetic.main.chat_to_case_manager.view.*


class ChatLogWithCaseManagerActivity : AppCompatActivity() {
//    private val CHANNEL_ID = "channel_id_example_01"
//    private val notificationId = 101

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null
    var checkRef: DatabaseReference? = null
    var userUID: String? = null

    var readDataUserType: String? = null

//    private val CHANNEL_ID = "channel_id_example_01"
//    private val notificationId = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log_with_case_manager)

        recyclerview_chatlog_casemanagers.adapter = adapter

        toUser = intent.getParcelableExtra(Messenger.USER_KEY)

        checkRef = FirebaseDatabase.getInstance().reference
        userUID = FirebaseAuth.getInstance().currentUser?.uid

//        createNotificationChannel()

        readData(object : MyCallback {
            override fun onCallback(value: String) {
                readDataUserType = value
            }
        })

        if (userUID != null) {
            checkRef!!.child("case_managers").child(userUID!!).child("email")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }

        listenForMessages()

        user_send_button.setOnClickListener {
            performSendMessage()
        }
    }

    private fun readData(MyCallBack: MyCallback) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("case_managers").child(uid)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {
                    if (item.value.toString() == "case_manager") {
                        MyCallBack.onCallback(item.value.toString())
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)
    }

    interface MyCallback {
        fun onCallback(value: String)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("ZXC", "onOptionsItemSelected $readDataUserType")
        if (readDataUserType == "case_manager") {
            when (item.itemId) {
                R.id.create_appointment -> {
                    val intent = Intent(this, SetAppointment::class.java)
                    val userFullName = toUser?.full_name.toString()
                    val clientUID = toUser?.uid.toString()
                    intent.putExtra("userFullName", userFullName)
                    intent.putExtra("clientUID", clientUID)
                    startActivity(intent)
                }
            }
        }

        if (readDataUserType != "case_manager") {
            when (item.itemId) {
                R.id.create_appointment -> {
                    val intent = Intent(this, ViewAppointments::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nva_menu, menu)
        supportActionBar?.title = toUser?.full_name

        return super.onCreateOptionsMenu(menu)
    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Notification Title"
//            val descriptionText = "Notification Description"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    private fun sendNotification() {
//        val intent = Intent(this, Messenger::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.tyc_logo_notif)
//            .setContentTitle("You have a new message!")
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        with(NotificationManagerCompat.from((this))) {
//            notify(notificationId, builder.build())
//        }
//    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text))
//                        sendNotification()
                    } else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }
                recyclerview_chatlog_casemanagers.scrollToPosition(adapter.itemCount - 1)
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
        Log.d("ChatLogWithCaseManager", "Message was sent")
        val text = user_input_message.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(Messenger.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return

        val reference =
            FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId").push()

        val chatMessage = toId?.let {
            ChatMessage(
                reference.key!!, text, fromId,
                it, System.currentTimeMillis() / 1000
            )
        }
        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d("ChatLogWithCaseManager", "Saved our chat message: ${reference.key}")
            user_input_message.text.clear()
            recyclerview_chatlog_casemanagers.scrollToPosition(adapter.itemCount - 1)
        }
        toReference.setValue(chatMessage)

    }
}

class ChatFromItem(val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_from_case_manager.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_case_manager
    }
}

class ChatToItem(val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_to_case_manager.text = text
//        val uid = FirebaseAuth.getInstance().currentUser?.uid
//        val ref = FirebaseDatabase.getInstance().reference
//
//        val imageURI = ref.child("/case_managers").child("$uid")
//            .child("image_url").child("image_url").toString()
//        val targetImageView = viewHolder.itemView.imageview_chat_to_row
//        Picasso.get().load(imageURI).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_case_manager
    }
}

class ChatMessage(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timeStamp: Long
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        -1
    )
}