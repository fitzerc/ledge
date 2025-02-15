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
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditGenreDialogViewModel

@Composable
fun EditGenreDialog(
    currentGenre: Genre?,
    vm: EditGenreDialogViewModel,
    onDismiss: () -> Unit,
    onSubmit: (newGenre: Genre) -> Unit
) {
    if (currentGenre == null) {
        onDismiss()
        return
    }

    val interactionSource = remember { MutableInteractionSource() }

    var genresExpanded by remember { mutableStateOf(false) }
    var genreEdit by remember { mutableStateOf(currentGenre) }
    var isSubmitEnabled by remember { mutableStateOf(false) }

    val genres: List<Genre> by vm.genres.collectAsState()

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
                    text = "Edit Genre",
                    style = MaterialTheme.typography.headlineMedium
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Genre:")
                        TextButton(onClick = { genresExpanded = true }) {
                            Text(genreEdit.name)
                        }
                    }
                    DropdownMenu(
                        expanded = genresExpanded,
                        onDismissRequest = { genresExpanded = false }
                    ) {
                        genres.forEach { genre ->
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    isSubmitEnabled = genreEdit != genre
                                    genreEdit = genre
                                    genresExpanded = false
                                },
                                text = { Text(genre.name) }
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
                        onSubmit(genreEdit)
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