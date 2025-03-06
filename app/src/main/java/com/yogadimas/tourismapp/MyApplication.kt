package com.yogadimas.tourismapp

import android.app.Application
import com.yogadimas.tourismapp.core.di.authModule
import com.yogadimas.tourismapp.core.di.databaseModule
import com.yogadimas.tourismapp.core.di.networkModule
import com.yogadimas.tourismapp.core.di.repositoryModule
import com.yogadimas.tourismapp.di.useCaseModule
import com.yogadimas.tourismapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    networkModule,
                    authModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}