package name.eraxillan.anilistapp.utilities

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate


private const val THEME_LIGHT_MODE = "light"
private const val THEME_DARK_MODE = "dark"
const val THEME_DEFAULT_MODE = "default"


fun applyUiTheme(themePref: String) {
    when (themePref) {
        THEME_LIGHT_MODE -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        THEME_DARK_MODE -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        THEME_DEFAULT_MODE -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
    }
}
