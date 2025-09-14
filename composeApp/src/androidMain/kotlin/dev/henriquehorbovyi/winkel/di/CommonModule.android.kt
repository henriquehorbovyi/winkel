package dev.henriquehorbovyi.winkel.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.henriquehorbovyi.winkel.data.local.DatabaseFactory
import dev.henriquehorbovyi.winkel.data.local.AppDatabase
import dev.henriquehorbovyi.winkel.data.repository.PreferencesRepository
import dev.henriquehorbovyi.winkel.data.repository.createDataStore
import dev.henriquehorbovyi.winkel.data.repository.dataStoreFileName
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase?> {
        DatabaseFactory(get()).createDatabase()
    }

    single<DataStore<Preferences>> {
        createDataStore(get<Context>())
    }

    single<PreferencesRepository> {
        PreferencesRepository(get<DataStore<Preferences>>())
    }
}

// shared/src/androidMain/kotlin/createDataStore.android.kt
fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)