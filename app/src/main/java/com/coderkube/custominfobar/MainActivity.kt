package com.coderkube.custominfobar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.coderkube.custominfobar.home.CustomInfoBarHome
import com.coderkube.custominfobar.ui.theme.CustomInfoBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomInfoBarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CustomInfoBarHome()
                }
            }
        }
    }
}