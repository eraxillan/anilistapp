package name.eraxillan.airinganimeschedule.ui

import androidx.navigation.NavController
import name.eraxillan.airinganimeschedule.NavGraphDirections
import name.eraxillan.airinganimeschedule.model.AiringAnime

fun showAiringAnimeInfo(anime: AiringAnime, navController: NavController) {
    val directions = NavGraphDirections.actionAiringAnimeDetails(
        anime, anime.originalName
    )
    navController.navigate(directions)
}
