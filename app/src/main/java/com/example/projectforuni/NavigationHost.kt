package com.example.projectforuni

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationHost(navController: NavHostController){
    NavHost(
        navController=navController,
        startDestination=Screen.Login.route
    ){
        composable(route=Screen.Login.route){LoginScreen(navController)}
        composable(route=Screen.Main.route){NavigationMenu(navController)}
        composable(route=Screen.SignUp.route){SignUpScreen(navController)}
    }
}