package com.rcdnc.cafezinho.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rcdnc.cafezinho.screens.auth.AuthScreen


@Composable
fun NavGraph(
    startDestination: String = "auth",
) {
    val navController = rememberNavController()

    CafezinhoNavHost(
        startDestination = startDestination,
        navController = navController
    )

}

@Composable
fun CafezinhoNavHost(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        composable("login") {
            AuthScreen(
                onLoginSuccess = { /* Handle login success */ },
                onGoogleClick = { /* Handle Google login */ },
                onFacebookClick = { /* Handle Facebook login */ })
        }

        composable("signup") {
            SignupScreen(
                onSignupSuccess = { /* Handle signup success */ },
            )
        }
    }
}
