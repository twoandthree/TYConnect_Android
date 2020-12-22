package com.iuni.myapplication

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_connect.*

class Connect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        var data = ""
        var tydCredentials = ""

        data += """
        Jobs, internships, and scholarships are plentiful,
        but it is up to you to pursuit them.
        
        Our case managers are passionate
        about seeing Tucson youth thrive,
        and they will help you evey step of the way.
        
        You have a direct connection to our case managers.
        Feel free to message any of them.

        """.trimIndent()

        tydCredentials += """
        Tucson Youth Development
        1901 N Stone Ave, 85705
        520-623-5843

        """.trimIndent()

        mission_textview_cont.text = data
        tyd_credentials.text = tydCredentials
    }

}
