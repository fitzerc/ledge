package com.github.fitzerc.ledge.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.ToastError
import com.github.fitzerc.ledge.ui.components.BookCard
import com.github.fitzerc.ledge.ui.dialogs.AddAuthorDialog
import com.github.fitzerc.ledge.ui.dialogs.AddBookDialog
import com.github.fitzerc.ledge.ui.models.BookUiModel
import com.github.fitzerc.ledge.ui.models.navparams.BookNavParam
import com.github.fitzerc.ledge.ui.models.navparams.SearchNavParam
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.AddAuthorDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.AddAuthorViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.AddBookDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.AddBookViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.HomeScreenViewModel
import com.github.fitzerc.ledge.ui.viewmodels.HomeScreenViewModelFactory
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    ledgeDb: LedgeDatabase,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val vm: HomeScreenViewModel = viewModel(factory = HomeScreenViewModelFactory(
        ledgeDb = ledgeDb,
        toastError = { m -> ToastError(m, context, coroutineScope) }
    ))

    val addAuthorDialogVm: AddAuthorDialogViewModel = viewModel(factory = AddAuthorViewModelFactory(ledgeDb))
    val addBookDialogVm: AddBookDialogViewModel = viewModel(factory = AddBookViewModelFactory(ledgeDb))

    var searchQuery: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val count = vm.bookCount
    var showDialog by remember { mutableStateOf(false) }
    var showAddAuthorDialog by remember { mutableStateOf(false) }
    //TODO: how to do this better
    var submittedBookUiModel: BookUiModel? = null

    val recentBooks by vm.recentBooks.collectAsState()
    val currentlyReading by vm.currentlyReading.collectAsState()

    val authors by vm.authors.collectAsState()

    var authorFullName: String? = null

    Scaffold(
        topBar = {
            TopSearchBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery },
                onSubmit = { query ->
                    navController.navigate(SearchNavParam(query))
                })
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
            Text(text = "Recently Added")
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentBooks) { book ->
                    BookCard(book = book, onClick = {
                        navController.navigate(BookNavParam(book.book.bookId))
                    })
                }
            }

            Text(text = "Currently Reading")
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentlyReading) { book ->
                    BookCard(book = book, onClick = {
                        navController.navigate(BookNavParam(book.book.bookId))
                    })
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
                        submittedBookUiModel = BookUiModel(
                            b.title, b.author, b.readStatus, b.genre, b.bookFormat
                        )
                    } else {
                        vm.saveBookWithAuthorCheck(b)
                    }
                })
        }

        if (showAddAuthorDialog) {
            AddAuthorDialog(
                authorFullName = authorFullName,
                vm = addAuthorDialogVm,
                onDismiss = {
                    showAddAuthorDialog = false
                    authorFullName = null },
                onSubmit = { a ->
                    vm.saveAuthor(a)
                    if (submittedBookUiModel == null) {
                        ToastError(
                            "somehow the book was lost - only author was saved",
                            context,
                            coroutineScope
                        )
                    }
                    else {
                        vm.saveBookWithAuthorCheck(submittedBookUiModel!!)
                        submittedBookUiModel = null
                    }
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(searchQuery: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit, onSubmit: (String) -> Unit) {
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
                onClick = { onSubmit(searchQuery.text) },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(56.dp),
            ) { Icon(Icons.Default.Search, "Search Button") }
        }
    })
}