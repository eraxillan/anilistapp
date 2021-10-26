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

package name.eraxillan.anilistapp.ui

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import name.eraxillan.anilistapp.NavGraphDirections
import name.eraxillan.anilistapp.model.Media

fun showMediaInfo(media: Media, navController: NavController) {
    val directions = NavGraphDirections.actionMediaDetails(
        media, media.romajiTitle
    )
    navController.navigate(directions)
}

fun correctTextColor(@ColorInt textColor: Int, @ColorInt backgroundColor: Int) : Int {
    val textColorIsDark = ColorUtils.calculateLuminance(textColor) < 0.25  // 0.5
    val textColorIsLight = ColorUtils.calculateLuminance(textColor) > 0.75 // 0.5

    val backgroundColorIsDark = ColorUtils.calculateLuminance(backgroundColor) < 0.25
    val backgroundColorIsLight = ColorUtils.calculateLuminance(backgroundColor) > 0.75

    if (textColorIsDark && backgroundColorIsDark)
        return Color.WHITE
    if (textColorIsLight && backgroundColorIsLight)
        return Color.BLACK
    return textColor
}
