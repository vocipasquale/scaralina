package com.pv.scaralina

import android.app.Application
import com.pv.scaralina.data.room.AppDatabase

class ScaralinaApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}
