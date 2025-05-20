package com.example.projectforuni

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.util.TableInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.res.Configuration

@Composable
fun DashboardScreen() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var username by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }
    val context = LocalContext.current
    var balance by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current

    LaunchedEffect(userId) {
        userId?.let { uid ->
            Firebase.firestore.collection("users").document(uid).get().addOnSuccessListener { doc ->
                username = doc.getString("username") ?: "Unknown"
            }
        }
    }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    LaunchedEffect(userId) {
        userId?.let { uid ->
            val db = AppDatabase.getInstance(context).userDao()
            val ibanRes = withContext(Dispatchers.IO) {
                db.getIbanByUserID(uid)
            }
            iban = ibanRes?.IBAN ?: ""
            val txn = withContext(Dispatchers.IO) {
                db.getTransactionsForIban(iban)
            }
            transactions = txn
        }
    }

    LaunchedEffect(userId) {
        userId?.let { uid ->
            Firebase.firestore.collection("users").document(uid).get().addOnSuccessListener { doc ->
                balance = doc.getDouble("balance")?.toString() ?: "0.0"
            }
        }
    }
    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Hi,$username",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp, start = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Account", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = iban,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Available", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "€$balance",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Transactions", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (transactions.isEmpty()) {
                        Text("No transactions found", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Column {
                            transactions.reversed().forEach { txn ->
                                Column(
                                    modifier = Modifier
                                        .padding(vertical = 6.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                ) {
                                    val direction =
                                        if (txn.from == iban) "Sent to" else "Received from"
                                    val counterparty = if (txn.from == iban) txn.to else txn.from

                                    Text("$direction: $counterparty")
                                    Text("Amount: €${txn.amount}")
                                    Text(
                                        "Date: ${
                                            SimpleDateFormat(
                                                "dd MMM yyyy, HH:mm",
                                                Locale.getDefault()
                                            ).format(Date(txn.timestamp))
                                        }"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Hi,$username",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Account", style = MaterialTheme.typography.labelMedium)
                        Text(
                            iban,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text("Available", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "€$balance",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Transactions", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(18.dp))

                if (transactions.isEmpty()) {
                    Text("No transactions found", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        transactions.reversed().forEach { txn ->
                                val direction = if (txn.from == iban) "Sent to" else "Received from"
                                val counterparty = if (txn.from == iban) txn.to else txn.from
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    tonalElevation = 4.dp,
                                    shadowElevation = 4.dp,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("$direction: $counterparty", style = MaterialTheme.typography.bodyMedium)
                                        Text("Amount: €${txn.amount}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                    }
                }
            }

        }
    }
}