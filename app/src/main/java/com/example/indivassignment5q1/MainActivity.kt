package com.example.indivassignment5q1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        AppNavHost(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Routes.Home.route, modifier = modifier) {
        composable(Routes.Home.route) {
            HomeScreen(viewModel = viewModel, onRecipeClick = {
                navController.navigate(Routes.Detail.createRoute(it.id))
            })
        }
        composable(
            route = Routes.Detail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1
            DetailScreen(viewModel = viewModel, recipeId = recipeId, onBack = { navController.popBackStack() })
        }
        composable(Routes.Add.route) {
            Text("Add Recipe Screen")
        }
        composable(Routes.Settings.route) {
            Text("Settings Screen")
        }
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

@Composable
fun HomeScreen(viewModel: RecipeViewModel, onRecipeClick: (Recipe) -> Unit) {
    val recipes by viewModel.recipes.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item { Text("What’s for Dinner?", style = MaterialTheme.typography.headlineMedium) }
        item { Spacer(Modifier.height(16.dp)) }
        items(recipes) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onRecipeClick(recipe) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = recipe.title,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: RecipeViewModel, recipeId: Int, onBack: () -> Unit) {
    val recipes by viewModel.recipes.collectAsState()
    val recipe = recipes.find { it.id == recipeId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.title ?: "Recipe not found") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { innerPadding ->
        if (recipe != null) {
            LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                item { Text(text = "Ingredients", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                item { Text(recipe.ingredients, style = MaterialTheme.typography.bodyLarge) }
                item { Spacer(Modifier.height(16.dp)) }
                item { Text(text = "Steps", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                item { Text(recipe.steps, style = MaterialTheme.typography.bodyLarge) }
            }
        } else {
            Text("Recipe not found.", modifier = Modifier.padding(innerPadding).padding(16.dp))
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
