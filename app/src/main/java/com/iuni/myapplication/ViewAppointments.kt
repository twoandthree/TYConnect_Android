 package com.iuni.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view_appointments.*

 class ViewAppointments : AppCompatActivity() {
     var readAppointment: String? = null

     var caseManager: String? = null
     var date: String? = null
     var time: String? = null
     var format: String? = null

     private var db = FirebaseFirestore.getInstance()

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_view_appointments)


         readData(object : AppointmentCallback {
             override fun onCallback(value: Appointment?) {
                 var data = ""
                 //Here readAppointment is of type DataSnapshot
                 if (value != null) {
                     caseManager = value.caseManagerName
                     date = value.dateAppointment
                     time = value.timeAppointment
                     format = value.formatAppointment

                     data += """
                  Appointment with:
                    $caseManager
                    
                  $date
                  $time
                  
                  The format of this appointment is:
                   $format
                  """.trimIndent()
                 }
                 text_view_data.text = data
             }
         })

         readCaseManager(object : CaseManagerNameCallBack {
             override fun onCaseManagerCallback(value: String?) {
                 Log.d(
                     "ViewAppointments",
                     "case manager name: $value"
                 )
                 db.collection("$value").get()
                     .addOnSuccessListener { result ->
                         var data = ""
                         for (documentSnapshot in result) {
                             Log.d(
                                 "ViewAppointments",
                                 "documentSnapshot: $documentSnapshot"
                             )
                             val note = documentSnapshot.toObject(
                                 Appointment::class.java
                             )
                             note.setId(documentSnapshot.id)
                             val documentId = note.documentId
                             val date = note.dateAppointment
                             val time = note.timeAppointment
                             val client_name = note.userName
                             val format = note.formatAppointment
                             data += """
                $client_name
                Date: $date 
                Time: $time
                Format: $format
                

                """.trimIndent()
                         }
//                         data += "___________\n\n"
                         text_view_data!!.append(data)
                     }
                     .addOnFailureListener { exception ->
                         Log.d("ViewAppointments", "Error getting documents: ", exception)
                     }
             }
         })
     }

     private fun readData(appointmentCallback: AppointmentCallback) {
         val uid = FirebaseAuth.getInstance().currentUser!!.uid
         val rootRef = FirebaseDatabase.getInstance().reference

         val uidRef = rootRef.child("users").child(uid)
         val valueEventListener = object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 for (item in dataSnapshot.children) {
                     if (item.key.toString() == "appointment") {
                         val appointment = item.getValue(Appointment::class.java)
                         appointmentCallback.onCallback(appointment)
                     }
                 }
             }
             override fun onCancelled(databaseError: DatabaseError) {
             }
         }
         uidRef.addListenerForSingleValueEvent(valueEventListener)
     }

     interface AppointmentCallback {
         fun onCallback(value: Appointment?)
     }

     interface CaseManagerNameCallBack {
         fun onCaseManagerCallback(value: String?)
     }

     private fun readCaseManager(caseManagerNameCallBack: CaseManagerNameCallBack) {
         val uid = FirebaseAuth.getInstance().currentUser!!.uid
         val rootRef = FirebaseDatabase.getInstance().reference

         val uidRef = rootRef.child("case_managers").child(uid)
         val valueEventListener = object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 for (item in dataSnapshot.children) {
                     if (item.key.toString() == "full_name") {
                         val caseManagerName = item.value.toString()
                         caseManagerNameCallBack.onCaseManagerCallback(caseManagerName)
                     }
                 }
             }
             override fun onCancelled(databaseError: DatabaseError) {
             }
         }
         uidRef.addListenerForSingleValueEvent(valueEventListener)
     }
 }

