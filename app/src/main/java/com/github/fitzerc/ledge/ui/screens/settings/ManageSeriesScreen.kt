package com.github.fitzerc.ledge.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Series
import com.github.fitzerc.ledge.data.models.SeriesAndAuthor
import com.github.fitzerc.ledge.ui.dialogs.settings.AddSeriesDialog
import com.github.fitzerc.ledge.ui.dialogs.settings.EditSeriesDialog
import com.github.fitzerc.ledge.ui.viewmodels.screens.settings.ManageSeriesScreenViewModel
import com.github.fitzerc.ledge.ui.viewmodels.screens.settings.ManageSeriesScreenViewModelFactory

@Composable
fun ManageSeriesScreen(
    innerPadding: PaddingValues,
    ledgeDb: LedgeDatabase
) {
    val vm: ManageSeriesScreenViewModel = viewModel(
        factory = ManageSeriesScreenViewModelFactory(ledgeDb)
    )

    val series by vm.series.collectAsState()

    var showAddSeriesDialog by remember { mutableStateOf(false) }
    var showEditSeriesDialog by remember { mutableStateOf(false) }
    var currentFilterValue: String by remember { mutableStateOf("") }

    var selectedSeries by remember { mutableStateOf<Series?>(null) }

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
                onClick = { showAddSeriesDialog = true }
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
                items(series) { s ->
                    Row {
                        SeriesCard(
                            series = s,
                            onClick = { series ->
                                selectedSeries = Series(
                                    seriesId = series.seriesId,
                                    seriesName = series.seriesName,
                                )
                                showEditSeriesDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showAddSeriesDialog) {
            AddSeriesDialog(
                onDismiss = { showAddSeriesDialog = false },
                onSubmit = {
                    s -> vm.addSeries(s)
                    vm.applyFilter(searchQuery.text)
                }
            )
        }

        if (showEditSeriesDialog && selectedSeries != null) {
            EditSeriesDialog(
                series = selectedSeries!!, //null check above
                onDismiss = { showEditSeriesDialog = false },
                onSubmit = { updatedSeries ->
                    vm.updateSeries(updatedSeries)
                    vm.applyFilter(currentFilterValue)
                }
            )
        }
    }
}

@Composable
fun SeriesCard(series: SeriesAndAuthor, onClick: (SeriesAndAuthor) -> Unit) {
    Card(
       modifier = Modifier.fillMaxWidth(),
       onClick = { onClick(series) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = series.seriesName, style = MaterialTheme.typography.titleMedium)
            if (!series.authorFullName.isNullOrEmpty()) {
                Text(
                    text = "By: ${series.authorFullName}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchAndFilterBar(
    searchQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit, onSubmit: (String) -> Unit
) {
    TopAppBar(title = {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
