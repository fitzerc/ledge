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
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.data.models.BookAndRelations
import com.github.fitzerc.ledge.ui.dialogs.AddAuthorDialog
import com.github.fitzerc.ledge.ui.dialogs.AddBookDialog
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

    val addAuthorDialogVm: AddAuthorDialogViewModel = viewModel(factory = AddAuthorViewModelFactory(ledgeDb))
    val addBookDialogVm: AddBookDialogViewModel = viewModel(factory = AddBookViewModelFactory(ledgeDb))

    var searchQuery: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val count = vm.bookCount
    var showDialog by remember { mutableStateOf(false) }
    var showAddAuthorDialog by remember { mutableStateOf(false) }
    //TODO: how to do this better
    var bookUiModel by remember { mutableStateOf<BookUiModel?>(null) }

    val books by vm.books.collectAsState()

    val currentAuthor = vm.author.collectAsState()

    val authors by vm.authors.collectAsState()

    var authorFullName: String? = null

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
        Column(modifier = Modifier.padding(paddingInner)) {
            Text(
                text = "Book Count..." + count.collectAsState().value,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                vm = addBookDialogVm,
                onDismiss = { showDialog = false },
                onSubmit = { b ->
                    if (authors.none { a ->
                            b.author.lowercase(Locale.ROOT) == a.author.fullName.lowercase(
                                Locale.ROOT
                            )
                        }) {
                        authorFullName = b.author
                        showAddAuthorDialog = true
                    }

                    vm.saveBookWithAuthorCheck(b)
                })
        }

        if (showAddAuthorDialog) {
            AddAuthorDialog(
                authorFullName = authorFullName,
                vm = addAuthorDialogVm,
                onDismiss = {
                    showAddAuthorDialog = false
                    authorFullName = null },
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