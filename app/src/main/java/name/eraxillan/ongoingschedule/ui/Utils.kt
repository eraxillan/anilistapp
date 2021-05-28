package name.eraxillan.ongoingschedule.ui

import androidx.navigation.NavController
import name.eraxillan.ongoingschedule.model.AiringAnime

fun showOngoingInfo(anime: AiringAnime, navController: NavController) {
    val direction = OngoingListFragmentDirections.actionOngoingDetails(
        anime, anime.originalName
    )
    navController.navigate(direction)
}
