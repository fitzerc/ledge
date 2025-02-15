package com.github.fitzerc.ledge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.fitzerc.ledge.data.converter.RoomTypeConverters
import com.github.fitzerc.ledge.data.daos.AuthorDao
import com.github.fitzerc.ledge.data.daos.BookDao
import com.github.fitzerc.ledge.data.daos.BookFormatDao
import com.github.fitzerc.ledge.data.daos.GenreDao
import com.github.fitzerc.ledge.data.daos.ReadStatusDao
import com.github.fitzerc.ledge.data.daos.SeriesDao
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Book
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.data.entities.Series
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Author::class,
        Book::class,
        BookFormat::class,
        Genre::class,
        ReadStatus::class,
        Series::class],
    version = 1
)
@TypeConverters(RoomTypeConverters::class)
abstract class LedgeDatabase : RoomDatabase() {
    abstract fun readStatusDao(): ReadStatusDao
    abstract fun bookFormatDao(): BookFormatDao
    abstract fun genreDao(): GenreDao
    abstract fun bookDao(): BookDao
    abstract fun authorDao(): AuthorDao
    abstract fun seriesDao(): SeriesDao

    companion object {
        @Volatile
        private var INSTANCE: LedgeDatabase? = null

        fun getDatabase(context: Context): LedgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LedgeDatabase::class.java,
                    "ledge_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                //Read Statuses
                INSTANCE?.readStatusDao()?.insertReadStatus(
                    ReadStatus(value = "Not Read")
                )
                INSTANCE?.readStatusDao()?.insertReadStatus(
                    ReadStatus(value = "Want to Read")
                )
                INSTANCE?.readStatusDao()?.insertReadStatus(
                    ReadStatus(value = "Currently Reading")
                )
                INSTANCE?.readStatusDao()?.insertReadStatus(
                    ReadStatus(value = "Read")
                )

                //Book Formats
                INSTANCE?.bookFormatDao()?.insertBookFormat(
                    BookFormat(format = "Print")
                )
                INSTANCE?.bookFormatDao()?.insertBookFormat(
                    BookFormat(format = "E-Book")
                )
                INSTANCE?.bookFormatDao()?.insertBookFormat(
                    BookFormat(format = "Audio")
                )

                //Genres - Fiction
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Mystery", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Science Fiction", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Fantasy", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Romance", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Thriller", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Horror", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Historical Fiction", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Young Adult", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Literary Fiction", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Adventure", isFiction = true)
                )
                //Genres - Non-fiction
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Biography/Autobiography", isFiction = true)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Memoir", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Self-Help", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "History", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Science", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Travel", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "True Crime", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Philosophy", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Business", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Health & Fitness", isFiction = false)
                )
                INSTANCE?.genreDao()?.insertGenre(
                    Genre(name = "Technology", isFiction = false)
                )

                //author
                INSTANCE?.authorDao()?.insertAuthor(
                    Author(fullName = "James Clavell", typicalGenreId = 7)
                )
            }
        }
    }
}