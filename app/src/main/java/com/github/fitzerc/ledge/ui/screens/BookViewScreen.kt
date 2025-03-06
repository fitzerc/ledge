package com.github.fitzerc.ledge.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.toastError
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditBookSeriesDialog
import com.github.fitzerc.ledge.ui.models.navparams.BookNavParam
import com.github.fitzerc.ledge.ui.viewmodels.screens.BookViewScreenViewModel
import com.github.fitzerc.ledge.ui.viewmodels.screens.BookViewScreenViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditBookSeriesDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditBookSeriesDialogViewModelFactory
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookViewScreen(
    bookNavParam: BookNavParam,
    ledgeDb: LedgeDatabase,
    navController: NavController
) {
    var showEditSeriesDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }

    var showInfoPopup by remember { mutableStateOf(false) }

    if (bookNavParam.bookId == null) {
        toastError(
            "Book Id missing - unable to display book",
            context,
            coroutineScope
        )

        return
    }

    val vm: BookViewScreenViewModel = viewModel(
        factory = BookViewScreenViewModelFactory(
            ledgeDb = ledgeDb,
            bookId = bookNavParam.bookId,
            toastError = { toastError(it, context, coroutineScope) }
        )
    )
    val book by vm.book.collectAsState()

    val editBookSeriesViewModel: EditBookSeriesDialogViewModel = viewModel(
        factory = EditBookSeriesDialogViewModelFactory(ledgeDb)
    )

    val genres by vm.genres.collectAsState()
    val readStatuses by vm.readStatuses.collectAsState()
    val formats by vm.formats.collectAsState()
    val authors = vm.filteredAuthorNames
    val series = vm.filteredSeriesNames

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back Button")
                    }
                    Button(onClick = {
                        book?.book?.let {
                            vm.deleteBook(it)
                            navController.navigateUp()
                        } ?: toastError(
                            "Internal error related to state of book - unable to delete",
                            context,
                            coroutineScope
                        )
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Book Button")
                    }
                }
            })
        }
    ) { paddingInner ->
        Card(
            modifier = Modifier
                .padding(paddingInner)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 3.dp)
            ) {
                BookTitleRow(
                    title = book?.book?.title ?: "",
                    onValueChange = { newTitle ->
                        if (newTitle.isNotEmpty()) {
                            book?.book?.copy(title = newTitle)?.let { vm.updateBook(it) }
                                ?: {
                                    println("copy failed on title save")
                                    toastError(
                                        "copy failed - unable to update title",
                                        context,
                                        coroutineScope
                                    )
                                }

                            vm.refreshBook(book?.book?.bookId!!)
                        } else {
                            println("empty string on title save")
                            toastError(
                                "title is somehow empty - unable to update title",
                                context,
                                coroutineScope
                            )
                        }
                    }
                )
                BookDetailRow(
                    label = "By",
                    initialValue = book?.author?.fullName ?: "N/A",
                    suggestionsStateFlow = authors,
                    suggestionUpdateRequested = { vm.filterAuthors(it) },
                    onValueChange = { newAuthorName ->
                        book?.book?.let {
                            vm.updateBookAuthor(it, newAuthorName)
                            vm.refreshBook(it.bookId)
                            vm.filterAuthors("")
                        } ?: toastError(
                            "book is somehow missing - unable to update author",
                            context,
                            coroutineScope
                        )
                    }
                )
                BookRatingRow(
                    rating = book?.book?.rating ?: 0,
                    onChange = { rating ->
                        book?.book?.copy(rating = rating)?.let { vm.updateBook(it) }
                            ?: {
                                println("copy failed on rating save")
                                toastError(
                                    "copy failed - unable to update rating",
                                    context,
                                    coroutineScope
                                )
                            }

                        vm.refreshBook(book?.book?.bookId!!)
                    }
                )
                BookDetailRowWithDropdown(
                    label = "Format",
                    value = book?.bookFormat?.format ?: "N/A",
                    dropdownVals = formats.map { f -> f.format },
                    interactionSource = interactionSource,
                    onChange = { newVal ->
                        val selectedFormat = formats.find { f -> f.format == newVal }

                        selectedFormat?.let {
                            book?.book?.copy(bookFormatId = selectedFormat.bookFormatId)
                                ?.let { vm.updateBook(it) }
                                ?: {
                                    println("copy failed on bookFormat save")
                                    toastError(
                                        "copy failed - unable to update format",
                                        context,
                                        coroutineScope
                                    )
                                }
                        } ?: {
                            toastError(
                                "selected format somehow cannot be found - unable to update",
                                context,
                                coroutineScope
                            )
                        }

                        vm.refreshBook(book?.book?.bookId!!)
                    }
                )
                BookDetailRowWithDropdown(
                    label = "Read Status",
                    value = book?.readStatus?.value ?: "N/A",
                    dropdownVals = readStatuses.map { rs -> rs.value },
                    interactionSource = interactionSource,
                    onChange = { newVal ->
                        val selectedStatus = readStatuses.find { rs -> rs.value == newVal }

                        selectedStatus?.let {
                            book?.book?.copy(readStatusId = selectedStatus.readStatusId)
                                ?.let { vm.updateBook(it) }
                                ?: {
                                    println("copy failed on readStatus save")
                                    toastError(
                                        "copy failed - unable to update read status",
                                        context,
                                        coroutineScope
                                    )
                                }
                        } ?: {
                            toastError(
                                "selected read status somehow cannot be found - unable to update",
                                context,
                                coroutineScope
                            )
                        }

                        vm.refreshBook(book?.book?.bookId!!)
                    }
                )
                BookDetailRowWithDropdown(
                    label = "Genre",
                    value = book?.genre?.name ?: "N/A",
                    dropdownVals = genres.map { g -> g.name },
                    interactionSource = interactionSource,
                    onChange = { newVal ->
                        val selectedGenre = genres.find { g -> g.name == newVal }

                        selectedGenre?.let {
                            book?.book?.copy(genreId = selectedGenre.genreId)
                                ?.let { vm.updateBook(it) }
                                ?: {
                                    println("copy failed on genre save")
                                    toastError(
                                        "copy failed - unable to update genre",
                                        context,
                                        coroutineScope
                                    )
                                }
                        } ?: {
                            toastError(
                                "selected genre somehow cannot be found - unable to update",
                                context,
                                coroutineScope
                            )
                        }

                        vm.refreshBook(book?.book?.bookId!!)
                    }
                )
                BookDetailRow(
                    label = "Series",
                    initialValue = book?.partOfSeries?.seriesName ?: "N/A",
                    suggestionsStateFlow = series,
                    suggestionUpdateRequested = { vm.filterSeries(it) },
                    onValueChange = { newSeriesName ->
                        book?.book?.let {
                            vm.updateBookSeries(it, newSeriesName)
                            vm.refreshBook(it.bookId)
                            vm.filterSeries("")
                        } ?: toastError(
                            "book is somehow missing - unable to update series",
                            context,
                            coroutineScope
                        )
                    }
                )
                BookDetailRowWithTextField(
                    label = "Location",
                    value = book?.book?.location ?: "N/A",
                    onChange = { newLocation ->
                        book?.book?.copy(location = newLocation)?.let { vm.updateBook(it) }
                            ?: {
                                println("copy failed on location save")
                                toastError(
                                    "copy failed - unable to update location",
                                    context,
                                    coroutineScope
                                )
                            }

                        vm.refreshBook(book?.book?.bookId!!)
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info Button",
                            modifier = Modifier.clickable { showInfoPopup = true }
                        )
                    }
                }

                if (showInfoPopup) {
                    Popup(
                        alignment = Alignment.BottomEnd,
                        onDismissRequest = { showInfoPopup = false },
                        offset = IntOffset(-25, -120) // Adjust the offset as needed
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .background(color = MaterialTheme.colorScheme.primary)
                            ) {
                                BasicText(
                                    text = "Click a field to make a change.\n" +
                                            "When rating is outlined, tap star to update.",
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }

                if (showEditSeriesDialog) {
                    EditBookSeriesDialog(
                        seriesName = book?.partOfSeries?.seriesName,
                        vm = editBookSeriesViewModel,
                        onDismiss = { showEditSeriesDialog = false },
                        onSubmit = { updatedSeriesName ->
                            book?.book?.let {
                                try {
                                    vm.updateBookWithSeries(book?.book!!, updatedSeriesName)
                                } catch (e: Exception) {
                                    println(e.message)
                                    toastError(
                                        "unable to update series",
                                        context,
                                        coroutineScope
                                    )
                                }
                            } ?: {
                                toastError(
                                    "unable to update series",
                                    context,
                                    coroutineScope
                                )
                            }

                            vm.refreshBook(book?.book?.bookId!!)
                        })
                }
            }
        }
    }
}

@Composable
fun BookTitleRow(title: String, onValueChange: (String) -> Unit) {
    var isEditable by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .clickable { isEditable = !isEditable },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(.2f)) {
                Text(
                    text = "Title:",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Column(modifier = Modifier.weight(.8f)) {
                if (isEditable) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = title,
                                label = { Text("Title") },
                                onValueChange = { newTitle ->
                                    onValueChange(newTitle)
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
fun BookRatingRow(
    rating: Int,
    onChange: (Int) -> Unit
) {
    var isEditable by remember { mutableStateOf(false) }

    if (isEditable) {
        Row(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .clickable { isEditable = !isEditable }
                .border(width = 2.dp, color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                for (i in 1..5) {
                    if (i <= rating) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Filled Star",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    onChange(i)
                                    isEditable = false
                                },
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Outlined Star",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    onChange(i)
                                    isEditable = false
                                },
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .clickable { isEditable = !isEditable },
        ) {
            for (i in 1..5) {
                if (i <= rating) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Filled Star",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Outlined Star",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BookDetailRowWithDropdown(
    label: String,
    value: String,
    dropdownVals: List<String>,
    interactionSource: MutableInteractionSource,
    onChange: (String) -> Unit
) {
    var isEditable by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var valueEdit by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .clickable { isEditable = !isEditable },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(.25f)
            ) {
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column(modifier = Modifier.weight(.75f)) {
                if (isEditable) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = { dropdownExpanded = true }
                            ) {
                                Text(value)
                            }
                        }
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            dropdownVals.forEach { value ->
                                DropdownMenuItem(
                                    interactionSource = interactionSource,
                                    text = { Text(value) },
                                    onClick = {
                                        valueEdit = value
                                        dropdownExpanded = false
                                        onChange(value)
                                        isEditable = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun BookDetailRowWithTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    var isEditable by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .clickable { isEditable = !isEditable },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(.25f)
            ) {
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column(modifier = Modifier.weight(.75f)) {
                if (isEditable) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(.75f)) {
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            TextField(
                                value = value,
                                label = { Text(label) },
                                onValueChange = { newVal ->
                                    onChange(newVal)
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailRow(
    label: String,
    initialValue: String?,
    suggestionsStateFlow: StateFlow<List<String>>,
    suggestionUpdateRequested: (String) -> Unit,
    onValueChange: (String) -> Unit
) {
    var isEditable by remember { mutableStateOf(false) }
    var currentText by remember { mutableStateOf(TextFieldValue(initialValue ?: "")) }
    val selectedSuggestion = remember { mutableStateOf<String?>(null) }

    val suggestions: List<String> by suggestionsStateFlow.collectAsState()

    if (isEditable) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            //Same as AutoSuggestTextField except only trigger
            // onValueChange when existing item is clicked
            TextField(
                value = currentText,
                onValueChange = { newText ->
                    currentText = newText
                    if (
                        selectedSuggestion.value == null ||
                        selectedSuggestion.value != newText.text
                    ) {
                        suggestionUpdateRequested(currentText.text)
                    } else {
                        suggestionUpdateRequested("")
                    }
                },
                label = { Text(label) }
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = suggestions.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    LazyColumn(modifier = Modifier
                        .heightIn(max = 150.dp)
                        .fillMaxWidth(),
                        content = {
                            items(suggestions) { suggestion ->
                                Text(
                                    text = suggestion,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            currentText = TextFieldValue(suggestion)
                                            selectedSuggestion.value = suggestion
                                            suggestionUpdateRequested("")
                                            isEditable = false
                                            onValueChange(currentText.text)
                                        }
                                )
                            }
                        }
                    )
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .clickable { isEditable = !isEditable },
        ) {
            Row(
            ) {
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = initialValue ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BookDetailRow2(
    label: String,
    value: String,
    onEditClick: () -> Unit
) {
    var isEditable by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .clickable { isEditable = !isEditable },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(.25f)
            ) {
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column(modifier = Modifier.weight(.75f)) {
                if (isEditable) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(.75f)) {
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Column(modifier = Modifier.weight(.25f)) {
                                Button(onClick = onEditClick) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Field"
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}