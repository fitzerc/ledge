package com.github.fitzerc.ledge.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AutoSuggestTextField(
    label: String,
    suggestionsStateFlow: StateFlow<List<String>>,
    suggestionUpdateRequested: (currentText: String) -> Unit,
    initialValue: String? = null,
    onValueChange: (newText: String) -> Unit
) {
    var currentText by remember { mutableStateOf(TextFieldValue(initialValue ?: "")) }
    val selectedSuggestion = remember { mutableStateOf<String?>(null) }

    val suggestions: List<String> by suggestionsStateFlow.collectAsState()

    TextField(
        value = currentText,
        onValueChange = { newText ->
            currentText = newText
            if (
                selectedSuggestion.value == null ||
                selectedSuggestion.value != newText.text
            ) {
                suggestionUpdateRequested(currentText.text)
            } else {
                suggestionUpdateRequested("")
            }

            onValueChange(currentText.text)
        },
        label = { Text(label) }
    )

    AnimatedVisibility(
        visible = suggestions.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxWidth(),
            //.width(textFieldSize.width.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            LazyColumn(modifier = Modifier
                .heightIn(max = 150.dp)
                .fillMaxWidth(),
                content = {
                    items(suggestions) { suggestion ->
                        Text(
                            text = suggestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    currentText = TextFieldValue(suggestion)
                                    selectedSuggestion.value = suggestion
                                    suggestionUpdateRequested("")
                                    onValueChange(currentText.text)
                                }
                        )
                    }
                }
            )
        }
    }
}