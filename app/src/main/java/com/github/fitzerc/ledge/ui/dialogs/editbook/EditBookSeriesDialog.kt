package com.github.fitzerc.ledge.ui.dialogs.editbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.fitzerc.ledge.ui.components.AutoSuggestTextField
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditBookSeriesDialogViewModel

@Composable
fun EditBookSeriesDialog(
    seriesName: String?,
    vm: EditBookSeriesDialogViewModel,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    val filteredSeriesNames = vm.filteredSeriesNames

    var seriesNameEdit by remember { mutableStateOf(seriesName ?: "") }
    var isSubmitEnabled by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Edit Series",
                    style = MaterialTheme.typography.headlineMedium
                )

                AutoSuggestTextField(
                    label = "Series",
                    suggestionsStateFlow = filteredSeriesNames,
                    suggestionUpdateRequested = { t -> vm.filterSeries(t) },
                    initialValue = seriesNameEdit,
                    onValueChange = { newVal ->
                        isSubmitEnabled = when {
                            seriesNameEdit.isEmpty() -> false
                            seriesNameEdit == seriesName -> false
                            else -> true
                        }
                        seriesNameEdit = newVal
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }
                    TextButton(enabled = isSubmitEnabled, onClick = {
                        onSubmit(seriesNameEdit)
                        onDismiss()
                    })
                    {
                        Text("Save")
                    }
                }
            }
        }
    }
}