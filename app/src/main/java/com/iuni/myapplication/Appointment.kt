package com.iuni.myapplication

import com.google.firebase.firestore.Exclude

class Appointment(
    dateSetAppointment: String = "",
    timeAppointment: String = "",
    caseManagerName: String = "",
    userName: String = "",
    formatAppointment: String = "",
    clientUID: String = ""
) {
    var documentId: String? = null
    private set
    var dateAppointment: String? = dateSetAppointment
    private set
    var timeAppointment: String? = timeAppointment
    private set
    var userName: String? = userName
    private set
    var caseManagerName: String? = caseManagerName
    private set
    var formatAppointment: String? = formatAppointment
    private set
    var clientUID: String? = clientUID
    private set

    @Exclude
    fun setId(documentId: String) {
        this.documentId = documentId
    }

    @Exclude
    fun getId(): String? {
        return documentId
    }
}
