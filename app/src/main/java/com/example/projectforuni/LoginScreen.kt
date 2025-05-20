package com.example.projectforuni

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.*
import kotlinx.coroutines.delay
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val configuration = LocalConfiguration.current

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            delay(5000)
            errorMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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

        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LoginContentPortrait(
                username = username,
                password = password,
                onUsernameChange = { username = it },
                onPasswordChange = { password = it },
                navController = navController,
                onError = { errorMessage = it }
            )
        } else {
            LoginContentLandscape(
                username = username,
                password = password,
                onUsernameChange = { username = it },
                onPasswordChange = { password = it },
                navController = navController,
                onError = { errorMessage = it }
            )
        }
    }
}

@Composable
fun LoginContentPortrait(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    navController: NavHostController,
    onError: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Image(
            painter = painterResource(id = R.drawable.sign_in),
            contentDescription = "Sign In",
            modifier = Modifier.height(180.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextFieldArea(username = username, onValueChange = onUsernameChange)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordFieldArea(password = password, onValueChange = onPasswordChange)
        Spacer(modifier = Modifier.height(16.dp))
        LoginButtons(username, password, navController, onError)
    }
}

@Composable
fun LoginContentLandscape(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    navController: NavHostController,
    onError: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.sign_in),
            contentDescription = "Sign In",
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldArea(username = username, onValueChange = onUsernameChange)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordFieldArea(password = password, onValueChange = onPasswordChange)
            Spacer(modifier = Modifier.height(16.dp))
            LoginButtons(username, password, navController, onError)
        }
    }
}

@Composable
fun TextFieldArea(username: String, onValueChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = username,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Person, contentDescription = "Username")
        },
        label = { Text("Email Address") },
        placeholder = { Text("Enter your email") },
        singleLine = true,
        shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordFieldArea(password: String, onValueChange: (String) -> Unit) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = "Password")
        },
        trailingIcon = {
            val image = if (passwordVisible)
                painterResource(id = R.drawable.visibility_icon_off)
            else
                painterResource(id = R.drawable.visibility_icon_on)

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = image, contentDescription = null)
            }
        },
        label = { Text("Password") },
        placeholder = { Text("Enter your password") },
        singleLine = true,
        shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LoginButtons(
    username: String,
    password: String,
    navController: NavHostController,
    onError: (String) -> Unit
) {
    Button(
        onClick = {
            if (username.isBlank() || password.isBlank()) {
                onError("Fields must not be empty")
                return@Button
            }
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    onError(
                        when (exception) {
                            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                            is FirebaseAuthInvalidUserException -> "User not found"
                            else -> "Login failed: ${exception.localizedMessage}"
                        }
                    )
                }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(30.dp)
    ) {
        Text("Login", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
    }

    TextButton(
        onClick = {
            navController.navigate(Screen.SignUp.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Don't have an account? Sign up", color = MaterialTheme.colorScheme.primary)
    }
}
