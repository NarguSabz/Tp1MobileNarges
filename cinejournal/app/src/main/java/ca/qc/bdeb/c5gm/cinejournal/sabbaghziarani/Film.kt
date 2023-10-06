package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Film(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    val image:Int,
    val titre: String,
    val anneParution: String,
    val slogan: String,
    val nombreEtoile: Int
)
