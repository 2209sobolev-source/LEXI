package com.lexi.vocab.ui.dictionary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lexi.vocab.data.local.dao.WordWithDefinitions
import com.lexi.vocab.data.repository.VocabRepository
import com.lexi.vocab.ui.components.LevelBadge
import com.lexi.vocab.util.ViewModelFactory

@Composable
fun DictionaryScreen(repository: VocabRepository) {
    val viewModel: DictionaryViewModel = viewModel(factory = ViewModelFactory(repository))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = "Словарь",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
        )
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Найти слово...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(state.results, key = { it.word.id }) { entry ->
                DictionaryRow(entry)
            }
        }
    }
}

@Composable
private fun DictionaryRow(entry: WordWithDefinitions) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                Text(entry.word.word, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                if (entry.word.ipa.isNotBlank()) {
                    Text(
                        "/${entry.word.ipa}/",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                LevelBadge(entry.word.level)
            }
            entry.definitions.sortedBy { it.senseOrder }.take(2).forEach { def ->
                Text(
                    text = "${def.partOfSpeech} — ${def.definition}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
