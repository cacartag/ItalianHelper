package com.example.italianhelper.data

import java.util.Calendar
import kotlin.math.max

fun calculateNextReview(flashcard: Flashcard, quality: Int): Flashcard {
    var repetitions = flashcard.repetitions
    var easinessFactor = flashcard.easinessFactor
    var interval = flashcard.interval

    if (quality >= 3) {
        if (repetitions == 0) {
            interval = 1
        } else if (repetitions == 1) {
            interval = 6
        } else {
            interval = (interval * easinessFactor).toInt()
        }
        repetitions++
    } else {
        repetitions = 0
        interval = 1
    }

    easinessFactor = easinessFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
    if (easinessFactor < 1.3f) easinessFactor = 1.3f

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, interval)
    val nextReview = calendar.timeInMillis

    return flashcard.copy(
        repetitions = repetitions,
        easinessFactor = easinessFactor,
        interval = interval,
        lastReview = System.currentTimeMillis(),
        nextReview = nextReview
    )
}
