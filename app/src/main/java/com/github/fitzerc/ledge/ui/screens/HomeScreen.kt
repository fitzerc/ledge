package com.github.fitzerc.ledge.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.data.models.BookAndRelations
import com.github.fitzerc.ledge.ui.models.BookUiModel
import com.github.fitzerc.ledge.ui.viewmodels.AddAuthorDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.AddAuthorViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.AddBookDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.AddBookViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.HomeScreenViewModel
import com.github.fitzerc.ledge.ui.viewmodels.HomeScreenViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.Locale

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    ledgeDb: LedgeDatabase,
    innerPadding: PaddingValues
) {

    val vm: HomeScreenViewModel = viewModel(factory = HomeScreenViewModelFactory(ledgeDb))
    var searchQuery: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var count = vm.bookCount
    var showDialog by remember { mutableStateOf(false) }
    var showAddAuthorDialog by remember { mutableStateOf(false) }
    //TODO: how to do this better
    var bookUiModel by remember { mutableStateOf<BookUiModel?>(null) }

    val books by vm.books.collectAsState()

    val currentAuthor = vm.author.collectAsState()

    val authors by vm.authors.collectAsState()

    Scaffold(
        topBar = {
            TopSearchBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = RectangleShape,
                modifier = Modifier.padding(innerPadding)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { paddingInner ->
        Column {
            Text(
                text = "Book Count..." + count.collectAsState().value,
                modifier = Modifier.padding(paddingInner)
            )
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookItem(book = book)
                }
            }
        }

        if (showDialog) {
            AddBookDialog(
                ledgeDb,
                onDismiss = { showDialog = false },
                onSubmit = { b ->

                    if (authors.none { a ->
                            b.author.lowercase(Locale.ROOT) == a.author.fullName.lowercase(
                                Locale.ROOT
                            )
                        }) {
                        showAddAuthorDialog = true
                    }

                    vm.saveBookWithAuthorCheck(b)
                })
        }

        if (showAddAuthorDialog) {
            AddAuthorDialog(
                ledgeDb = ledgeDb,
                onDismiss = { showAddAuthorDialog = false },
                onSubmit = { a -> vm.saveAuthor(a) })
        }
    }
}

@Composable
fun BookItem(book: BookAndRelations) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = book.book.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = "Author: ${book.author.fullName}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(searchQuery: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit) {
    TopAppBar(title = {
        Row {
            TextField(
                value = searchQuery,
                onValueChange = { text -> onQueryChange(text) },
                placeholder = { Text("Search...") },
                modifier = Modifier.height(56.dp)
            )
            Button(
                shape = RectangleShape,
                onClick = { /* Handle search request */ },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(56.dp),
            ) { Icon(Icons.Default.Search, "Search Button") }
        }
    })
}

@Composable
fun AddAuthorDialog(
    ledgeDb: LedgeDatabase,
    onDismiss: () -> Unit,
    onSubmit: (Author) -> Unit
) {
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var selectedGenre by remember { mutableStateOf<Genre?>(null) }
    var isSubmitEnabled by remember { mutableStateOf(false) }

    val vm: AddAuthorDialogViewModel = viewModel(factory = AddAuthorViewModelFactory(ledgeDb))

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

@Composable
fun AddBookDialog(
    ledgeDb: LedgeDatabase,
    onDismiss: () -> Unit,
    onSubmit: (BookUiModel) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var author by remember { mutableStateOf(TextFieldValue("")) }

    val vm: AddBookDialogViewModel = viewModel(factory = AddBookViewModelFactory(ledgeDb))

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