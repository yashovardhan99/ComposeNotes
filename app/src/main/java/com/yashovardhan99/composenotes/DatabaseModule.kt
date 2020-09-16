package com.yashovardhan99.composenotes

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yashovardhan99.composenotes.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext appContext: Context): NoteDatabase {
        return Room.databaseBuilder(
            appContext, NoteDatabase::class.java,
            "note_database"
        ).addMigrations(
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "CREATE VIRTUAL TABLE IF NOT EXISTS `note_search`" +
                                " USING FTS4(`text`, content=`note_table`);"
                    )
                    database.execSQL("INSERT INTO note_search(`rowid`, `text`) SELECT `id`, `text` FROM note_table;")
                }
            }
        ).addMigrations(
            object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE `note_table` ADD `imageUri` varchar")
                }

            }
        ).build()

    }
}