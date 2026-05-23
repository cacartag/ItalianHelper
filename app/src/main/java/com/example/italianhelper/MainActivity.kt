package com.example.italianhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.italianhelper.ui.theme.ItalianHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItalianHelperTheme {
                val viewModel: FlashcardViewModel = viewModel()
                val cardsToReview by viewModel.cardsToReview.collectAsState()
                val selectedCategory by viewModel.selectedCategory.collectAsState()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (selectedCategory == null) {
                        HomeScreen(
                            onCategorySelected = { category ->
                                viewModel.selectCategory(category)
                            }
                        )
                    } else {
                        FlashcardScreen(
                            cards = cardsToReview,
                            categoryName = selectedCategory!!,
                            modifier = Modifier.padding(innerPadding),
                            onReview = { card, quality ->
                                viewModel.reviewCard(card, quality)
                            },
                            onBack = {
                                viewModel.selectCategory(null)
                            }
                        )
                    }
                }
            }
        }
    }
}
