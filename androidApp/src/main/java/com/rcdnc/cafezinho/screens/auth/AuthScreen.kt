package com.rcdnc.cafezinho.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {

    AuthContent(
        onLoginSuccess = onLoginSuccess,
        onGoogleClick = onGoogleClick,
        onFacebookClick = onFacebookClick
    )
}

@Composable
fun AuthContent(
    onLoginSuccess: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {

    Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
    ) {

        Button(onClick = onGoogleClick) {
            Text("Login with Google")
        }

        Button(onClick = onFacebookClick) {
            Text("Login with Facebook")
        }
    }

}