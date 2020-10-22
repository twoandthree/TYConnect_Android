package com.codinginflow.architectureexample

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iuni.myapplication.Appointment

@Database(entities = [Appointment::class], version = 1)
abstract class AppointmentDataBase : RoomDatabase() {

    companion object {
        private var instance: AppointmentDataBase? = null
        @Synchronized
        fun getInstance(context: Context): AppointmentDataBase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppointmentDataBase::class.java, "appointment_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}