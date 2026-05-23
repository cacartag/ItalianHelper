package com.example.italianhelper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onCategorySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Italian Helper", fontSize = 32.sp, modifier = Modifier.padding(top = 32.dp, bottom = 32.dp))
        
        Text(text = "Choose a category to practice:", fontSize = 18.sp, modifier = Modifier.padding(bottom = 24.dp))

        CategoryButton("Verbs", "Verb", onCategorySelected)
        CategoryButton("Nouns", "Noun", onCategorySelected)
        CategoryButton("Adjectives", "Adjective", onCategorySelected)
        CategoryButton("Others (Pronouns, etc.)", "Other", onCategorySelected)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CategoryButton(
    label: String,
    category: String,
    onCategorySelected: (String) -> Unit
) {
    Button(
        onClick = { onCategorySelected(category) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(56.dp)
    ) {
        Text(text = label, fontSize = 18.sp)
    }
}
