package com.example.matchday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.matchday.navigation.AppNavGraph
import com.example.matchday.ui.theme.MatchdayTheme
import com.example.matchday.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        setContent {
            MatchdayTheme {
                AppNavGraph(appViewModel)
            }
        }
    }
}
