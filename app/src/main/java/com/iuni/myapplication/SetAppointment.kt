package com.iuni.myapplication

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.create_appointment.*


class SetAppointment : AppCompatActivity() {
    private var dateEditText: EditText? = null
    private var timeEditText: EditText? = null
    private var caseManagerName: EditText? = null
    private var userName: EditText? = null
    private var formatEditText: EditText? = null
    private var appointmentTextView: TextView? = null
    private var clientFullName: String? = null
    private var clientUID: String? = null
    private var checkRef: DatabaseReference? = null
    private var userUID: String? = null

    private val db = FirebaseFirestore.getInstance()
//    private val notebookRef = db.collection("Appointment")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_appointment)

        clientFullName = intent.getStringExtra("userFullName")
        clientUID = intent.getStringExtra("clientUID")

        dateEditText = findViewById(R.id.set_appointment_date)
        timeEditText = findViewById(R.id.set_appointment_time)
        caseManagerName = findViewById(R.id.set_appointment_case_manager)
        userName = findViewById(R.id.set_appointment_user_name)
        formatEditText = findViewById(R.id.set_appointment_format)

        checkRef = FirebaseDatabase.getInstance().reference
        userUID = FirebaseAuth.getInstance().currentUser?.uid

        creating_appointment_for.text = "Appointment for:\n ${clientFullName}"
        set_appointment_user_name.hint = "${clientFullName.toString()}"

//        Log.d("ZXC", clientUID.toString())
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences =
            getSharedPreferences("sharedPrefCaseManagerName", Context.MODE_PRIVATE)
        val savedCaseManager = sharedPreferences.getString("STRING_KEY", null)

        Log.d("ZXC", savedCaseManager.toString())

        if(savedCaseManager != "") {
//            Log.d("ZXC", savedCaseManager.toString())
            val notebookRef = db.collection("$savedCaseManager")
            notebookRef.addSnapshotListener(this, object : EventListener<QuerySnapshot?> {
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    if (p1 != null) {
                        return
                    }
                    if (p0 != null) {
                        for (documentSnapshot in p0) {
//                            Log.d("ZXC", documentSnapshot["clientUID"].toString())
//                            Log.d("ZXC", savedUserUID.toString())
                            if (documentSnapshot["clientUID"].toString() == clientUID.toString()) {
                                val appointment = documentSnapshot.toObject(
                                    Appointment::class.java
                                )
//                                Log.d("ZXC", documentSnapshot.toString())
                                appointment.setId(documentSnapshot.id)
                                appointment_text_view_date.text =
                                    "Date: " + documentSnapshot["dateAppointment"].toString()
                                appointment_text_view_time.text =
                                    "Time: " + documentSnapshot["timeAppointment"].toString()
                                appointment_text_view_case_manager.text =
                                    "Case Manager: " + documentSnapshot["caseManagerName"].toString()
                                appointment_text_view_client_name.text =
                                    "Client: " + documentSnapshot["userName"].toString()
                                appointment_text_view_format.text =
                                    "Format: " + documentSnapshot["formatAppointment"].toString()
                            }
                        }
                    }
                }
            })
        }
    }

    fun setAppointment(v: View?) {

        val dateForAppointment = dateEditText!!.text.toString()
        val timeForAppointment = timeEditText!!.text.toString()
        val caseManager = caseManagerName!!.text.trim().replace(Regex("\\s+"), " ")
        val clientName = userName!!.text.trim().replace(Regex("\\s+"), " ")
        val formatForAppointment = formatEditText!!.text.toString()
        val clientUID = clientUID!!

        val insertedText = caseManagerName!!.text.toString()
        val sharedPreferences =
            getSharedPreferences("sharedPrefCaseManagerName", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("STRING_KEY", insertedText)
        }

        val appointment = Appointment(
            dateForAppointment,
            timeForAppointment,
            caseManager,
            clientName,
            formatForAppointment,
            clientUID
        )

        checkRef?.child("users")?.child(clientUID)?.child("appointment")?.setValue(appointment)

//        val notebookRef = db.collection("$caseManager")
        db.collection("$caseManager").document("$clientUID").set(appointment)
        viewAppointment(v)

    }

    fun viewAppointment(v: View?) {
        val caseManagerNameView = caseManagerName?.text
        val notebookRef = db.collection("$caseManagerNameView")

        notebookRef.get().addOnSuccessListener { queryDocumentSnapshots ->
            for (documentSnapshot in queryDocumentSnapshots) {
                if (documentSnapshot["userName"].toString() == clientFullName.toString()) {
//                    Log.d("ZXC", documentSnapshot["userName"].toString())
//                    Log.d("ZXC", clientFullName.toString())
                    val appointment = documentSnapshot.toObject(
                        Appointment::class.java
                    )
                    appointment.setId(documentSnapshot.id)
//                    val documentId = appointment.documentId
//                    val date = appointment.dateAppointment
//                    val time = appointment.timeAppointment
//                    val nameCaseManager = appointment.caseManagerName
//                    val clientName = appointment.userName
//                    val format = appointment.formatAppointment

                    appointment_text_view_date.text =
                        "Date: " + documentSnapshot["dateAppointment"].toString()
                    appointment_text_view_time.text =
                        "Time: " + documentSnapshot["timeAppointment"].toString()
                    appointment_text_view_case_manager.text =
                        "Case Manager: " + documentSnapshot["caseManagerName"].toString()
                    appointment_text_view_client_name.text =
                        "Client: " + documentSnapshot["userName"].toString()
                    appointment_text_view_format.text =
                        "Format: " + documentSnapshot["formatAppointment"].toString()

//                    dateEditText!!.text = documentId
//                    timeEditText!!.text = time.toString() as Editable
//                    caseManagerName!!.text = nameCaseManager.toString() as Editable
//                    userName!!.text = clientName.toString() as Editable
//                    formatEditText!!.text = format.toString() as Editable
//                    data += """
//                Appointment with:
//                $clientName
//                on $date
//                at $time
//                Format: $format
//
//                """.trimIndent()
                }
//                appointmentTextView!!.append(data)
            }
        }
    }

    fun deleteAppointment(v: View?) {
//        Log.d("ZXC", caseManagerName.toString())
        val sharedPreferences =
            getSharedPreferences("sharedPrefCaseManagerName", Context.MODE_PRIVATE)
        val savedCaseManager = sharedPreferences.getString("STRING_KEY", null)
//        Log.d("ZXC", "$savedCaseManager")

        if (savedCaseManager != null) {
            Log.d("ZXC", "Appointment was deleted!")
            db.collection(savedCaseManager).document("$clientUID").delete()
            val preferences = getSharedPreferences("sharedPrefCaseManagerName", Context.MODE_PRIVATE)
            preferences.edit().remove("STRING_KEY").commit()
        }

        appointment_text_view_date.text =
            ""
        appointment_text_view_time.text =
            ""
        appointment_text_view_case_manager.text =
            ""
        appointment_text_view_client_name.text =
            ""
        appointment_text_view_format.text =
            ""
    }
}