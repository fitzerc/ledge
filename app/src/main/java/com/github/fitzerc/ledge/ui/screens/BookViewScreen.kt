package com.github.fitzerc.ledge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditAuthorDialog
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditFormatDialog
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditGenreDialog
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditLocationDialog
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditRatingDialog
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditReadStatusDialog
import com.github.fitzerc.ledge.ui.dialogs.editbook.EditTitleDialog
import com.github.fitzerc.ledge.ui.models.navparams.BookNavParam
import com.github.fitzerc.ledge.ui.viewmodels.BookViewScreenViewModel
import com.github.fitzerc.ledge.ui.viewmodels.BookViewScreenViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditFormatDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditFormatDialogViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditGenreDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditGenreDialogViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditReadStatusDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook.EditReadStatusDialogViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookViewScreen(
    bookNavParam: BookNavParam,
    ledgeDb: LedgeDatabase,
    navController: NavController
) {
    var showEditTitleDialog by remember { mutableStateOf(false) }
    var showEditAuthorDialog by remember { mutableStateOf(false) }
    var showEditRatingDialog by remember { mutableStateOf(false) }
    var showEditGenreDialog by remember { mutableStateOf(false) }
    var showEditFormatDialog by remember { mutableStateOf(false) }
    var showEditStatusDialog by remember { mutableStateOf(false) }
    var showEditLocationDialog by remember { mutableStateOf(false) }

    var showInfoPopup by remember { mutableStateOf(false) }

    if (bookNavParam.bookId == null) {
        throw Exception("Book Nav Param Empty!!")
    }

    val vm: BookViewScreenViewModel = viewModel(
        factory = BookViewScreenViewModelFactory(ledgeDb, bookNavParam.bookId)
    )
    val book by vm.book.collectAsState()

    val editGenreDialogViewModel: EditGenreDialogViewModel = viewModel(
        factory = EditGenreDialogViewModelFactory(ledgeDb)
    )
    val editFormatDialogViewModel: EditFormatDialogViewModel = viewModel(
        factory = EditFormatDialogViewModelFactory(ledgeDb)
    )
    val editReadStatusDialogViewModel: EditReadStatusDialogViewModel = viewModel(
        factory = EditReadStatusDialogViewModelFactory(ledgeDb)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back Button")
                    }
                    Button(onClick = {
                        book?.book?.let {
                            vm.deleteBook(it)
                            navController.navigate("home")
                        }
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
                    onEditClick = { showEditTitleDialog = true })
                BookDetailRow(
                    label = "By",
                    value = book?.author?.fullName ?: "N/A",
                    onEditClick = { showEditAuthorDialog = true }
                )
                BookDetailRow(
                    label = "Rating",
                    value = book?.book?.rating?.toString() ?: "N/A",
                    onEditClick = { showEditRatingDialog = true }
                )
                BookDetailRow(
                    label = "Format",
                    value = book?.bookFormat?.format ?: "N/A",
                    onEditClick = { showEditFormatDialog = true }
                )
                BookDetailRow(
                    label = "Read Status",
                    value = book?.readStatus?.value ?: "N/A",
                    onEditClick = { showEditStatusDialog = true }
                )
                BookDetailRow(
                    label = "Genre",
                    value = book?.genre?.name ?: "N/A",
                    onEditClick = { showEditGenreDialog = true }
                )
                BookDetailRow(
                    label = "Location",
                    value = book?.book?.location ?: "N/A",
                    onEditClick = { showEditLocationDialog = true },
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            onClick = { showInfoPopup = true }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Info Button"
                            )
                        }
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
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(color = Color.White)
                                    .border(width = 1.dp, color = Color.White)
                            ) {
                                BasicText(
                                    text = "Click a field to make a change",
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }

                if (showEditTitleDialog) {
                    EditTitleDialog(
                        title = book?.book?.title,
                        onDismiss = { showEditTitleDialog = false },
                        onSubmit = { newtitle ->
                            if (newtitle.isNotEmpty()) {
                                book?.book?.copy(title = newtitle)?.let { vm.updateBook(it) }
                                    ?: println("copy failed on title save") //TODO: toast?

                                vm.refreshBook(book?.book?.bookId!!)
                            } else {
                                //TODO: toast - new title cannot be empty
                            }
                        }
                    )
                }

                if (showEditAuthorDialog) {
                    EditAuthorDialog(
                        author = book?.author,
                        onDismiss = { showEditAuthorDialog = false },
                        onSubmit = { aFullName ->
                            if (book == null) {
                                throw Exception("book cannot be null!!")
                            }

                            vm.updateBookAuthor(book?.book!!, aFullName)
                            vm.refreshBook(book?.book?.bookId!!)
                        })
                }

                if (showEditRatingDialog) {
                    EditRatingDialog(
                        rating = book?.book?.rating ?: 0,
                        onDismiss = { showEditRatingDialog = false },
                        onSubmit = { rating ->
                            book?.book?.copy(rating = rating)?.let { vm.updateBook(it) }
                                ?: println("copy failed on rating save") //TODO: toast?

                            vm.refreshBook(book?.book?.bookId!!)
                        }
                    )
                }

                if (showEditFormatDialog) {
                    EditFormatDialog(
                        currentFormat = book?.bookFormat,
                        vm = editFormatDialogViewModel,
                        onDismiss = { showEditFormatDialog = false },
                        onSubmit = { format ->
                            book?.book?.copy(bookFormatId = format.bookFormatId)?.let { vm.updateBook(it) }
                                ?: println("copy failed on book format save") //TODO: toast?

                            vm.refreshBook(book?.book?.bookId!!)
                        }
                    )
                }

                if (showEditStatusDialog) {
                    EditReadStatusDialog(
                        currentStatus = book?.readStatus,
                        vm = editReadStatusDialogViewModel,
                        onDismiss = { showEditStatusDialog = false },
                        onSubmit = { status ->
                            book?.book?.copy(readStatusId = status.readStatusId)?.let { vm.updateBook(it) }
                                ?: println("copy failed on read status save") //TODO: toast?

                            vm.refreshBook(book?.book?.bookId!!)
                        }
                    )
                }

                if (showEditGenreDialog) {
                    EditGenreDialog(
                        currentGenre = book?.genre,
                        vm = editGenreDialogViewModel,
                        onDismiss = { showEditGenreDialog = false },
                        onSubmit = { genre ->
                            book?.book?.copy(genreId = genre.genreId)?.let { vm.updateBook(it) }
                                ?: println("copy failed on rating save")  //TODO: toast?

                            vm.refreshBook(book?.book?.bookId!!)
                        }
                    )
                }

                if (showEditLocationDialog) {
                    EditLocationDialog(
                        location = book?.book?.location,
                        onDismiss = { showEditLocationDialog = false },
                        onSubmit = { newLocation ->
                            book?.book?.copy(location = newLocation)?.let { vm.updateBook(it) }
                                ?: println("copy failed on location save") //TODO: toast?

                            vm.refreshBook(book?.book?.bookId!!)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookTitleRow(title: String, onEditClick: () -> Unit) {
    var isEditable by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .clickable { isEditable = !isEditable },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(.3f)) {
                Text(
                    text = "Title:",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Column(modifier = Modifier.weight(.7f)) {
                if (isEditable) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Button(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Field"
                            )
                        }
                    }
                } else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun BookDetailRow(
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
                modifier = Modifier.weight(.33f)
            ) {
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column(modifier = Modifier.weight(.67f)) {
                if (isEditable) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Button(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Field"
                            )
                        }
                    }
                } else {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}