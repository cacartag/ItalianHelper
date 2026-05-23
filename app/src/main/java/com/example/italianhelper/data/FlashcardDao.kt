package com.example.italianhelper.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE nextReview <= :currentTime AND category = :category ORDER BY nextReview ASC")
    fun getCardsToReviewByCategory(currentTime: Long, category: String): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE nextReview <= :currentTime AND category NOT IN ('Verb', 'Noun', 'Adjective') ORDER BY nextReview ASC")
    fun getOtherCardsToReview(currentTime: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE category = :category")
    fun getAllCardsByCategory(category: String): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flashcards: List<Flashcard>)

    @Update
    suspend fun update(flashcard: Flashcard)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getCount(): Int

    @Query("DELETE FROM flashcards")
    suspend fun deleteAll()
}
