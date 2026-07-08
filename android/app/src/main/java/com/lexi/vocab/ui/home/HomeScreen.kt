package com.lexi.vocab.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lexi.vocab.util.ViewModelFactory
import com.lexi.vocab.data.repository.VocabRepository

@Composable
fun HomeScreen(
    repository: VocabRepository,
    onStartReview: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = ViewModelFactory(repository))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Привет!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Продолжим изучать английский",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        if (state.isSeeding) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "К повторению сегодня",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${state.dueCount}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = onStartReview,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.dueCount > 0
                ) {
                    Text(if (state.dueCount > 0) "Начать повторение" else "Пока нечего повторять")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatMiniCard(
                modifier = Modifier.weight(1f),
                label = "Новые слова",
                value = state.newCount.toString()
            )
            StatMiniCard(
                modifier = Modifier.weight(1f),
                label = "Изучено",
                value = state.learnedCount.toString()
            )
        }
    }
}

@Composable
private fun StatMiniCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
