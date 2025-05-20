package com.example.projectforuni

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import android.graphics.ColorFilter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title:String,
    onNavigationIconClick:()-> Unit
){
    TopAppBar(
        title={
            Text(text=title)
        },
        colors= TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary),
        navigationIcon = {
            IconButton(onClick=onNavigationIconClick){
                Icon(painterResource(R.drawable.menu_66dp), contentDescription = "Menu",tint=MaterialTheme.colorScheme.onPrimary)
            }
        }
    )
}