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
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditReadStatusDialogViewModel

@Composable
fun EditReadStatusDialog(
    currentStatus: ReadStatus?,
    vm: EditReadStatusDialogViewModel,
    onDismiss: () -> Unit,
    onSubmit: (newStatus: ReadStatus) -> Unit
) {
    if (currentStatus == null) {
        onDismiss()
        return
    }

    val interactionSource = remember { MutableInteractionSource() }

    var statusesExpanded by remember { mutableStateOf(false) }
    var statusEdit by remember { mutableStateOf(currentStatus) }
    var isSubmitEnabled by remember { mutableStateOf(false) }

    val statuses: List<ReadStatus> by vm.readStatuses.collectAsState()

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
                    text = "Edit Read Status",
                    style = MaterialTheme.typography.headlineMedium
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Read Status:")
                        TextButton(onClick = { statusesExpanded = true }) {
                            Text(statusEdit.value)
                        }
                    }
                    DropdownMenu(
                        expanded = statusesExpanded,
                        onDismissRequest = { statusesExpanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                text = { Text(text = status.value) },
                                onClick = {
                                    isSubmitEnabled = status != statusEdit
                                    statusEdit = status
                                    statusesExpanded = false
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
                        onSubmit(statusEdit)
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