package com.example.noteappfirebase.core.di

import com.example.noteappfirebase.core.service.AuthService
import com.example.noteappfirebase.core.service.AuthServiceImpl
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
    fun provideNoteRepo(authService: AuthService): NoteRepo {
        return NoteRepoFirestoreImpl(authService = authService)
    }

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return AuthServiceImpl()
    }
}