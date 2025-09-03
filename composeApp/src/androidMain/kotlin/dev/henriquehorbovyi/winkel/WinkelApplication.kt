package dev.henriquehorbovyi.winkel

import android.app.Application
import dev.henriquehorbovyi.winkel.di.initKoin
import org.koin.android.ext.koin.androidContext

class WinkelApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@WinkelApplication)
        }
    }
}