package com.iuni.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_menu_main.*
import java.util.*

class MenuMain : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        verifyUserIsLoggedIn()
        performLogOut()
        sendMessage()
        viewAppointments()
        tyConnect()
//        sendMessageNotifications()

//        selectPhoto()
//        displayUserName()

    }

//    private fun displayUserName() {
//        when {
//            cm_name_edittext_create_account_page?.text != null -> current_user_name.text = cm_name_edittext_create_account_page.text.toString()
//            name_edittext_create_account_page?.text != null -> current_user_name.text = name_edittext_create_account_page.text.toString()
//        }
//    }

//    private fun selectPhoto() {
//        upload_image.setOnClickListener {
//            Log.d("ZXC", "Select Photo has been pressed!")
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent, 0)
//        }
//    }
//
//    var selectedPhotoUri: Uri? = null
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
//            Log.d("ZXC", "Photo was selected!")
//            selectedPhotoUri = data.data
//
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//
////            profile_image.setImageBitmap(bitmap)
//
//            upload_image.alpha = 0f
////            val bitmapDrawable = BitmapDrawable(bitmap)
////            select_photo_button.setBackgroundDrawable(bitmapDrawable)
//
//            uploadImageToFirebaseStorage()
//        }
//    }
//
//    private fun uploadImageToFirebaseStorage() {
//        if (selectedPhotoUri == null) return
//        val filename = UUID.randomUUID().toString()
//        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
//
//        ref.putFile(selectedPhotoUri!!)
//            .addOnSuccessListener { it ->
//                Log.d("ZXC", "Uploaded image: ${it.metadata?.path}")
//
//                ref.downloadUrl.addOnSuccessListener {
//                    it.toString()
//                    Log.d("ZXC", "File location: $it")
//
//                    saveUserToFirebaseDatabase(it.toString())
//                }
//            }
//    }
//
//    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid
//        val ref = FirebaseDatabase.getInstance().reference
//
//        ref.child("/case_managers").child("$uid")
//            .child("image_url").setValue(profileImageUrl)
//
//        ref.child("/users").child("$uid")
//             .child("image_url").setValue(profileImageUrl)
//
//        //check if user is case manager or client
//        //perhaps implement a class that keeps track of the type of user
//
//    }

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

private fun viewAppointments() {
    appointments_main_menu.setOnClickListener {
        val intent = Intent(this, ViewAppointments::class.java)
        startActivity(intent)
    }
}

private fun tyConnect() {
    connect_main_menu.setOnClickListener {
        val intent = Intent(this, Connect::class.java)
        startActivity(intent)
    }
}
}
