package com.example.indivassignment5q1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
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
        // Add some sample recipes for initial demonstration.
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IndivAssignment5Q1Theme {
                // The main app composable will be built here in the next step.
                Text("Setup Complete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IndivAssignment5Q1Theme {
        Text("Setup Complete")
    }
}
