package com.github.fitzerc.ledge.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.models.AuthorAndGenre
import com.github.fitzerc.ledge.ui.dialogs.AddAuthorDialog
import com.github.fitzerc.ledge.ui.dialogs.EditAuthorDialog
import com.github.fitzerc.ledge.ui.toastError
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.AddAuthorDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.AddAuthorViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.EditAuthorDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.EditAuthorViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.screens.settings.ManageAuthorsScreenViewModel
import com.github.fitzerc.ledge.ui.viewmodels.screens.settings.ManageAuthorsScreenViewModelFactory

@Composable
fun ManageAuthorScreen(
    innerPadding: PaddingValues,
    ledgeDb: LedgeDatabase
) {
    val vm: ManageAuthorsScreenViewModel = viewModel(
        factory = ManageAuthorsScreenViewModelFactory(ledgeDb)
    )

    val addAuthorDialogVm: AddAuthorDialogViewModel = viewModel(factory = AddAuthorViewModelFactory(ledgeDb))
    val editAuthorDialogViewModel: EditAuthorDialogViewModel = viewModel(
        factory = EditAuthorViewModelFactory(ledgeDb)
    )

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val filteredAuthorsWithGenre by vm.filteredAuthorsWithGenre.collectAsState()

    var showAddAuthorDialog by remember { mutableStateOf(false) }
    var showEditAuthorDialog by remember { mutableStateOf(false) }
    var currentFilterValue: String by remember { mutableStateOf("") }

    var selectedAuthor by remember { mutableStateOf<Author?>(null)}

    var searchQuery: TextFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }

    Scaffold(
        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        topBar = {
            TopSearchAndFilterBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery },
                onSubmit = { query ->
                    currentFilterValue = query
                    vm.applyFilter(query)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAuthorDialog = true }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Series"
                )
            }
        }
    ) { paddingInner ->
        Column(modifier = Modifier.padding(paddingInner)) {
            Text(
                text = "Manage Series",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredAuthorsWithGenre) { a ->
                    Row {
                        AuthorCard(
                            author = a,
                            onClick = { author ->
                                selectedAuthor = author
                                showEditAuthorDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showAddAuthorDialog) {
            AddAuthorDialog(
                authorFullName = "",
                vm = addAuthorDialogVm,
                onDismiss = { showAddAuthorDialog = false },
                onSubmit = { a ->
                    try {
                        vm.addAuthor(a)
                    } catch (e: Exception) {
                        println(e.message)
                        toastError(
                            "unable to add author",
                            context,
                            coroutineScope
                        )
                    }
                    vm.applyFilter(currentFilterValue)
                }
            )
        }

        if (showEditAuthorDialog && selectedAuthor != null) {
            EditAuthorDialog(
                author = selectedAuthor!!,
                vm = editAuthorDialogViewModel,
                onDismiss = { showEditAuthorDialog = false },
                onSubmit = { updatedAuthor ->
                    try {
                        vm.updateAuthor(updatedAuthor)
                    }
                    catch (e: Exception) {
                        println(e.message)
                        toastError(
                            "unable to update author",
                            context,
                            coroutineScope
                        )
                    }

                    vm.applyFilter(currentFilterValue)
                }
            )
        }
    }
}

    @Composable
    fun AuthorCard(author: AuthorAndGenre, onClick: (Author) -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClick(author.author) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = author.author.fullName, style = MaterialTheme.typography.titleMedium)
                if (!author.genre?.name.isNullOrEmpty()) {
                    Text(
                        text = "Genre: ${author.genre?.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
