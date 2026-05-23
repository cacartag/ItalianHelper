package com.example.italianhelper.data

import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val flashcardDao: FlashcardDao) {
    fun getCardsToReview(currentTime: Long, category: String): Flow<List<Flashcard>> = 
        if (category == "Other") {
            flashcardDao.getOtherCardsToReview(currentTime)
        } else {
            flashcardDao.getCardsToReviewByCategory(currentTime, category)
        }

    suspend fun updateCard(flashcard: Flashcard) {
        flashcardDao.update(flashcard)
    }

    suspend fun insertAll(flashcards: List<Flashcard>) {
        flashcardDao.insertAll(flashcards)
    }

    suspend fun clearAll() {
        flashcardDao.deleteAll()
    }
}
