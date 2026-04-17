package com.nadim.tasknova.di

import android.content.Context
import androidx.room.Room
import com.nadim.tasknova.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tasknova_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTaskDao(db: AppDatabase) = db.taskDao()

    @Provides
    fun provideReminderDao(db: AppDatabase) = db.reminderDao()

    @Provides
    fun provideNoteDao(db: AppDatabase) = db.noteDao()

    @Provides
    fun provideExpenseDao(db: AppDatabase) = db.expenseDao()

    @Provides
    fun provideConversationDao(db: AppDatabase) = db.conversationDao()
}