package com.example.topcharacterlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.topcharacterlist.data.remote.responses.Data
import com.example.topcharacterlist.repository.CharacterRepository
import com.example.topcharacterlist.screens.TopCharacterScreen
import com.example.topcharacterlist.ui.theme.TopCharacterListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TopCharacterListTheme {
                val navController = rememberNavController() //state passed down to other composable fun
                NavHost(
                    navController = navController,
                    startDestination = "top_character_screen"
                ){
                    composable("top_character_screen"){
                        TopCharacterScreen(navController = navController)
                    }
                }
            }
        }
    }
}

