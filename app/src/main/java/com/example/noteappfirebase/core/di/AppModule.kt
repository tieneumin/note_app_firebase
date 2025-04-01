package com.example.noteappfirebase.core.di

import com.example.noteappfirebase.data.repo.NoteRepo
import com.example.noteappfirebase.data.repo.NoteRepoFirestoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideNoteRepo(): NoteRepo {
        return NoteRepoFirestoreImpl()
    }
}