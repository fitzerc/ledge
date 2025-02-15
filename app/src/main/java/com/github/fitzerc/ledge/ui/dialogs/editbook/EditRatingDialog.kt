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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun EditRatingDialog(
    rating: Int?,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int) -> Unit
) {
    if (rating == null) {
        onDismiss()
        return
    }

    val interactionSource = remember { MutableInteractionSource() }

    var ratingEdit by remember { mutableIntStateOf(rating) }
    var ratingsExpanded by remember { mutableStateOf(false) }

    var isSubmitEnabled by remember { mutableStateOf(false) }

    var options = 0..5


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
                    text = "Edit Rating",
                    style = MaterialTheme.typography.headlineMedium
                )

                Box(modifier = Modifier.fillMaxWidth()){
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Rating:")
                        TextButton(onClick = { ratingsExpanded = true }) {
                            Text( text = ratingEdit.toString())
                        }
                    }
                    DropdownMenu(
                        expanded = ratingsExpanded,
                        onDismissRequest = { ratingsExpanded = false }
                    ) {
                        options.forEach { rating ->
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                text = { Text(text = rating.toString()) },
                                onClick = {
                                    isSubmitEnabled = ratingEdit != rating
                                    ratingEdit = rating
                                    ratingsExpanded = false
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
                        onSubmit(ratingEdit)
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