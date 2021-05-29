package name.eraxillan.airinganimeschedule.ui

import androidx.navigation.NavController
import name.eraxillan.airinganimeschedule.model.AiringAnime

fun showAiringAnimeInfo(anime: AiringAnime, navController: NavController) {
    val direction = AiringAnimeListFragmentDirections.actionAiringAnimeDetails(
        anime, anime.originalName
    )
    navController.navigate(direction)
}