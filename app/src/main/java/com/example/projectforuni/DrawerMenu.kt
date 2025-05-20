package com.example.projectforuni

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.ColorFilter
import androidx.navigation.NavHostController
import androidx.compose.material3.DrawerState
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.res.painterResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Menu",
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun DrawerBody(
    navControllerRoot: NavHostController,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onBackground

    val items = listOf(
        DrawerMenuItem(Screen.Dashboard.route, "Dashboard", "Go to dashboard", DrawerIcon.PainterIcon(painterResource(R.drawable.dashboard_icon))),
        DrawerMenuItem(Screen.TransferMoney.route, "Transfer Money", "Go to transfer", DrawerIcon.PainterIcon(painterResource(R.drawable.send_money_icon))),
        DrawerMenuItem(Screen.Statistics.route, "Bank Account Statistics", "Go to stats", DrawerIcon.PainterIcon(painterResource(R.drawable.analytics_24dp))),
        DrawerMenuItem(Screen.EditDeleteMyAccount.route, "Edit/Delete My Bank Account", "Go to edit", DrawerIcon.Vector(Icons.Default.Edit))
    )

    LazyColumn(modifier = modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch { drawerState.close() }
                        navController.navigate(item.id) { launchSingleTop = true }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (val icon = item.icon) {
                    is DrawerIcon.Vector -> Icon(
                        imageVector = icon.icon,
                        contentDescription = item.contentDescription,
                        modifier = Modifier.size(24.dp),
                        tint = textColor
                    )
                    is DrawerIcon.PainterIcon -> Image(
                        painter = icon.painter,
                        contentDescription = item.contentDescription,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(textColor)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(item.title, color = textColor)
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        FirebaseAuth.getInstance().signOut()
                        navControllerRoot.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = textColor)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Logout", color = textColor)
            }
        }
    }
}
