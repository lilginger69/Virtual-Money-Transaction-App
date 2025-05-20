package com.example.projectforuni
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
sealed class DrawerIcon {
    data class Vector(val icon: ImageVector) : DrawerIcon()
    data class PainterIcon(val painter: Painter) : DrawerIcon()
}
