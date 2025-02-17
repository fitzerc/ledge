package com.github.fitzerc.ledge.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Book
import com.github.fitzerc.ledge.data.models.AuthorAndGenre
import com.github.fitzerc.ledge.data.models.BookAndRelations
import com.github.fitzerc.ledge.ui.models.BookUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val ledgeDb: LedgeDatabase,
    private val toastError: (message: String) -> Unit
): ViewModel() {
    private val _bookCount = MutableStateFlow(0)
    val bookCount: StateFlow<Int> = _bookCount.asStateFlow()

    private val _recentBooks = MutableStateFlow<List<BookAndRelations>>(emptyList())
    val recentBooks: StateFlow<List<BookAndRelations>> = _recentBooks.asStateFlow()

    private val _currentlyReading = MutableStateFlow<List<BookAndRelations>>(emptyList())
    val currentlyReading: StateFlow<List<BookAndRelations>> = _currentlyReading

    private val _authors = MutableStateFlow<List<AuthorAndGenre>>(emptyList())
    val authors: StateFlow<List<AuthorAndGenre>> = _authors.asStateFlow()

    //private val _author = MutableStateFlow<AuthorAndGenre?>(null)
    //val author: StateFlow<AuthorAndGenre?> = _author
    var author = MutableStateFlow<AuthorAndGenre?>(null)

    init {
       viewModelScope.launch {
           ledgeDb.bookDao().getBooksAlphaTitle().collect { books ->
               _bookCount.value = books.count()
           }
       }

        viewModelScope.launch {
            ledgeDb.authorDao().getAuthorsAlpha().collect { authorsList ->
                _authors.value = authorsList
            }
        }

        viewModelScope.launch {
            ledgeDb.bookDao().getRecentBooksWithLimit(5).collect { books ->
                _recentBooks.value = books
            }
        }

        viewModelScope.launch {
            ledgeDb.bookDao().getBooksByStatusValue("Currently Reading").collect { books ->
                _currentlyReading.value = books
            }
        }
    }

    fun saveBookWithAuthorCheck(bookUiModel: BookUiModel) {
        viewModelScope.launch {
            val author = ledgeDb.authorDao().getAuthorByName(bookUiModel.author)

            if (author == null) {
                toastError("somehow the author was lost - unable to save book")
            } else {
                val book = Book(
                    title = bookUiModel.title,
                    authorId = author.author.authorId,
                    genreId = bookUiModel.genre.genreId,
                    bookFormatId = bookUiModel.bookFormat.bookFormatId,
                    readStatusId = bookUiModel.readStatus.readStatusId)

                ledgeDb.bookDao().insertBook(book)
            }
        }
    }

    fun saveAuthor(author: Author) {
        viewModelScope.launch {
            ledgeDb.authorDao().insertAuthor(author)
        }
    }
}

class HomeScreenViewModelFactory(
    private val ledgeDb: LedgeDatabase,
    private val toastError: (message: String) -> Unit
)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(ledgeDb, toastError) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}