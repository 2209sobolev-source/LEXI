package com.lexi.vocab.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lexi.vocab.data.local.dao.WordWithDefinitions

/**
 * A single flashcard. Shows the word on the front; tapping (isFlipped = true)
 * reveals the English definitions, Cambridge-style: part of speech, meaning, example.
 */
@Composable
fun FlashCard(
    word: WordWithDefinitions,
    isFlipped: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(320),
        label = "cardFlip"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (rotation <= 90f) {
            CardFront(word)
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardBack(word)
            }
        }
    }
}

@Composable
private fun CardFront(word: WordWithDefinitions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LevelBadge(word.word.level)
        androidx.compose.foundation.layout.Spacer(Modifier.size(16.dp))
        Text(
            text = word.word.word,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        if (word.word.ipa.isNotBlank()) {
            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
            Text(
                text = "/${word.word.ipa}/",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        androidx.compose.foundation.layout.Spacer(Modifier.size(24.dp))
        Text(
            text = "Нажмите, чтобы увидеть значение",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun CardBack(word: WordWithDefinitions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = word.word.word,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
        androidx.compose.foundation.layout.Spacer(Modifier.size(16.dp))
        word.definitions.sortedBy { it.senseOrder }.forEach { def ->
            Row_PartOfSpeech(def.partOfSpeech)
            Text(
                text = def.definition,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
            if (def.example.isNotBlank()) {
                Text(
                    text = "\u201C${def.example}\u201D",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            androidx.compose.foundation.layout.Spacer(Modifier.size(14.dp))
        }
    }
}

@Composable
private fun Row_PartOfSpeech(partOfSpeech: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = partOfSpeech,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun LevelBadge(level: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = level,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
