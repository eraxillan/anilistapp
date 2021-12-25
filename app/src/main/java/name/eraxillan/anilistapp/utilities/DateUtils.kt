package name.eraxillan.anilistapp.utilities

import java.time.LocalDate

internal fun isWinterSeasonBegin() = LocalDate.now().month == java.time.Month.DECEMBER
