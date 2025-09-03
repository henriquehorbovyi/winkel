package dev.henriquehorbovyi.winkel.di

import dev.henriquehorbovyi.winkel.data.local.DatabaseFactory
import dev.henriquehorbovyi.winkel.data.local.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase?> {
        DatabaseFactory(get()).createDatabase()
    }
}