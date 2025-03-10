package com.github.fitzerc.ledge.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.components.BookCard
import com.github.fitzerc.ledge.ui.dialogs.SearchFilterDialog
import com.github.fitzerc.ledge.ui.models.SearchFilter
import com.github.fitzerc.ledge.ui.models.navparams.BookNavParam
import com.github.fitzerc.ledge.ui.models.navparams.SearchNavParam
import com.github.fitzerc.ledge.ui.viewmodels.screens.BooksScreenViewModel
import com.github.fitzerc.ledge.ui.viewmodels.screens.BooksScreenViewModelFactory
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.SearchFilterDialogViewModel
import com.github.fitzerc.ledge.ui.viewmodels.dialogs.SearchFilterDialogViewModelFactory

@Composable
fun BooksScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    searchNavParam: SearchNavParam,
    ledgeDb: LedgeDatabase) {

    val vm: BooksScreenViewModel = viewModel(factory = BooksScreenViewModelFactory(ledgeDb))
    val books by vm.searchResults.collectAsState()
    val ledgeStats by vm.ledgeStats.collectAsState()

    val searchFilterDialogVm: SearchFilterDialogViewModel = viewModel(factory = SearchFilterDialogViewModelFactory(ledgeDb))
    var currentFilterValue: SearchFilter by remember { mutableStateOf(SearchFilter()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showInfoPopup by remember { mutableStateOf(false) }

    var searchQuery: TextFieldValue by remember {
        mutableStateOf(TextFieldValue(searchNavParam.searchString ?: ""))
    }

    if (searchNavParam.searchString == null) {
        vm.getSearchBooks("")
    } else {
        vm.getSearchBooks(searchNavParam.searchString)
    }

    Scaffold(
        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        topBar = {
            TopSearchAndFilterBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery },
                onSubmit = { query ->
                    vm.getSearchBooks(query)
                }
            )
        },
        floatingActionButton = {
            if (ledgeStats != null) {
                FloatingActionButton(
                    onClick = { showInfoPopup = true },
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info"
                    )
                }
            }
        }
    ) { paddingInner ->

        Column(modifier = Modifier.padding(paddingInner)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Results: " + books.count().toString(), modifier = Modifier.padding(10.dp))

                Button(
                    shape = RectangleShape,
                    onClick = { showFilterDialog = true}
                ) { Icon(Icons.Default.FilterList, "Filter Button") }
            }

            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (currentFilterValue.series == null || currentFilterValue.series?.isEmpty() == true) {
                    items(books) { book ->
                        BookCard(book, onClick = {
                            navController.navigate(BookNavParam(book.book.bookId))
                        })
                    }
                } else {
                    items(books.filter { b ->
                        b.partOfSeries != null && currentFilterValue.series?.contains(b.partOfSeries) == true
                    }) { book ->
                        BookCard(book, onClick = {
                            navController.navigate(BookNavParam(book.book.bookId))
                        })
                    }
                }
            }

            if (showFilterDialog) {
                SearchFilterDialog(
                    vm = searchFilterDialogVm,
                    searchFilter = currentFilterValue,
                    onDismiss = { showFilterDialog = false },
                    onSubmit = { filter ->
                        currentFilterValue = filter
                        queryWithFilter(searchQuery.text, filter, vm)
                    })
            }

            if (showInfoPopup && ledgeStats != null) {
                val text = """
                    Book Count: ${ledgeStats?.totalBooks}
                    Top Author: ${ledgeStats?.topAuthor}
                    Top Genre: ${ledgeStats?.topGenre}
                    Top Format: ${ledgeStats?.topFormat}
                """.trimIndent()
                Popup(
                    alignment = Alignment.Center,
                    onDismissRequest = { showInfoPopup = false },
                    offset = IntOffset(0, 0) // Adjust the offset as needed
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .background(color = MaterialTheme.colorScheme.primary)
                        ) {
                            BasicText(
                                //TODO: add most frequent author, most frequent genre, etc.
                                //  may need to re-design UI
                                //  could also add an Add button to the popup
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun queryWithFilter(searchTerm: String, filter: SearchFilter, vm: BooksScreenViewModel) {
    val genres = if (filter.genres != null && filter.genres.isEmpty()) null else filter.genres
    val readStatuses = if (filter.readStatuses != null && filter.readStatuses.isEmpty()) null else filter.readStatuses
    val bookFormats = if (filter.bookFormats != null && filter.bookFormats.isEmpty()) null else filter.bookFormats

    when {
        genres != null && readStatuses != null && bookFormats != null ->
            vm.updateResultsWithGenreReadStatusBookFormatFilter(
                searchTerm,
                filter.genres!!.map { g -> g.genreId },
                filter.readStatuses!!.map { rs -> rs.readStatusId },
                filter.bookFormats!!.map { bf -> bf.bookFormatId }
            )
        genres != null && readStatuses != null ->
            vm.updateResultsWithGenreReadStatusFilter(
                searchTerm,
                filter.genres!!.map { g -> g.genreId },
                filter.readStatuses!!.map { rs -> rs.readStatusId }
            )
        genres != null && bookFormats != null ->
            vm.updateResultsWithGenreBookFormatFilter(
                searchTerm,
                filter.genres!!.map { g -> g.genreId },
                filter.bookFormats!!.map { bf -> bf.bookFormatId }
            )
        readStatuses != null && bookFormats != null ->
            vm.updateResultsWithReadStatusBookFormatFilter(
                searchTerm,
                filter.readStatuses!!.map { rs -> rs.readStatusId },
                filter.bookFormats!!.map { bf -> bf.bookFormatId }
            )
        genres != null ->
            vm.updateResultsWithGenreFilter(searchTerm, filter.genres!!.map { g -> g.genreId })
        readStatuses != null ->
            vm.updateResultsWithReadStatusFilter(
                searchTerm,
                filter.readStatuses!!.map { rs -> rs.readStatusId }
            )
        bookFormats != null ->
            vm.updateResultsWithBookFormatFilter(
                searchTerm,
                filter.bookFormats!!.map { bf -> bf.bookFormatId }
            )
        else -> vm.getSearchBooks(searchTerm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchAndFilterBar(searchQuery: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit, onSubmit: (String) -> Unit) {
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