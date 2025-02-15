package com.github.fitzerc.ledge.ui.dialogs.editbook

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditFormatDialogViewModel

@Composable
fun EditFormatDialog(
    currentFormat: BookFormat?,
    vm: EditFormatDialogViewModel,
    onDismiss: () -> Unit,
    onSubmit: (newFormat: BookFormat) -> Unit
) {
    if (currentFormat == null) {
        onDismiss()
        return
    }

    val interactionSource = remember { MutableInteractionSource() }

    var formatsExpanded by remember { mutableStateOf(false) }
    var formatEdit by remember { mutableStateOf(currentFormat) }
    var isSubmitEnabled by remember { mutableStateOf(false) }

    val formats: List<BookFormat> by vm.formats.collectAsState()

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
                    text = "Edit Book Format",
                    style = MaterialTheme.typography.headlineMedium
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Book Format:")
                        TextButton(onClick = { formatsExpanded = true }) {
                            Text(formatEdit.format)
                        }
                    }
                    DropdownMenu(
                        expanded = formatsExpanded,
                        onDismissRequest = { formatsExpanded = false }
                    ) {
                        formats.forEach { format ->
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                text = { Text(text = format.format) },
                                onClick = {
                                    isSubmitEnabled = format != formatEdit
                                    formatEdit = format
                                    formatsExpanded = false
                                }
                            )
                        }
                    }
                }
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
                        onSubmit(formatEdit)
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