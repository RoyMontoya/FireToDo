package com.firebase.test.firetodo

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Created by Roy on 4/5/18.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

}