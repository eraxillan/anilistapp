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

package name.eraxillan.airinganimeschedule.ui

import androidx.navigation.NavController
import name.eraxillan.airinganimeschedule.NavGraphDirections
import name.eraxillan.airinganimeschedule.model.AiringAnime

fun showAiringAnimeInfo(anime: AiringAnime, navController: NavController) {
    val directions = NavGraphDirections.actionAiringAnimeDetails(
        anime, anime.title.romaji
    )
    navController.navigate(directions)
}
