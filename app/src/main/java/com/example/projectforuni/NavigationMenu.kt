package com.example.projectforuni

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.projectforuni.ui.theme.ProjectForUniTheme
import kotlinx.coroutines.launch
import androidx.navigation.compose.composable
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.material3.DrawerState
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme

@Composable
fun NavigationMenu(navControllerRoot: NavHostController) {
    val orientation = LocalConfiguration.current.orientation
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentTitle = remember { mutableStateOf("") }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentTitle.value = when (backStackEntry.destination.route) {
                Screen.Dashboard.route -> "Dashboard"
                Screen.TransferMoney.route -> "Transfer Money"
                Screen.EditDeleteMyAccount.route -> "Edit/Delete My Bank Account"
                Screen.Statistics.route->"Statistics"
                else -> ""
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                DrawerContentPortrait(navControllerRoot, navController, drawerState, scope)
            } else {
                DrawerContentLandscape(navControllerRoot, navController, drawerState, scope)
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = currentTitle.value,
                    onNavigationIconClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
                    composable(Screen.Dashboard.route) { DashboardScreen() }
                    composable(Screen.TransferMoney.route) { TransferMoneyScreen() }
                    composable(Screen.EditDeleteMyAccount.route) { EditDeleteMyAccountScreen(navControllerRoot) }
                    composable(Screen.Statistics.route){StatisticsScreen()}
                }
            }
        }
    }
}
@Composable
fun DrawerContentPortrait(
    navControllerRoot: NavHostController,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DrawerHeader()
        DrawerBody(navControllerRoot, navController, drawerState, scope)
    }
}

@Composable
fun DrawerContentLandscape(
    navControllerRoot: NavHostController,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        DrawerHeader()
        Divider()
        DrawerBody(
            navControllerRoot,
            navController,
            drawerState,
            scope,
            modifier = Modifier.weight(1f)
        )
    }
}