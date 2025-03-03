package com.github.fitzerc.ledge.ui.dialogs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
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
import com.github.fitzerc.ledge.ui.models.SearchFilter
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.SearchFilterDialogViewModel

@Composable
fun SearchFilterDialog(
    vm: SearchFilterDialogViewModel,
    searchFilter: SearchFilter,
    onDismiss: () -> Unit,
    onSubmit: (SearchFilter) -> Unit
) {
    val isSubmitEnabled by remember { mutableStateOf(true) }

    var selectedGenres by remember { mutableStateOf(searchFilter.genres ?: emptyList()) }
    var selectedReadStatuses by remember { mutableStateOf(searchFilter.readStatuses ?: emptyList()) }
    var selectedBookFormats by remember { mutableStateOf(searchFilter.bookFormats ?: emptyList()) }
    var selectedSeries by remember { mutableStateOf(searchFilter.series ?: emptyList()) }

    val genres by vm.genres.collectAsState()
    val readStatuses by vm.readStatuses.collectAsState()
    val bookFormats by vm.bookFormats.collectAsState()
    val series by vm.series.collectAsState()

    var genresExpanded by remember { mutableStateOf(false) }
    var readStatusesExpanded by remember { mutableStateOf(false) }
    var bookFormatsExpanded by remember { mutableStateOf(false) }
    var seriesExpanded by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

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
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth())
                {
                    Text("Filter")

                    TextButton(
                        modifier = Modifier.padding(top = 0.dp),
                        enabled = (selectedGenres.any() || selectedBookFormats.any() || selectedReadStatuses.any()),
                        onClick = {
                            selectedGenres = emptyList()
                            selectedBookFormats = emptyList()
                            selectedReadStatuses = emptyList()
                            selectedSeries = emptyList()
                        }
                    ) {
                        Text("Clear")
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { genresExpanded = true }) {
                        Text(
                            if (selectedGenres.isEmpty()) "By Genre"
                            else "By Genre (${selectedGenres.count()})"
                        )
                    }
                    DropdownMenu(
                        expanded = genresExpanded,
                        onDismissRequest = { genresExpanded = false }
                    ) {
                        genres.forEach { genre ->
                            val isSelected = selectedGenres.contains(genre)

                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    selectedGenres = if (isSelected) {
                                        selectedGenres - genre
                                    } else {
                                        selectedGenres + genre
                                    }
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(genre.name)
                                    }
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { readStatusesExpanded = true }) {
                        Text(
                            if (selectedReadStatuses.isEmpty()) "By Read Status"
                            else "By Read Status (${selectedReadStatuses.count()})"
                        )
                    }
                    DropdownMenu(
                        expanded = readStatusesExpanded,
                        onDismissRequest = { readStatusesExpanded = false }
                    ) {
                        readStatuses.forEach { readStatus ->
                            val isSelected = selectedReadStatuses.contains(readStatus)
                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    selectedReadStatuses = if (isSelected) {
                                        selectedReadStatuses - readStatus
                                    } else {
                                        selectedReadStatuses + readStatus
                                    }
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(readStatus.value)
                                    }
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { seriesExpanded = true }) {
                        Text(
                            if (selectedSeries.isEmpty()) "By Series"
                            else "By Series (${selectedSeries.count()})"
                        )
                    }
                    DropdownMenu(
                        expanded = seriesExpanded,
                        onDismissRequest = { seriesExpanded = false }
                    ) {
                        series.forEach { s ->
                            val isSelected = selectedSeries.contains(s)

                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    selectedSeries = if (isSelected) {
                                        selectedSeries - s
                                    } else {
                                        selectedSeries + s
                                    }
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(s.seriesName)
                                    }
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { bookFormatsExpanded = true }) {
                        Text(
                            if (selectedBookFormats.isEmpty()) "By Format"
                            else "By Format (${selectedBookFormats.count()})"
                        )
                    }
                    DropdownMenu(
                        expanded = bookFormatsExpanded,
                        onDismissRequest = { bookFormatsExpanded = false }
                    ) {
                        bookFormats.forEach { bookFormat ->
                            val isSelected = selectedBookFormats.contains(bookFormat)

                            DropdownMenuItem(
                                interactionSource = interactionSource,
                                onClick = {
                                    selectedBookFormats = if (isSelected) {
                                        selectedBookFormats - bookFormat
                                    } else {
                                        selectedBookFormats + bookFormat
                                    }
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(bookFormat.format)
                                    }
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
                        onSubmit(SearchFilter(selectedGenres, selectedReadStatuses, selectedBookFormats, selectedSeries))
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