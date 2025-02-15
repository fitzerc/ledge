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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun EditLocationDialog(
    location: String?,
    onDismiss: () -> Unit,
    onSubmit: (newLocation: String?) -> Unit
) {
    var locationEdit by remember { mutableStateOf(location) }
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
                    text = "Edit Location",
                    style = MaterialTheme.typography.headlineMedium
                )

                TextField(
                    value = locationEdit ?: "",
                    onValueChange = { newLocation ->
                        isSubmitEnabled = locationEdit != newLocation
                        locationEdit = newLocation
                    },
                    label = { Text("Location") }
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
                        if (locationEdit != null && locationEdit?.length!! > 0) {
                            onSubmit(locationEdit)
                        } else {
                            onSubmit(null)
                        }

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