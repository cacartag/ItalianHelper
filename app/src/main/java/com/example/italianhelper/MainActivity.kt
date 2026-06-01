package com.example.italianhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                
                val uiState by viewModel.uiState.collectAsState()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val state = uiState) {
                        is UiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { viewModel.syncWithFirebase() }) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                        is UiState.Success -> {
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
    }
}
