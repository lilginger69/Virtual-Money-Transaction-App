package com.example.projectforuni

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerMenuItem(
    val id:String,
    val title: String,
    val contentDescription:String,
    val icon: DrawerIcon
)
