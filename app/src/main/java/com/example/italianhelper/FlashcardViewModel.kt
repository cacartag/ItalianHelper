package com.example.italianhelper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.italianhelper.data.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseRepository = FirebaseRepository()
    
    private val _allCards = MutableStateFlow<List<Flashcard>>(emptyList())
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val cardsToReview: StateFlow<List<Flashcard>> = combine(_allCards, _selectedCategory) { cards, category ->
        if (category == null) emptyList()
        else {
            val currentTime = System.currentTimeMillis()
            cards.filter { card ->
                val isCategoryMatch = if (category == "Other") {
                    card.category !in listOf("Verb", "Noun", "Adjective")
                } else {
                    card.category == category
                }
                isCategoryMatch && card.nextReview <= currentTime
            }.sortedBy { it.nextReview }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        syncWithFirebase()
    }

    fun syncWithFirebase() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                println("SRS_DEBUG: Starting sync...")
                val cloudWords = firebaseRepository.getWords()
                println("SRS_DEBUG: Cloud words found: ${cloudWords.size}")
                
                if (cloudWords.isEmpty()) {
                    println("SRS_DEBUG: Cloud is empty.")
                    _uiState.value = UiState.Error("Database is empty. Please contact support.")
                } else {
                    _allCards.value = cloudWords
                    _uiState.value = UiState.Success
                }
            } catch (e: Exception) {
                println("SRS_DEBUG: Sync failed: ${e.message}")
                _uiState.value = UiState.Error("Network error. Check your connection.")
            }
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun reviewCard(flashcard: Flashcard, quality: Int) {
        viewModelScope.launch {
            val updatedCard = calculateNextReview(flashcard, quality)
            // Update local state immediately for snappy UI
            _allCards.value = _allCards.value.map { 
                if (it.firestoreId == flashcard.firestoreId) updatedCard else it 
            }
            // Sync to Firebase
            firebaseRepository.updateCard(updatedCard)
        }
    }
}

data class WordEntry(val it: String, val en: String, val cat: String, val s: String? = null, val st: String? = null)

sealed interface UiState {
    object Loading : UiState
    object Success : UiState
    data class Error(val message: String) : UiState
}
