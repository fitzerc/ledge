package com.github.fitzerc.ledge.ui.viewmodels.dialogs.editbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Series
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditBookSeriesDialogViewModel(
    private val ledgeDatabase: LedgeDatabase
): ViewModel() {
    private val _filteredSeriesNames = MutableStateFlow<List<String>>(emptyList())
    val filteredSeriesNames: StateFlow<List<String>> = _filteredSeriesNames.asStateFlow()

    fun filterSeries(filter: String) {
        if (filter.isEmpty()) return

        viewModelScope.launch {
            ledgeDatabase.seriesDao().getFilteredSeriesAlpha(filter).collect { filteredSeries ->
                _filteredSeriesNames.value = filteredSeries.map { s -> s.seriesName }
            }
        }
    }

    fun upsertSeries(series: Series) {
        viewModelScope.launch {
            ledgeDatabase.seriesDao().upsertSeries(series)
        }
    }
}

class EditBookSeriesDialogViewModelFactory(private val ledgeDb: LedgeDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditBookSeriesDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditBookSeriesDialogViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
