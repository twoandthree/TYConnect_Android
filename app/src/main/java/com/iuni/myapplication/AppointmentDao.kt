package com.iuni.myapplication

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AppointmentDao {

    @Insert
    fun insert(note: Note?)

    @Update
    fun update(note: Note?)

    @Delete
    fun delete(note: Note?)

    @Query("DELETE FROM appointment_table")
    fun deleteAllNotes()

    @Query("SELECT * FROM appointment_table ORDER BY priority DESC")
    fun getAllNotes(): LiveData<List<Note?>?>?
}