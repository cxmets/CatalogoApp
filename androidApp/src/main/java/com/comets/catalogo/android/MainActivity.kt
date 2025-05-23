package com.comets.catalogo.android

import androidx.activity.ComponentActivity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    @Composable
    fun GreetingView(text: String) {
        Text(text = text)
    }

    @Preview
    @Composable
    fun DefaultPreview() {
        MyApplicationTheme {
            GreetingView("Hello, Android!")
        }
    }
}
