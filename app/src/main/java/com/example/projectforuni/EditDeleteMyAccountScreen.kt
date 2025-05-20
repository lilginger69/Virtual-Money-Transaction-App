package com.example.projectforuni

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun EditDeleteMyAccountScreen(navControllerRoot: NavHostController){
    var selectedTab by remember{ mutableStateOf(0) }
    val tabs=listOf("Edit Account","Delete Account")
    val userId=FirebaseAuth.getInstance().currentUser?.uid ?: return
    val configuration= LocalConfiguration.current

    if(configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
        Column(
            modifier=Modifier.fillMaxSize()
        ){
            TabRow(
                selectedTabIndex = selectedTab
            ){
                tabs.forEachIndexed{index,title->
                    Tab(
                        selected=selectedTab==index,
                        onClick = {selectedTab=index},
                        text={ Text(title) }
                    )
                }
            }
            when(selectedTab){
                0->EditAccountTab(userId)
                1->DeleteAccountTab(userId,navControllerRoot)
            }
        }
    }else if(configuration.orientation==Configuration.ORIENTATION_LANDSCAPE){
        Row(modifier=Modifier.fillMaxSize()){
            Column(modifier=Modifier.width(150.dp).fillMaxHeight()){
                TabRow(selectedTabIndex = selectedTab,modifier=Modifier.fillMaxWidth()){
                    tabs.forEachIndexed{index,title->
                        Tab(
                            selected = selectedTab==index,
                            onClick = {selectedTab=index},
                            text={Text(title)}
                        )
                    }
                }
            }
            Divider(color=Color.Gray,modifier=Modifier.fillMaxHeight().width(1.dp))
            Box(modifier=Modifier.weight(1f).padding(16.dp)){
                when(selectedTab){
                    0->EditAccountTab(userId)
                    1->DeleteAccountTab(userId,navControllerRoot)
                }
            }
        }
    }
}

@Composable
fun EditAccountTab(userId:String){
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context=LocalContext.current
    val configuration=LocalConfiguration.current
    val isLandscape=configuration.orientation== Configuration.ORIENTATION_LANDSCAPE

    var currentUsername by remember { mutableStateOf("") }
    var currentPhone by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }

    LaunchedEffect(true){
        val db = AppDatabase.getInstance(context).userDao()
        val user=db.getUserByUserId(userId)

        currentUsername=user?.name?:""
        currentPhone=user?.phone?:""
        currentEmail=user?.email?:""

        username=currentUsername
        phone=currentPhone
        email=currentEmail
    }

    LaunchedEffect(errorMessage){
        if(errorMessage!=null){
            delay(5000)
            errorMessage=null
        }
    }
    val scrollModifier = if (isLandscape) {
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    } else {
        Modifier.fillMaxSize().padding(16.dp)
    }

    Column(
        modifier=scrollModifier
    ){
        Text("Edit Your Account",style= MaterialTheme.typography.headlineSmall)
        Spacer(modifier=Modifier.height(16.dp))
        OutlinedTextField(value=username, onValueChange = {username=it},label={Text("Type a new username")},modifier=Modifier.fillMaxWidth())
        Spacer(modifier=Modifier.height(8.dp))
        OutlinedTextField(value=phone, onValueChange = {phone=it},label={Text("Type a new phone")},modifier=Modifier.fillMaxWidth())
        Spacer(modifier=Modifier.height(8.dp))
        OutlinedTextField(value=email, onValueChange = {email=it},label={Text("Type a new email")},modifier=Modifier.fillMaxWidth())
        Spacer(modifier=Modifier.height(16.dp))
        Button(
            modifier=Modifier.fillMaxWidth(),
            onClick={showConfirmDialog=true}
        ){
            Text("Save changes")
        }
        errorMessage.let{
            Spacer(modifier=Modifier.height(16.dp))
            Text(text=it?:"",color=MaterialTheme.colorScheme.error)
        }
    }

    if(showConfirmDialog==true){
        AlertDialog(
            onDismissRequest = {showConfirmDialog=false},
            title={Text("Confirm Edit")},
            text={Text("Are you sure you want to save these changes ?")},
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog=false
                        CoroutineScope(Dispatchers.IO).launch{
                            val db = AppDatabase.getInstance(context).userDao()
                            val existUsername=db.getUserByUsername(username)
                            val existPhone=db.getUserByPhone(phone)
                            val existEmail=db.getUserByEmail(email)

                            if(username.isBlank() || email.isBlank() || phone.isBlank()){
                                withContext(Dispatchers.Main){
                                    errorMessage="Fill all the fields"
                                }
                                return@launch
                            }

                            if(username==currentUsername && email==currentEmail && phone==currentPhone){
                                withContext(Dispatchers.Main){
                                    errorMessage="No changes detected"
                                }
                                return@launch
                            }

                            if((existUsername!=null && existUsername.uid!=userId) || (existPhone!=null && existPhone.uid!=userId) || (existEmail!=null && existEmail.uid!=userId)){
                                withContext(Dispatchers.Main){
                                    errorMessage="Username,email or phone already used by another user"
                                }
                                return@launch
                            }
                            val firestore= Firebase.firestore
                            val usernameQuery=firestore.collection("users").whereEqualTo("username",username).get().await()
                            val emailQuery=firestore.collection("users").whereEqualTo("email",email).get().await()
                            val phoneQuery=firestore.collection("users").whereEqualTo("phone",phone).get().await()

                            if(usernameQuery.documents.any{it.id!=userId} || emailQuery.documents.any{it.id!=userId} || phoneQuery.documents.any{it.id!=userId}){
                                withContext(Dispatchers.Main){
                                    errorMessage="Username,email or phone already used by another user"
                                }
                                return@launch
                            }
                            db.updateUser(UserAccountInfo(uid=userId,name=username,email=email,phone=phone))
                            firestore.collection("users").document(userId).update(
                                mapOf(
                                    "username" to username,
                                    "phone" to phone,
                                    "email" to email
                                )
                            )
                            withContext(Dispatchers.Main){
                                showNotification(context=context,title="Edit Completed Successfully!",message="Your account info changed successfully!")
                                errorMessage="Account info updated successfully!"
                                username=""
                                phone=""
                                email=""
                            }
                        }
                    }
                ){Text("Confirm")}
            },
            dismissButton = {
                TextButton(onClick = {showConfirmDialog=false}){Text("Cancel")}
            }
        )
    }
}

@Composable
fun DeleteAccountTab(userId:String,navControllerRoot: NavHostController){
    val context=LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier=Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Delete your account",style=MaterialTheme.typography.headlineSmall)
        Spacer(modifier=Modifier.height(24.dp))
        Button(
            modifier=Modifier.fillMaxWidth(),
            onClick={showDeleteDialog=true},
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ){
            Text("Delete My Account",color= MaterialTheme.colorScheme.onPrimary)
        }
    }
    if(showDeleteDialog==true){
        AlertDialog(
            onDismissRequest = {showDeleteDialog=false},
            title={Text("Confirm Deletion")},
            text={Text("Are you sure you want to delete your bank account?")},
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog=false
                    CoroutineScope(Dispatchers.IO).launch{
                        val db = AppDatabase.getInstance(context).userDao()
                        db.deleteUser(userId)
                        db.deleteBankAccount(userId)
                        Firebase.firestore.collection("users").document(userId).delete().await()
                        withContext(Dispatchers.Main){
                            FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener{task->
                                if(task.isSuccessful){
                                    showNotification(context=context,title="Bank Account Deletion",message="Your account has been successfully deleted from the app.Please note that your bank balance remains active within the banking system.")
                                    CoroutineScope(Dispatchers.IO).launch{
                                        val userCount=db.getUserCount()
                                        if(userCount==0){
                                            AppDatabase.getInstance(context).clearAllTables()
                                            clearAllFirestoreData()
                                        }
                                    }
                                    navControllerRoot.navigate(Screen.Login.route){
                                        popUpTo(0){inclusive=true}
                                    }
                                }
                            }
                        }
                    }
                }){Text("Confirm",color=MaterialTheme.colorScheme.primary)}
            },
            dismissButton = {
                TextButton(onClick = {showDeleteDialog=false}){
                    Text("Cancel")
                }
            }
        )
    }
}

suspend fun clearAllFirestoreData() {
    val usersCollection = Firebase.firestore.collection("users")
    val usersSnapshot = usersCollection.get().await()
    for (userDoc in usersSnapshot.documents) {
        val transactions = userDoc.reference.collection("transactions").get().await()
        for (tx in transactions.documents) {
            tx.reference.delete().await()
        }
        userDoc.reference.delete().await()
    }
}
