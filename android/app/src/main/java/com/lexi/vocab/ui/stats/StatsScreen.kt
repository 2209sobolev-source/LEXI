package com.lexi.vocab.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.lexi.vocab.data.repository.VocabRepository
import com.lexi.vocab.util.ViewModelFactory

@Composable
fun StatsScreen(repository: VocabRepository) {
    val viewModel: StatsViewModel = viewModel(factory = ViewModelFactory(repository))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = "Статистика",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "${state.currentStreak} ${dayWord(state.currentStreak)} подряд",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Так держать!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatBlock(modifier = Modifier.weight(1f), label = "Изучено слов", value = state.learnedCount)
            StatBlock(modifier = Modifier.weight(1f), label = "Осталось новых", value = state.newCount)
        }
    }
}

@Composable
private fun StatBlock(modifier: Modifier = Modifier, label: String, value: Int) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun dayWord(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod100 in 11..14 -> "дней"
        mod10 == 1 -> "день"
        mod10 in 2..4 -> "дня"
        else -> "дней"
    }
}
