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
import com.github.fitzerc.ledge.data.entities.Series

@Composable
fun EditSeriesDialog(
    series: Series,
    onDismiss: () -> Unit,
    onSubmit: (Series) -> Unit
) {
    var seriesName by remember { mutableStateOf(TextFieldValue(series.seriesName)) }
    val orgSeries = series.copy()
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

                TextField(
                    value = seriesName,
                    label = { Text("Series Name") },
                    onValueChange = { newSeriesName ->
                        isSubmitEnabled = when {
                            newSeriesName.text.isEmpty() -> false
                            newSeriesName.text == orgSeries.seriesName -> false
                            else -> true
                        }

                        seriesName = newSeriesName
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
                            onSubmit(series.copy(seriesName = seriesName.text))
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