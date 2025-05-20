package com.example.projectforuni

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccountInfo(
    @PrimaryKey val uid: String,
    val name: String,
    val email:String,
    val phone:String
)
