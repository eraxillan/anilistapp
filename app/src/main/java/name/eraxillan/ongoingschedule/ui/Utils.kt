package name.eraxillan.ongoingschedule.ui

import androidx.navigation.NavController
import name.eraxillan.ongoingschedule.model.Ongoing

fun showOngoingInfo(ongoing: Ongoing, navController: NavController) {
    val direction = OngoingListFragmentDirections.actionOngoingDetails(
        ongoing, ongoing.originalName
    )
    navController.navigate(direction)
}
