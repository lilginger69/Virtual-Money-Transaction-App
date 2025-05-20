package com.example.projectforuni

import android.R
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

@Composable
fun StatisticsScreen(){
    val context= LocalContext.current
    val scope= rememberCoroutineScope()

    var sentTransactions by remember { mutableStateOf(0) }
    var totalAmountReceived by remember { mutableStateOf(0.0) }
    var countOverEqual500 by remember { mutableStateOf(0) }

    val userId= FirebaseAuth.getInstance().currentUser?.uid?:return
    var iban by remember { mutableStateOf("") }

    val configuration= LocalConfiguration.current
    val isLandscape=configuration.orientation== Configuration.ORIENTATION_LANDSCAPE

    var searchQuery by remember {mutableStateOf("")}
    var searchResults by remember { mutableStateOf<List<Map<String,Any>>>(emptyList()) }

    val scrollModifier=if(isLandscape){
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    }
    else{
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    }

    LaunchedEffect(true){
        scope.launch{
            withContext(Dispatchers.IO){
                val db= AppDatabase.getInstance(context).userDao()
                val account=db.getBankAccountByUserID(userId)
                iban=account?.IBAN?:return@withContext

                sentTransactions=db.getSentTransactionCount(iban)
                totalAmountReceived=db.getTotalReceivedAmount(iban)

                Firebase.firestore.collection("users").document(userId).collection("transactions").whereGreaterThanOrEqualTo("amount",500).get()
                    .addOnSuccessListener { result->
                        countOverEqual500=result.size()
                    }
            }
        }
    }
    Column(modifier=scrollModifier){
        Text("Statistics",style= MaterialTheme.typography.headlineLarge,color= MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 24.dp))
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier=Modifier.fillMaxWidth()
        ){
            Column(modifier=Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Text(text="Transactions sent",style= MaterialTheme.typography.titleMedium,color= MaterialTheme.colorScheme.primary)
                Spacer(modifier=Modifier.height(8.dp))
                Text(text="$sentTransactions",style= MaterialTheme.typography.headlineLarge,color= MaterialTheme.colorScheme.onSurface)
            }
        }
        Spacer(modifier=Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier=Modifier.fillMaxWidth()
        ){
            Column(modifier=Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Text(text="Total Amount Received (€)",style= MaterialTheme.typography.titleMedium,color= MaterialTheme.colorScheme.primary)
                Spacer(modifier=Modifier.height(8.dp))
                Text(text="€%.2f".format(totalAmountReceived),style= MaterialTheme.typography.headlineLarge,color= MaterialTheme.colorScheme.onSurface)
            }
        }
        Spacer(modifier=Modifier.height(16.dp))
        Card(
            shape=RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors= CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier=Modifier.fillMaxWidth()
        ){
            Column(modifier=Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Text(text="Number of Transactions ≥ €500",style=MaterialTheme.typography.titleMedium,color= MaterialTheme.colorScheme.primary)
                Spacer(modifier=Modifier.height(8.dp))
                Text("$countOverEqual500",style=MaterialTheme.typography.headlineLarge,color= MaterialTheme.colorScheme.onSurface)
            }
        }
        Spacer(modifier=Modifier.height(16.dp))
        Card(
            shape=RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors= CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier=Modifier.fillMaxWidth()
        ){
            Column(modifier=Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                OutlinedTextField(
                    value=searchQuery,
                    onValueChange = {searchQuery=it},
                    label={Text("Search by phone or email")},
                    modifier=Modifier.fillMaxWidth(),
                    leadingIcon={ Icon(imageVector = Icons.Default.Search, contentDescription = "Search by phone or email") }
                )
                Spacer(modifier=Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch{
                            withContext(Dispatchers.IO){
                                val db= AppDatabase.getInstance(context).userDao()

                                val targetUser=db.getUserByPhone(searchQuery) ?:  db.getUserByEmail(searchQuery)
                                val targetUserBankAccount=targetUser?.uid?.let{db.getBankAccountByUserID(it)}
                                val targetUserIBAN=targetUserBankAccount?.IBAN

                                if(targetUserIBAN!=null){
                                    val ibans=listOf(targetUserIBAN)

                                    val sent= Firebase.firestore.collection("users").document(userId)
                                        .collection("transactions")
                                        .whereIn("from",ibans)
                                        .orderBy("timestamp",Query.Direction.DESCENDING)
                                        .limit(3)
                                        .get()

                                    val received= Firebase.firestore.collection("users").document(userId)
                                        .collection("transactions")
                                        .whereIn("to",ibans)
                                        .orderBy("timestamp", Query.Direction.DESCENDING)
                                        .limit(3)
                                        .get()

                                    Tasks.whenAllSuccess<QuerySnapshot>(sent,received)
                                        .addOnSuccessListener{result->
                                            val combined=result
                                                .flatMap { it.documents }
                                                .sortedByDescending { it.getString("timestamp") }
                                                .take(3)

                                            searchResults=combined.mapNotNull{it.data}
                                        }
                                }
                            }
                        }
                    },
                    modifier=Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(30.dp)
                ){
                    Text("Search Transactions",color= MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier=Modifier.height(16.dp))
                if(searchResults.isEmpty()){
                    Text(text="No transactions found",style= MaterialTheme.typography.bodyMedium,color= MaterialTheme.colorScheme.error)
                }
                else{
                    Column{
                        searchResults.forEach { txn->
                            Text("From: ${txn["from"]}")
                            Text("To ${txn["to"]}")
                            Text(text = "Amount: €${txn["amount"]}")
                            Text(text = "Time: ${txn["timestamp"]}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}