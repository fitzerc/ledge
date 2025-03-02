package com.github.fitzerc.ledge.ui.viewmodels.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Series
import com.github.fitzerc.ledge.data.models.BookAndAuthor
import com.github.fitzerc.ledge.data.models.SeriesAndAuthor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageSeriesScreenViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _series = MutableStateFlow<List<SeriesAndAuthor>>(emptyList())
    val series: StateFlow<List<SeriesAndAuthor>> = _series.asStateFlow()

    init {
        applyFilter("")
    }

    fun applyFilter(query: String) {
        val seriesAndBookList: MutableList<SeriesAndBooks> = mutableListOf()

        viewModelScope.launch {
            ledgeDb.seriesDao().getSeriesAndAuthor(query).collect { saaList ->
                _series.value = saaList
            }
        }
    }

    fun addSeries(series: Series) {
        viewModelScope.launch {
            ledgeDb.seriesDao().insertSeries(series)
        }
    }

    fun updateSeries(series: Series) {
        viewModelScope.launch {
            ledgeDb.seriesDao().updateSeries(series)
        }
    }
}
class ManageSeriesScreenViewModelFactory(private val ledgeDb: LedgeDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageSeriesScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ManageSeriesScreenViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class SeriesAndBooks(
    val series: Series,
    val bookAndAuthorList: List<BookAndAuthor>
)
