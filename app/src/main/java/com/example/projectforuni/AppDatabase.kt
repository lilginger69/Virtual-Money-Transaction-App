package com.example.projectforuni

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [UserAccountInfo::class, BankAccountInfo::class, Transaction::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao

    companion object{
        @Volatile private var INSTANCE: AppDatabase?=null
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?:synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bank_app_db"
                ).build().also{INSTANCE=it}
            }
        }
    }
}