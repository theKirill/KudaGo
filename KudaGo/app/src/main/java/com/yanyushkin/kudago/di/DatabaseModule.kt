package com.yanyushkin.kudago.di

import com.yanyushkin.kudago.database.DatabaseService
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideRealm(): Realm = Realm.getDefaultInstance()

    @Singleton
    @Provides
    fun provideDatabaseService(realm: Realm): DatabaseService = DatabaseService(realm)
}