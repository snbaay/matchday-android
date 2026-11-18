package com.example.matchday

import android.app.Application
import com.example.matchday.di.dataModule
import com.example.matchday.di.domainModule
import com.example.matchday.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MatchdayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MatchdayApplication)
            modules(dataModule, domainModule, presentationModule)
        }
    }
}
