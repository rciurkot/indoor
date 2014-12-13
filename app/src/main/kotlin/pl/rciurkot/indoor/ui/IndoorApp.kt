package pl.rciurkot.indoor

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by rafalciurkot on 12.12.14.
 */
public class IndoorApp : Application() {
    override fun onCreate() {
        super<Application>.onCreate()

        Timber.plant(DebugTree())
    }
}