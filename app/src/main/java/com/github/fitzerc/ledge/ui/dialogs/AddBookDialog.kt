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
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.ui.models.BookUiModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.AddBookDialogViewModel

@Composable
fun AddBookDialog(
    vm: AddBookDialogViewModel,
    onDismiss: () -> Unit,
    onSubmit: (BookUiModel) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var author by remember { mutableStateOf(TextFieldValue("")) }

    val genres by vm.genres.collectAsState()
    val readStatuses by vm.readStatuses.collectAsState()
    val bookFormats by vm.bookFormats.collectAsState()

    var genresExpanded by remember { mutableStateOf(false) }
    var readStatusesExpanded by remember { mutableStateOf(false) }
    var bookFormatsExpanded by remember { mutableStateOf(false) }

    var isSubmitEnabled by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    var selectedGenre by remember { mutableStateOf<Genre?>(null) }
    var selectedReadStatus by remember { mutableStateOf<ReadStatus?>(null) }
    var selectedBookFormat by remember { mutableStateOf<BookFormat?>(null) }

    fun isFormValid(): Boolean {
        return selectedReadStatus != null &&
                selectedGenre != null &&
                selectedBookFormat != null &&
                title.text.trim().isNotEmpty() &&
                author.text.trim().isNotEmpty()
    }

    Dialog(
        onDismissRequest =
        onDismiss, properties = DialogProperties(dismissOnClickOutside = true)
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
                    text = "Book Details",
                    style = MaterialTheme.typography.headlineMedium
                )

                TextField(
                    value = title,
                    onValueChange = { newTitle ->
                        title = newTitle
                        isSubmitEnabled = isFormValid()
                    },
                    label = { Text("Title") })

                TextField(
                    value = author,
                    onValueChange = { authorName ->
                        author = authorName
                        isSubmitEnabled = isFormValid()
                    },
                    label = { Text("Author") })

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { genresExpanded = true }) {
                        Text(selectedGenre?.name ?: "Select Genre")
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

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { readStatusesExpanded = true }) {
                        Text(selectedReadStatus?.value ?: "Select Read Status")
                    }
                    DropdownMenu(
                        expanded = readStatusesExpanded,
                        onDismissRequest = { readStatusesExpanded = false }
                    ) {
                        readStatuses.forEach { readStatus ->
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    selectedReadStatus = readStatus
                                    isSubmitEnabled = isFormValid()
                                    readStatusesExpanded = false
                                },
                                text = { Text(readStatus.value) }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { bookFormatsExpanded = true }) {
                        Text(selectedBookFormat?.format ?: "Select Format")
                    }
                    DropdownMenu(
                        expanded = bookFormatsExpanded,
                        onDismissRequest = { bookFormatsExpanded = false }
                    ) {
                        bookFormats.forEach { bookFormat ->
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    selectedBookFormat = bookFormat
                                    isSubmitEnabled = isFormValid()
                                    bookFormatsExpanded = false
                                },
                                text = { Text(bookFormat.format) }
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
                        val readStatus = selectedReadStatus
                        val bookFormat = selectedBookFormat

                        if (readStatus != null && genre != null && bookFormat != null) {
                            val book = BookUiModel(
                                title.text,
                                author.text,
                                readStatus,
                                genre,
                                bookFormat
                            )

                            onSubmit(book)
                            onDismiss()
                        }
                    })
                    {
                        Text("Submit")
                    }
                }
            }

        }
    }
}