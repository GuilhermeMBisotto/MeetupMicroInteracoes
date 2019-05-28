package com.guilhermebisotto.meetupsample.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventModel(
    val id: Int,
    val name: String,
    val city: String,
    val state: String,
    val place: String,
    val initialDate: String,
    val initialDateNumber: Int,
    val finalDate: String,
    val finalDateNumber: Int,
    val month: String,
    val image: String
) : Parcelable