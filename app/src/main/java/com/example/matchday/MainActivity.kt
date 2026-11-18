package com.example.matchday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.matchday.presentation.navigation.AppNavGraph
import com.example.matchday.presentation.ui.theme.MatchdayTheme
import com.example.matchday.presentation.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MatchdayTheme {
                AppNavGraph(appViewModel)
            }
        }
    }
}
