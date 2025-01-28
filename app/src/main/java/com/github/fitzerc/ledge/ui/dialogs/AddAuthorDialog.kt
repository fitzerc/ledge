package com.github.fitzerc.ledge.ui.dialogs

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.ui.viewmodels.AddAuthorDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.AddAuthorViewModelFactory

@Composable
fun AddAuthorDialog(
    vm: AddAuthorDialogViewModel,
    onDismiss: () -> Unit,
    onSubmit: (Author) -> Unit
) {
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var selectedGenre by remember { mutableStateOf<Genre?>(null) }
    var isSubmitEnabled by remember { mutableStateOf(false) }

    val genres by vm.genres.collectAsState()
    var genresExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    fun isFormValid(): Boolean = selectedGenre != null && fullName.text.trim().isNotEmpty()

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
                    text = "New Author",
                    style = MaterialTheme.typography.headlineMedium
                )

                TextField(
                    value = fullName,
                    onValueChange = { newName ->
                        fullName = newName
                        isSubmitEnabled = isFormValid()
                    },
                    label = { Text("Full Name") })

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { genresExpanded = true }) {
                        Text(selectedGenre?.name ?: "Select Genre (Optional)")
                    }
                    DropdownMenu(
                        expanded = genresExpanded,
                        onDismissRequest = { genresExpanded = false }
                    ) {
                        genres.forEach { genre ->
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    selectedGenre = genre
                                    isSubmitEnabled = isFormValid()
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
                        //Handle form submission onDismiss()
                        val genre = selectedGenre

                        val author = Author(
                            fullName = fullName.text,
                            typicalGenreId = genre?.genreId
                        )

                        onSubmit(author)
                        onDismiss()
                    })
                    {
                        Text("Submit")
                    }
                }
            }
        }
    }
}