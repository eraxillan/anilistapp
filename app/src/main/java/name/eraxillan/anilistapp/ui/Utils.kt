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
import name.eraxillan.anilistapp.data.room.LocalMediaWithRelations
import name.eraxillan.anilistapp.model.MediaFilter
import name.eraxillan.anilistapp.model.MediaSort
import name.eraxillan.anilistapp.model.MediaSortArg


fun showMediaInfo(media: LocalMediaWithRelations, navController: NavController) {
    val directions = NavGraphDirections.actionMediaDetails(
        media, media.localMedia.romajiTitle
    )
    navController.navigate(directions)
}

fun searchMedia(filterOptions: MediaFilter, sortOption: MediaSort, navController: NavController) {
    val directions = NavGraphDirections.actionMediaSearch(
        filterOptions, MediaSortArg(sortOption)
    )
    navController.navigate(directions)
}

private const val MIN_CONTRAST = 4.50f

/*
This function helps us to always present human-readable text and fix the warning below:
"""
The item's text contrast ratio is 2.16.
This ratio is based on an estimated foreground color of #FFFFFF and an estimated background color of #FF9800.
Consider increasing this item's text contrast ratio to 4.50 or greater.
"""
*/
@ColorInt
fun correctTextColor(@ColorInt textColor: Int, @ColorInt backgroundColor: Int) : Int {
    // Contrast ratios can range from 1 to 21 (commonly written 1:1 to 21:1)
    var contrast = ColorUtils.calculateContrast(textColor, backgroundColor)

    val result = if (contrast > MIN_CONTRAST) {
        // The text color looks fine on the specified background
        textColor
    } else {
        contrast = ColorUtils.calculateContrast(Color.BLACK, backgroundColor)
        if (contrast > MIN_CONTRAST)
            Color.BLACK
        else
            Color.WHITE
    }
    return result
}
