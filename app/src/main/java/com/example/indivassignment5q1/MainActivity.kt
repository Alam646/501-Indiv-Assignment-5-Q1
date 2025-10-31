package com.example.indivassignment5q1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.indivassignment5q1.ui.theme.IndivAssignment5Q1Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Data class to define the structure of a single recipe.
data class Recipe(
    val id: Int,
    val title: String,
    val ingredients: String,
    val steps: String
)

// The ViewModel holds the application's state in memory.
class RecipeViewModel : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()

    init {
        _recipes.value = listOf(
            Recipe(
                id = 1,
                title = "Nihari",
                ingredients = "1 kg beef shank, 1/4 cup ghee, 2 large onions, Ginger-garlic paste, Nihari masala, Flour slurry",
                steps = "1. Brown beef in ghee with onions. 2. Add spices and water, then slow cook for 4-6 hours until tender. 3. Thicken the gravy with a flour slurry. 4. Garnish with ginger, cilantro, and lemon."
            ),
            Recipe(
                id = 2,
                title = "Chicken Tikka Masala",
                ingredients = "1 lb chicken, 1 cup yogurt, 1 tbsp ginger-garlic paste, 1 can crushed tomatoes, 1 cup heavy cream, Spices",
                steps = "1. Marinate chicken. 2. Grill chicken. 3. Make the sauce. 4. Combine and simmer."
            )
        )
    }

    fun addRecipe(title: String, ingredients: String, steps: String) {
        val newId = (_recipes.value.maxOfOrNull { it.id } ?: 0) + 1
        val newRecipe = Recipe(newId, title, ingredients, steps)
        _recipes.update { it + newRecipe }
    }
}

// Sealed class for defining navigation routes for type safety.
sealed class Routes(val route: String, val label: String, val icon: ImageVector) {
    object Home : Routes("home", "Home", Icons.Default.Home)
    object Add : Routes("add", "Add", Icons.Default.Add)
    object Settings : Routes("settings", "Settings", Icons.Default.Settings)
    object Detail : Routes("detail/{recipeId}", "Detail", Icons.Default.Home) { // Icon isn't shown but required by class structure
        fun createRoute(recipeId: Int) = "detail/$recipeId"
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: RecipeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IndivAssignment5Q1Theme {
                RecipeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RecipeApp(viewModel: RecipeViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { innerPadding ->
        // The NavHost will go here in a future step.
        Text("App Skeleton", modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val items = listOf(Routes.Home, Routes.Add, Routes.Settings)

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IndivAssignment5Q1Theme {
        RecipeApp(viewModel = RecipeViewModel())
    }
}
