package com.example.italianhelper.data

data class Flashcard(
    val id: Int = 0,
    val italian: String,
    val english: String,
    val category: String,
    val exampleSentence: String = "",
    val exampleTranslation: String = "",
    val lastReview: Long = 0L,
    val nextReview: Long = 0L,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val easinessFactor: Float = 2.5f,
    val firestoreId: String = ""
)
