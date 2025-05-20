package com.example.projectforuni

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val from:String,
    val to:String,
    val amount: Double,
    val timestamp: Long
)
