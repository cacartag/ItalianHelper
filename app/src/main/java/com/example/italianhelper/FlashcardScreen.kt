package com.example.italianhelper

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.italianhelper.data.Flashcard
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun FlashcardScreen(
    cards: List<Flashcard>,
    categoryName: String,
    modifier: Modifier = Modifier,
    onReview: (Flashcard, Int) -> Unit,
    onBack: () -> Unit
) {
    var showAnswer by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Initialize TTS
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale.ITALIAN
            }
        }
        ttsInstance
    }

    // Clean up TTS when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) {
            Text("< Back to Categories")
        }

        if (cards.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("All caught up in $categoryName! Check back later.")
            }
        } else {
            val currentCard = cards.first()
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = categoryName, fontSize = 20.sp, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clickable { showAnswer = !showAnswer },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (showAnswer) currentCard.english else currentCard.italian,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (showAnswer && currentCard.exampleSentence.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(modifier = Modifier.padding(horizontal = 24.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentCard.exampleSentence,
                                        fontSize = 18.sp,
                                        fontStyle = FontStyle.Italic,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = currentCard.exampleTranslation,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                IconButton(onClick = { 
                                    tts?.speak(currentCard.exampleSentence, TextToSpeech.QUEUE_FLUSH, null, null)
                                }) {
                                    Text("🔊", fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (showAnswer) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { onReview(currentCard, 1); showAnswer = false }) { Text("Again") }
                        Button(onClick = { onReview(currentCard, 3); showAnswer = false }) { Text("Good") }
                        Button(onClick = { onReview(currentCard, 5); showAnswer = false }) { Text("Easy") }
                    }
                } else {
                    Text("Tap card to show answer", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                }
                
                Spacer(modifier = Modifier.height(64.dp))
                Text("Cards remaining: ${cards.size}")
            }
        }
    }
}
