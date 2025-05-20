package com.example.projectforuni

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.contracts.contract
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import android.content.Context
import androidx.compose.material3.TextButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.platform.SoftwareKeyboardController

@Composable
fun SignUpScreen(navController: NavHostController){
    var name by rememberSaveable {mutableStateOf("")}
    var phone by rememberSaveable {mutableStateOf("")}
    var email by rememberSaveable {mutableStateOf("")}
    var password by rememberSaveable{mutableStateOf("")}
    var passwordHidden by rememberSaveable{mutableStateOf(true)}
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var confirmPasswordHidden by rememberSaveable { mutableStateOf(true) }
    val keyboardController=LocalSoftwareKeyboardController.current
    var errorMessage by remember {mutableStateOf<String?>(null)}
    val configuration=LocalConfiguration.current

    LaunchedEffect(errorMessage){
        if(errorMessage!=null){
            delay(5000)
            errorMessage=null
        }
    }
    val auth= FirebaseAuth.getInstance()
    val firestore= FirebaseFirestore.getInstance()
    Box(
        modifier=Modifier.fillMaxWidth().fillMaxHeight().background(MaterialTheme.colorScheme.background)
    ){
        errorMessage?.let{
            Box(
                modifier=Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text=it,
                    color=Color.White,
                    modifier=Modifier.padding(horizontal = 16.dp, vertical = 8.dp).background(color= MaterialTheme.colorScheme.error,shape=RoundedCornerShape(8.dp)),
                    style=MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    if(configuration.orientation== Configuration.ORIENTATION_PORTRAIT){
        Box(
            modifier=Modifier.fillMaxWidth(0.8f)
        ){
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Spacer(modifier=Modifier.height(32.dp))
                    Image(
                        painter = painterResource(R.drawable.sign_up),
                        contentDescription = null,
                        modifier = Modifier.width(200.dp).padding(top=48.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(modifier = Modifier.align(Alignment.CenterHorizontally).offset(x=30.dp),
                    text = "Create An Account",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Box(
            modifier=Modifier.fillMaxWidth().fillMaxHeight().background(color= Color.Transparent).offset(y=20.dp),
            contentAlignment = Alignment.Center

        ){
            Column(
                modifier=Modifier.padding(16.dp).fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,

                ){
                OutlinedTextField(
                    value=name,
                    onValueChange = {name=it},
                    label = {Text("Enter Username",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    placeholder={Text("Username")},
                    singleLine = true,
                    shape= RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary

                    ),
                    leadingIcon={
                        Icon(imageVector = Icons.Default.Person, contentDescription="Enter Name")

                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onDone={keyboardController?.hide()})

                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = {phone=it},
                    label={Text("Enter Phone",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    placeholder = {Text("Phone")},
                    singleLine = true,
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary

                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Enter Phone")

                    },
                    shape= RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onDone={keyboardController?.hide()})

                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value=email,
                    onValueChange={email=it},
                    label={Text("Enter Email",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    placeholder = {Text("Email")},
                    singleLine = true,
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary

                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Enter Email")

                    },
                    shape= RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onDone={keyboardController?.hide()})
                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value=password,
                    onValueChange = {password=it},
                    keyboardOptions= KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done

                    ),
                    shape=RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    leadingIcon={
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Create password")

                    },
                    label={Text("Enter Password",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    singleLine=true,
                    trailingIcon={
                        IconButton(onClick={passwordHidden=!passwordHidden}){
                            Icon(
                                painter = painterResource(
                                    if(passwordHidden) R.drawable.visibility_icon_on else R.drawable.visibility_icon_off
                                ), contentDescription = "Toggle password visibility"
                            )
                        }

                    },
                    visualTransformation =
                        if(passwordHidden){
                            PasswordVisualTransformation()
                        }
                        else{
                            VisualTransformation.None

                        },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value=confirmPassword,
                    onValueChange = {confirmPassword=it},
                    label={Text(text = "Confirm Password",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    visualTransformation = if(confirmPasswordHidden){
                        PasswordVisualTransformation()

                    }
                    else{
                        VisualTransformation.None

                    },
                    singleLine = true,
                    leadingIcon = {Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm Password")},
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done

                    ),
                    shape=RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    trailingIcon = {IconButton(onClick = {confirmPasswordHidden=!confirmPasswordHidden}){
                        Icon(
                            painter=painterResource(if(confirmPasswordHidden) R.drawable.visibility_icon_on else R.drawable.visibility_icon_off),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }

                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier=Modifier.height(5.dp))
                val context = LocalContext.current
                Button(
                    onClick = {
                        errorMessage = null
                        if (name.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Fill all the fields"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getInstance(context).userDao()
                            val existsLocal =
                                db.getUserByUsername(name) != null || db.getUserByEmail(email) != null || db.getUserByPhone(
                                    phone
                                ) != null
                            if (existsLocal) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "User already exists"
                                }
                                return@launch
                            }
                            val usernameSnapshot =
                                firestore.collection("users").whereEqualTo("username", name).get()
                                    .await()
                            val emailSnapshot =
                                firestore.collection("users").whereEqualTo("email", email).get()
                                    .await()
                            val phoneSnapshot =
                                firestore.collection("users").whereEqualTo("phone", phone).get()
                                    .await()
                            if (!usernameSnapshot.isEmpty || !emailSnapshot.isEmpty || !phoneSnapshot.isEmpty) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "User already exists"
                                }
                                return@launch
                            }
                            val iban = "GR" + List(25) { ('0'..'9').random() }.joinToString("")
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { result ->
                                    val userID = result.user?.uid ?: return@addOnSuccessListener
                                    val user = hashMapOf(
                                        "username" to name,
                                        "email" to email,
                                        "phone" to phone,
                                        "balance" to 1000.0,
                                        "iban" to iban
                                    )
                                    firestore.collection("users").document(userID).set(user)
                                        .addOnSuccessListener {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                db.insertUserAccountInfo(
                                                    UserAccountInfo(
                                                        uid = userID,
                                                        name = name,
                                                        email = email,
                                                        phone = phone
                                                    )
                                                )
                                                db.insertBankAccountInfo(
                                                    BankAccountInfo(
                                                        IBAN = iban,
                                                        userID = userID,
                                                        balance = 1000.0
                                                    )
                                                )
                                                withContext(Dispatchers.Main) {
                                                    showNotification(
                                                        context = context,
                                                        title = "Account Creation",
                                                        message = "Your account was created successfully!"
                                                    )
                                                    name = ""
                                                    phone = ""
                                                    email = ""
                                                    password = ""
                                                    confirmPassword = ""
                                                    keyboardController?.hide()
                                                }
                                            }
                                        }
                                }.addOnFailureListener {
                                errorMessage = "Failed to create user: ${it.localizedMessage}"
                            }
                        }
                    },
                    modifier=Modifier.fillMaxWidth(0.8f),
                    shape=RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp)
                ){
                    Text(text="Create An Account",color=Color.White, fontSize = 20.sp)
                }
                Button(
                    onClick= {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    modifier=Modifier.fillMaxWidth(),
                    colors= ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary)
                ){
                    Text(text="Sign In")
                }
            }
        }
    }
    else if(configuration.orientation== Configuration.ORIENTATION_LANDSCAPE){
        Row(
            modifier=Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ){
            Box(
                modifier=Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center
            ){
                Image(painterResource(R.drawable.sign_up), contentDescription = null,modifier=Modifier.fillMaxWidth(0.8f).aspectRatio(1f), contentScale = ContentScale.Fit)
            }
            Spacer(modifier=Modifier.width(16.dp))
            Column(
                modifier=Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
            ){
                OutlinedTextField(
                    value=name,
                    onValueChange = {name=it},
                    label = {Text("Enter Username",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    placeholder={Text("Username")},
                    singleLine = true,
                    shape= RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary

                    ),
                    leadingIcon={
                        Icon(imageVector = Icons.Default.Person, contentDescription="Enter Name")

                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onDone={keyboardController?.hide()})

                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = {phone=it},
                    label={Text("Enter Phone",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    placeholder = {Text("Phone")},
                    singleLine = true,
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary

                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Enter Phone")

                    },
                    shape= RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onDone={keyboardController?.hide()})

                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value=email,
                    onValueChange={email=it},
                    label={Text("Enter Email",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    placeholder = {Text("Email")},
                    singleLine = true,
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary

                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Enter Email")

                    },
                    shape= RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onDone={keyboardController?.hide()})
                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value=password,
                    onValueChange = {password=it},
                    keyboardOptions= KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done

                    ),
                    shape=RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    leadingIcon={
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Create password")

                    },
                    label={Text("Enter Password",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    singleLine=true,
                    trailingIcon={
                        IconButton(onClick={passwordHidden=!passwordHidden}){
                            Icon(
                                painter = painterResource(
                                    if(passwordHidden) R.drawable.visibility_icon_on else R.drawable.visibility_icon_off
                                ), contentDescription = "Toggle password visibility"
                            )
                        }

                    },
                    visualTransformation =
                        if(passwordHidden){
                            PasswordVisualTransformation()
                        }
                        else{
                            VisualTransformation.None

                        },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier=Modifier.height(5.dp))
                OutlinedTextField(
                    value=confirmPassword,
                    onValueChange = {confirmPassword=it},
                    label={Text(text = "Confirm Password",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)},
                    visualTransformation = if(confirmPasswordHidden){
                        PasswordVisualTransformation()

                    }
                    else{
                        VisualTransformation.None

                    },
                    singleLine = true,
                    leadingIcon = {Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm Password")},
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done

                    ),
                    shape=RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
                    trailingIcon = {IconButton(onClick = {confirmPasswordHidden=!confirmPasswordHidden}){
                        Icon(
                            painter=painterResource(if(confirmPasswordHidden) R.drawable.visibility_icon_on else R.drawable.visibility_icon_off),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }

                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier=Modifier.height(5.dp))
                val context = LocalContext.current
                Button(
                    onClick = {
                        errorMessage = null
                        if (name.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Fill all the fields"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getInstance(context).userDao()
                            val existsLocal =
                                db.getUserByUsername(name) != null || db.getUserByEmail(email) != null || db.getUserByPhone(
                                    phone
                                ) != null
                            if (existsLocal) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "User already exists"
                                }
                                return@launch
                            }
                            val usernameSnapshot =
                                firestore.collection("users").whereEqualTo("username", name).get()
                                    .await()
                            val emailSnapshot =
                                firestore.collection("users").whereEqualTo("email", email).get()
                                    .await()
                            val phoneSnapshot =
                                firestore.collection("users").whereEqualTo("phone", phone).get()
                                    .await()
                            if (!usernameSnapshot.isEmpty || !emailSnapshot.isEmpty || !phoneSnapshot.isEmpty) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "User already exists"
                                }
                                return@launch
                            }
                            val iban = "GR" + List(25) { ('0'..'9').random() }.joinToString("")
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { result ->
                                    val userID = result.user?.uid ?: return@addOnSuccessListener
                                    val user = hashMapOf(
                                        "username" to name,
                                        "email" to email,
                                        "phone" to phone,
                                        "balance" to 5000.0,
                                        "iban" to iban
                                    )
                                    firestore.collection("users").document(userID).set(user)
                                        .addOnSuccessListener {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                db.insertUserAccountInfo(
                                                    UserAccountInfo(
                                                        uid = userID,
                                                        name = name,
                                                        email = email,
                                                        phone = phone
                                                    )
                                                )
                                                db.insertBankAccountInfo(
                                                    BankAccountInfo(
                                                        IBAN = iban,
                                                        userID = userID,
                                                        balance = 5000.0
                                                    )
                                                )
                                                withContext(Dispatchers.Main) {
                                                    showNotification(
                                                        context = context,
                                                        title = "Account Creation",
                                                        message = "Your account was created successfully!"
                                                    )
                                                    name = ""
                                                    phone = ""
                                                    email = ""
                                                    password = ""
                                                    confirmPassword = ""
                                                    keyboardController?.hide()
                                                }
                                            }
                                        }
                                }.addOnFailureListener {
                                    errorMessage = "Failed to create user: ${it.localizedMessage}"
                                }
                        }
                    },
                    modifier=Modifier.fillMaxWidth(0.8f),
                    shape=RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp)
                ){
                    Text(text="Create An Account",color=Color.White, fontSize = 20.sp)
                }
                Button(
                    onClick= {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    modifier=Modifier.fillMaxWidth(),
                    colors= ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary)
                ){
                    Text(text="Sign In")
                }
            }
        }
    }
}