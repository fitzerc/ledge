package com.github.fitzerc.ledge.ui.dialogs.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Series

@Composable
fun AddSeriesDialog(
    onDismiss: () -> Unit,
    onSubmit: (Series) -> Unit
){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        var seriesName by remember { mutableStateOf(TextFieldValue("")) }
        var isSubmitEnabled by remember { mutableStateOf(false) }

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
                    text = "Add Series",
                    style = MaterialTheme.typography.headlineMedium
                )

                TextField(
                    value = seriesName,
                    label = { Text("Series Name") },
                    onValueChange = { newSeriesName ->
                        seriesName = newSeriesName

                        isSubmitEnabled = when {
                            seriesName.text.isEmpty() -> {
                                false
                            }
                            else -> {
                                true
                            }
                        }
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
                    TextButton(
                        enabled = isSubmitEnabled,
                        onClick = {
                            onSubmit(Series(seriesName = seriesName.text))
                            onDismiss()
                        }
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}