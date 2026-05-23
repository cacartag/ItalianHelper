package com.example.italianhelper.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val wordsCollection = db.collection("words")

    suspend fun getWords(): List<Flashcard> {
        return try {
            val snapshot = wordsCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val italian = doc.getString("italian") ?: return@mapNotNull null
                val english = doc.getString("english") ?: return@mapNotNull null
                val category = doc.getString("category") ?: "Noun"
                val sentence = doc.getString("exampleSentence") ?: ""
                val translation = doc.getString("exampleTranslation") ?: ""
                Flashcard(
                    italian = italian,
                    english = english,
                    category = category,
                    exampleSentence = sentence,
                    exampleTranslation = translation
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun seedDatabase(words: List<Flashcard>) {
        try {
            words.chunked(500).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { word ->
                    val docRef = wordsCollection.document()
                    batch.set(docRef, mapOf(
                        "italian" to word.italian,
                        "english" to word.english,
                        "category" to word.category,
                        "exampleSentence" to word.exampleSentence,
                        "exampleTranslation" to word.exampleTranslation
                    ))
                }
                batch.commit().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
