package com.pv.scaralina.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "termini")
data class TermEntity(
    @PrimaryKey
    val parola: String,
    val definizione: String? = null
)
