package com.manish.wardrobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.composable
import com.manish.wardrobe.ui.add.AddScreen
import com.manish.wardrobe.ui.home.HomeScreen
import com.manish.wardrobe.ui.theme.WardrobeAppTheme
import com.manish.wardrobe.viewmodel.ClothingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ClothingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WardrobeAppTheme {

                val navController = androidx.navigation.compose.rememberNavController()

                androidx.navigation.compose.NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {

                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onAddClick = {
                                navController.navigate("add")
                            }
                        )
                    }

                    composable("add") {
                        AddScreen(
                            viewModel = viewModel,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }

    }
}
