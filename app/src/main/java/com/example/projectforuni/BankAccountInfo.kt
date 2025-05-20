package com.example.projectforuni

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "bank_accounts",
    foreignKeys = [ForeignKey(
        entity = UserAccountInfo::class,
        parentColumns = ["uid"],
        childColumns = ["userID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userID"], unique = true)]
)
data class BankAccountInfo(
    @PrimaryKey val IBAN: String,
    val userID:String,
    val balance:Double
)
