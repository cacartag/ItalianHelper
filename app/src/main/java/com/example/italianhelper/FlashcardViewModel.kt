package com.example.italianhelper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.italianhelper.data.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlashcardRepository
    private val firebaseRepository = FirebaseRepository()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val cardsToReview: StateFlow<List<Flashcard>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) flowOf(emptyList())
            else repository.getCardsToReview(System.currentTimeMillis(), category)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val dao = AppDatabase.getDatabase(application).flashcardDao()
        repository = FlashcardRepository(dao)
        
        syncWithFirebase()
    }

    private fun syncWithFirebase() {
        viewModelScope.launch {
            println("SRS_DEBUG: Starting sync...")
            val cloudWords = firebaseRepository.getWords()
            println("SRS_DEBUG: Cloud words found: ${cloudWords.size}")
            if (cloudWords.isEmpty()) {
                println("SRS_DEBUG: Cloud is empty, loading from assets...")
                val initialWords = loadWordsFromAssets()
                println("SRS_DEBUG: Loaded ${initialWords.size} words from assets")
                if (initialWords.isNotEmpty()) {
                    firebaseRepository.seedDatabase(initialWords)
                    println("SRS_DEBUG: Seeded cloud database")
                    repository.clearAll()
                    repository.insertAll(initialWords)
                }
            } else {
                println("SRS_DEBUG: Cloud has words, syncing to local...")
                repository.clearAll()
                repository.insertAll(cloudWords)
            }
        }
    }

    private fun loadWordsFromAssets(): List<Flashcard> {
        return try {
            val inputStream = getApplication<Application>().assets.open("words.json")
            val content = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<WordEntry>>() {}.type
            val wordEntries: List<WordEntry> = Gson().fromJson(content, type)
            wordEntries.map { 
                Flashcard(
                    italian = it.it,
                    english = it.en,
                    category = it.cat,
                    exampleSentence = it.s ?: "",
                    exampleTranslation = it.st ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun reviewCard(flashcard: Flashcard, quality: Int) {
        viewModelScope.launch {
            val updatedCard = calculateNextReview(flashcard, quality)
            repository.updateCard(updatedCard)
        }
    }
}

data class WordEntry(val it: String, val en: String, val cat: String, val s: String? = null, val st: String? = null)
