package com.iuni.myapplication

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val email: String): Parcelable {
    constructor(): this("", "")
}