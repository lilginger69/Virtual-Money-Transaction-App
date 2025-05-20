package com.example.projectforuni

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Int
    @Query("SELECT * FROM users WHERE users.name=:name")
    suspend fun getUserByUsername(name:String): UserAccountInfo?
    @Query("SELECT * FROM users WHERE users.email=:email")
    suspend fun getUserByEmail(email:String): UserAccountInfo
    @Query("SELECT * FROM users WHERE users.phone=:phone")
    suspend fun getUserByPhone(phone:String): UserAccountInfo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccountInfo(user: UserAccountInfo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccountInfo(bankAccount: BankAccountInfo)
    @Query("SELECT * FROM bank_accounts WHERE bank_accounts.userID=:userID LIMIT 1")
    suspend fun getIbanByUserID(userID:String): BankAccountInfo?
    @Query("SELECT * FROM bank_accounts where userID=:userID")
    suspend fun getBankAccountByUserID(userID:String): BankAccountInfo?
    @Query("SELECT * FROM bank_accounts WHERE IBAN=:iban")
    suspend fun getBankAccountByIban(iban:String): BankAccountInfo?
    @Query("UPDATE bank_accounts SET balance=:newBalance WHERE userID=:userID")
    suspend fun updateBalance(userID:String,newBalance:Double)
    @Insert
    suspend fun insertTransaction(transaction: Transaction)
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<Transaction>
    @Query("UPDATE users SET name=:name,email=:email,phone=:phone WHERE uid=:uid")
    suspend fun updateUserAccountInfo(uid:String,name:String,email:String,phone:String)
    @Update
    suspend fun updateUser(user: UserAccountInfo)
    @Query("DELETE FROM users WHERE uid=:userId")
    suspend fun deleteUser(userId:String)
    @Query("DELETE FROM bank_accounts WHERE userID=:userID")
    suspend fun deleteBankAccount(userID:String)
    @Query("SELECT * FROM users WHERE uid=:userId LIMIT 1")
    suspend fun getUserByUserId(userId:String): UserAccountInfo?

    //3 Queries
    @Query("SELECT * FROM transactions WHERE `from` = :iban OR `to` = :iban ORDER BY timestamp DESC LIMIT 3")//ποιές είναι οι 3 τελευταίες συναλλαγές που πραγματοποιήθηκαν
    suspend fun getTransactionsForIban(iban: String): List<Transaction>
    @Query("SELECT sum(amount) from transactions WHERE `to`= :iban")//ποίο είναι το συνολικό ποσό που έχει λάβει ο χρήστης
    suspend fun getTotalReceivedAmount(iban:String): Double
    @Query("SELECT COUNT(*) FROM transactions WHERE `from`= :iban")//πόσες συναλλαγές έκανε ο χρήστης
    suspend fun getSentTransactionCount(iban: String):Int
}