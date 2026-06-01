package com.example.italianhelper.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance().apply {
        // Enable offline persistence
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        firestoreSettings = settings
    }
    private val wordsCollection = db.collection("words")

    suspend fun getWords(): List<Flashcard> {
        val snapshot = wordsCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val italian = doc.getString("italian") ?: return@mapNotNull null
            val english = doc.getString("english") ?: return@mapNotNull null
            val category = doc.getString("category") ?: "Noun"
            val sentence = doc.getString("exampleSentence") ?: ""
            val translation = doc.getString("exampleTranslation") ?: ""
            
            // Map Firestore fields to Flashcard
            Flashcard(
                id = doc.id.hashCode(), // temporary mapping, doc.id is better
                italian = italian,
                english = english,
                category = category,
                exampleSentence = sentence,
                exampleTranslation = translation,
                lastReview = doc.getLong("lastReview") ?: 0L,
                nextReview = doc.getLong("nextReview") ?: 0L,
                interval = doc.getLong("interval")?.toInt() ?: 0,
                repetitions = doc.getLong("repetitions")?.toInt() ?: 0,
                easinessFactor = doc.getDouble("easinessFactor")?.toFloat() ?: 2.5f,
                firestoreId = doc.id // Add this field to Flashcard
            )
        }
    }

    suspend fun updateCard(flashcard: Flashcard) {
        if (flashcard.firestoreId.isEmpty()) return
        
        wordsCollection.document(flashcard.firestoreId).update(
            mapOf(
                "lastReview" to flashcard.lastReview,
                "nextReview" to flashcard.nextReview,
                "interval" to flashcard.interval,
                "repetitions" to flashcard.repetitions,
                "easinessFactor" to flashcard.easinessFactor
            )
        ).await()
    }
}
