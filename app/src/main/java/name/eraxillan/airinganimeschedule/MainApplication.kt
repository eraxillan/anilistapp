/*
 * Copyright 2021 Aleksandr Kamyshnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
