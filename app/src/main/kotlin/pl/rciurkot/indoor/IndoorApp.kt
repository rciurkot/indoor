package pl.rciurkot.indoor

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree
import kotlin.properties.Delegates

/**
 * Created by rafalciurkot on 12.12.14.
 */
public class IndoorApp : Application() {
    class object {
        private var _self: IndoorApp? = null
        val self by Delegates.lazy { _self!! }
    }

    override fun onCreate() {
        super<Application>.onCreate()
        _self = this

        Timber.plant(DebugTree())
    }
}