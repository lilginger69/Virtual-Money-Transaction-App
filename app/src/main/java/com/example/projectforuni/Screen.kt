package com.example.projectforuni

sealed class Screen(val route: String){
    object Main:Screen(route="main_screen")
    object Login:Screen(route="login_screen")
    object SignUp:Screen(route="sign_up_screen")
    object Dashboard:Screen(route="dashboard_screen")
    object TransferMoney:Screen(route="transfer_screen")
    object EditDeleteMyAccount: Screen(route="edit_delete_account")
    object Statistics: Screen(route="statistics_screen")
}