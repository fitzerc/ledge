package com.github.fitzerc.ledge.ui.viewmodels.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.models.AuthorAndGenre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageAuthorsScreenViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _filteredAuthorsWithGenre = MutableStateFlow<List<AuthorAndGenre>>(emptyList())
    val filteredAuthorsWithGenre: StateFlow<List<AuthorAndGenre>> = _filteredAuthorsWithGenre.asStateFlow()

    init {
        applyFilter("")
    }

    fun applyFilter(filter: String) {
        viewModelScope.launch {
            ledgeDb.authorDao().filterAuthorsByName(filter).collect { authors ->
                _filteredAuthorsWithGenre.value = authors
            }
        }
    }

    fun addAuthor(author: Author) {
        viewModelScope.launch {
            val existingAuthor = ledgeDb.authorDao().getAuthorByName(author.fullName)
            if (existingAuthor == null) {
                ledgeDb.authorDao().insertAuthor(author)
            }
        }
    }

    fun updateAuthor(author: Author) {
        viewModelScope.launch {
            ledgeDb.authorDao().getAuthorById(author.authorId)
                ?: throw Exception("author not found")

            ledgeDb.authorDao().updateAuthor(author)
        }
    }
}

class ManageAuthorsScreenViewModelFactory(private val ledgeDb: LedgeDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageAuthorsScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ManageAuthorsScreenViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
