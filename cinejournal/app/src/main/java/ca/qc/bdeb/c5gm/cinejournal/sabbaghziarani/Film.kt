package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Film(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    val image: String,
    val titre: String,
    val anneParution: String,
    val slogan: String,
    val nombreEtoile: Int
) : Parcelable {}
