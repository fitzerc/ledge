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

class HomeScreenViewModel(private val ledgeDb: LedgeDatabase): ViewModel() {
    private val _bookCount = MutableStateFlow<Int>(-1)
    val bookCount: StateFlow<Int> = _bookCount.asStateFlow()

    private val _books = MutableStateFlow<List<BookAndRelations>>(emptyList())
    val books: StateFlow<List<BookAndRelations>> = _books.asStateFlow()

    private val _authors = MutableStateFlow<List<AuthorAndGenre>>(emptyList())
    val authors: StateFlow<List<AuthorAndGenre>> = _authors.asStateFlow()

    //private val _author = MutableStateFlow<AuthorAndGenre?>(null)
    //val author: StateFlow<AuthorAndGenre?> = _author
    var author = MutableStateFlow<AuthorAndGenre?>(null)

    init {
        viewModelScope.launch {
            ledgeDb.bookDao().getBooksAlphaTitle().collect() { books ->
                _books.value = books
                _bookCount.value = books.count()
            }
        }

        viewModelScope.launch {
            ledgeDb.authorDao().getAuthorsAlpha().collect() { authorsList ->
                _authors.value = authorsList
            }
        }
    }

    fun saveBookWithAuthorCheck(bookUiModel: BookUiModel) {
        viewModelScope.launch {
            val author = ledgeDb.authorDao().getAuthorByName(bookUiModel.author)

            if (author == null) {
                //TODO: handle no author case - should never hit
            } else {
                val book = Book(
                    title = bookUiModel.title,
                    authorId = author.author.authorId ?: 0,
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

    fun getAuthorByNameAndUpdateCurrentAuthor(fullName: String) {
        viewModelScope.launch {
            author.value = getAuthorByName(fullName)
        }
    }

    suspend fun getAuthorByName(fullName: String): AuthorAndGenre? {
        return ledgeDb.authorDao().getAuthorByName(fullName)
    }
}

class HomeScreenViewModelFactory(private val ledgeDb: LedgeDatabase)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(ledgeDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}