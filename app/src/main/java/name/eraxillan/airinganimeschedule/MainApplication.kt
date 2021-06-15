package name.eraxillan.airinganimeschedule

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.concurrent.Executors
//import dagger.hilt.android.HiltAndroidApp


//@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            //Timber.plant(Timber.DebugTree())

            // NOTE: okhttp3 library violates the Strict Mode with "untagged socket" error;
            // no fix yet available neither in library itself nor in client app
            // (e.g. line below not working too); all we can do now - just suppress this error
            // See https://github.com/square/okhttp/issues/3537 for details
            //TrafficStats.setThreadStatsTag(1)

            // This includes warning on leaked closeables and much more
            // https://developer.android.com/reference/kotlin/android/os/StrictMode#enabledefaults
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                enableStrictMode()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun enableStrictMode() {
    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
        .detectAll().penaltyLog().build()
    )

    StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
        .detectAll().penaltyListener(Executors.newSingleThreadExecutor()) { violation ->
            if (violation.javaClass.name.equals("android.os.strictmode.untaggedsocketviolation", ignoreCase = true)) {
                //Timber.v(violation)
                Log.d("name.eraxillan.animeapp", violation.toString())
            }
        }
        .build()
    )
}
