package com.lexi.vocab.ui.review

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lexi.vocab.data.model.ReviewRating
import com.lexi.vocab.data.repository.VocabRepository
import com.lexi.vocab.ui.components.FlashCard
import com.lexi.vocab.ui.theme.RatingAgain
import com.lexi.vocab.ui.theme.RatingEasy
import com.lexi.vocab.ui.theme.RatingGood
import com.lexi.vocab.ui.theme.RatingHard
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ReviewScreen(repository: VocabRepository) {
    val viewModel: ReviewViewModel = viewModel(
        factory = com.lexi.vocab.util.ViewModelFactory(repository)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            state.isEmpty -> EmptyState(text = "В словаре пока нет слов")
            state.isFinished -> EmptyState(text = "Отлично, на сегодня всё повторено!")
            else -> {
                val word = state.currentWord
                if (word != null) {
                    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                        Text(
                            text = "Осталось: ${state.remaining}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val offsetX = remember(word.word.id) { Animatable(0f) }
                        val scope = rememberCoroutineScope()
                        var dragAccum by remember(word.word.id) { mutableFloatStateOf(0f) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .aspectRatio(0.8f)
                                .align(Alignment.CenterHorizontally)
                                .graphicsLayer {
                                    translationX = offsetX.value
                                    rotationZ = (offsetX.value / 40f).coerceIn(-12f, 12f)
                                }
                                .pointerInput(word.word.id, state.isFlipped) {
                                    if (!state.isFlipped) return@pointerInput
                                    detectHorizontalDragGestures(
                                        onDragEnd = {
                                            val threshold = 260f
                                            when {
                                                dragAccum > threshold -> {
                                                    scope.launch {
                                                        offsetX.animateTo(1200f)
                                                        viewModel.rate(ReviewRating.GOOD)
                                                    }
                                                }
                                                dragAccum < -threshold -> {
                                                    scope.launch {
                                                        offsetX.animateTo(-1200f)
                                                        viewModel.rate(ReviewRating.AGAIN)
                                                    }
                                                }
                                                else -> {
                                                    scope.launch { offsetX.animateTo(0f) }
                                                }
                                            }
                                            dragAccum = 0f
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        dragAccum += dragAmount
                                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                                    }
                                }
                        ) {
                            FlashCard(
                                word = word,
                                isFlipped = state.isFlipped,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .then(
                                        if (!state.isFlipped) Modifier.pointerInputTap { viewModel.flip() }
                                        else Modifier
                                    )
                            )
                        }

                        if (state.isFlipped) {
                            Text(
                                text = "Свайп влево = не помню, вправо = помню",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 8.dp)
                            )
                            RatingButtonsRow(onRate = viewModel::rate)
                        } else {
                            androidx.compose.foundation.layout.Spacer(Modifier.padding(28.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingButtonsRow(onRate: (ReviewRating) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RatingButton("Не помню", RatingAgain, Modifier.weight(1f)) { onRate(ReviewRating.AGAIN) }
            RatingButton("Сложно", RatingHard, Modifier.weight(1f)) { onRate(ReviewRating.HARD) }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RatingButton("Помню", RatingGood, Modifier.weight(1f)) { onRate(ReviewRating.GOOD) }
            RatingButton("Легко", RatingEasy, Modifier.weight(1f)) { onRate(ReviewRating.EASY) }
        }
    }
}

@Composable
private fun RatingButton(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = color),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(label, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmptyState(text: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

/** Simple tap detector that doesn't interfere with the sibling drag gesture. */
private fun Modifier.pointerInputTap(onTap: () -> Unit): Modifier = this.pointerInput(onTap) {
    androidx.compose.foundation.gestures.detectTapGestures(onTap = { onTap() })
}
