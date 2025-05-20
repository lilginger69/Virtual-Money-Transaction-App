package com.example.projectforuni

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.credentials.provider.Action
import androidx.room.util.TableInfo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@Composable
fun TransferMoneyScreen(){
    val context= LocalContext.current
    val userId= FirebaseAuth.getInstance().currentUser?.uid?:return
    var receiverIBAN by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searchedIBAN by remember { mutableStateOf<String?>(null) }
    val configuration= LocalConfiguration.current

    LaunchedEffect(message){
        if(message!=null){
            delay(5000)
            message=null
        }
    }

    if(showConfirmDialog){
        AlertDialog(
            onDismissRequest = {showConfirmDialog=false},
            confirmButton = {
                TextButton(
                    onClick = {
                        if (receiverIBAN.isBlank() || amount.isBlank()) {
                            message = "Please fill all the fields"
                            showConfirmDialog = false
                            return@TextButton
                        }
                        showConfirmDialog=false
                        CoroutineScope(Dispatchers.IO).launch{
                            val db= AppDatabase.getInstance(context).userDao()
                            val sender=db.getBankAccountByUserID(userId)
                            val receiver=db.getBankAccountByIban(receiverIBAN)
                            val amountDouble=amount.toDoubleOrNull()?:0.0
                            if(sender==null || receiver==null || amountDouble<=0.0){
                                message="Invalid data"
                                return@launch
                            }
                            if(sender.balance<amountDouble){
                                message="Insufficient funds"
                                return@launch
                            }
                            db.updateBalance(sender.userID,sender.balance-amountDouble)
                            db.updateBalance(receiver.userID,receiver.balance+amountDouble)
                            Firebase.firestore.collection("users").document(sender.userID).update("balance",sender.balance-amountDouble)
                            Firebase.firestore.collection("users").document(receiver.userID).update("balance",receiver.balance+amountDouble)
                            Firebase.firestore.collection("users").document(sender.userID).collection("transactions").add(
                                mapOf(
                                    "from" to sender.IBAN,
                                    "to" to receiver.IBAN,
                                    "amount" to amountDouble,
                                    "timestamp" to SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))
                                )
                            )
                            Firebase.firestore.collection("users").document(receiver.userID).collection("transactions").add(
                                mapOf(
                                    "from" to sender.IBAN,
                                    "to" to receiver.IBAN,
                                    "amount" to amountDouble,
                                    "timestamp" to SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))
                                )
                            )
                            db.insertTransaction(
                                Transaction(
                                    from=sender.IBAN,
                                    to=receiver.IBAN,
                                    amount=amountDouble,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            withContext(Dispatchers.Main){
                                showNotification(context=context,title="Transfer Successfull!",message="You sent $amountDouble to ${receiver.IBAN}")
                                message="Transfer Successfull"
                                amount=""
                                receiverIBAN=""
                                searchedIBAN=null
                            }
                        }
                    }
                ){
                    Text("Confirm")
                }
            },
            dismissButton={
                TextButton(onClick = {showConfirmDialog=false}){
                    Text("Cancel")
                }
            },
            title={Text("Confirm Transfer")},
            text={Text("Are you sure you want to send €$amount to $receiverIBAN?")}
        )
    }
    if(configuration.orientation== Configuration.ORIENTATION_PORTRAIT){
        Column(
            modifier=Modifier.fillMaxSize().padding(24.dp)
        ){
            Text("Transfer Money",style=MaterialTheme.typography.titleLarge)
            Spacer(modifier=Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {searchQuery=it},
                label={Text("Type phone or email")},
                modifier=Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch{
                        if(searchQuery.isBlank()){
                            withContext(Dispatchers.Main){message="Fill the field"}
                            return@launch
                        }

                        val db= AppDatabase.getInstance(context).userDao()
                        val currentUser = db.getUserByUserId(userId)
                        if (searchQuery == currentUser?.email || searchQuery == currentUser?.phone) {
                            withContext(Dispatchers.Main) {
                                message = "This is your own account"
                            }
                            return@launch
                        }

                        val userByPhone=db.getUserByPhone(searchQuery)
                        val userByEmail=db.getUserByEmail(searchQuery)
                        val foundUser=userByPhone?:userByEmail
                        if(foundUser!=null){
                            val bankAccount=db.getBankAccountByUserID(foundUser.uid)
                            searchedIBAN=bankAccount?.IBAN
                            withContext(Dispatchers.Main){
                                if(searchedIBAN!=null){
                                    message="IBAN found"
                                    receiverIBAN=searchedIBAN!!
                                }else{
                                    message="IBAN not found"
                                }
                            }
                        }else{
                            withContext(Dispatchers.Main){message="No user found with given phone or email"}
                        }
                    }
                },
                modifier=Modifier.fillMaxWidth().padding(top=8.dp)
            ){
                Text("Search IBAN")
            }

            if(searchedIBAN!=null){
                OutlinedTextField(
                    value=receiverIBAN,
                    onValueChange = {receiverIBAN=it},
                    label={Text("Recipient IBAN")},
                    modifier=Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value=amount,
                    onValueChange = {amount=it},
                    label={Text("Amount")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier=Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {showConfirmDialog=true},
                    modifier=Modifier.fillMaxWidth().padding(top=16.dp)
                ){
                    Text("Send Money")
                }
            }
            message?.let{
                Spacer(modifier=Modifier.height(16.dp))
                Text(text=it?:"",color=MaterialTheme.colorScheme.error)
            }
        }
    }else if(configuration.orientation==Configuration.ORIENTATION_LANDSCAPE){
        Row(
            modifier=Modifier.fillMaxSize().padding(24.dp)
        ){
            Column(modifier=Modifier.weight(1f)){
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {searchQuery=it},
                    label={Text("Type phone or email")},
                    modifier=Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch{
                            if(searchQuery.isBlank()){
                                withContext(Dispatchers.Main){message="Fill the field"}
                                return@launch
                            }

                            val db= AppDatabase.getInstance(context).userDao()
                            val currentUser = db.getUserByUserId(userId)
                            if (searchQuery == currentUser?.email || searchQuery == currentUser?.phone) {
                                withContext(Dispatchers.Main) {
                                    message = "This is your own account"
                                }
                                return@launch
                            }

                            val userByPhone=db.getUserByPhone(searchQuery)
                            val userByEmail=db.getUserByEmail(searchQuery)
                            val foundUser=userByPhone?:userByEmail
                            if(foundUser!=null){
                                val bankAccount=db.getBankAccountByUserID(foundUser.uid)
                                searchedIBAN=bankAccount?.IBAN
                                withContext(Dispatchers.Main){
                                    if(searchedIBAN!=null){
                                        message="IBAN found"
                                        receiverIBAN=searchedIBAN!!
                                    }else{
                                        message="IBAN not found"
                                    }
                                }
                            }else{
                                withContext(Dispatchers.Main){message="No user found with given phone or email"}
                            }
                        }
                    },
                    modifier=Modifier.fillMaxWidth().padding(top=8.dp)
                ){
                    Text("Search IBAN")
                }
            }
            Spacer(modifier=Modifier.width(16.dp))
            Column(modifier=Modifier.weight(1f)){
                if(searchedIBAN!=null){
                    OutlinedTextField(
                        value=receiverIBAN,
                        onValueChange = {receiverIBAN=it},
                        label={Text("Recipient IBAN")},
                        modifier=Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value=amount,
                        onValueChange = {amount=it},
                        label={Text("Amount")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier=Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {showConfirmDialog=true},
                        modifier=Modifier.fillMaxWidth().padding(top=16.dp)
                    ){
                        Text("Send Money")
                    }
                }
                message?.let{
                    Spacer(modifier=Modifier.height(16.dp))
                    Text(text=it?:"",color=MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

fun showNotification(context: Context,title:String,message:String){
    val channelId="transfer_channel"
    val channelName="Transfer Notification"
    val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        val channel= NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
        if(ContextCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
            if(context is Activity){
                ActivityCompat.requestPermissions(context,arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),1001)
            }
            return
        }
    }
    val notification= NotificationCompat.Builder(context,channelId).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.notifications_24dp).setAutoCancel(true).build()
    notificationManager.notify(Random.nextInt(),notification)
}