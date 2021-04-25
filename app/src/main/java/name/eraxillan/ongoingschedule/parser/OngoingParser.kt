package name.eraxillan.ongoingschedule.parser

import android.util.Log
import name.eraxillan.ongoingschedule.db.ZonedScheduledTime
import java.time.*
import kotlin.random.Random
import name.eraxillan.ongoingschedule.model.Ongoing
import name.eraxillan.ongoingschedule.ui.OngoingSelectionFragment
import java.net.URL


// Strategy pattern: get ongoing info from several websites that have appropriate backend
interface OngoingParser {
    fun parse(url: URL, ongoing: Ongoing): Boolean
}


class FakeParser: OngoingParser {
    private val TAG = FakeParser::class.java.simpleName

    private val predefinedOngoings = listOf(
        Ongoing(
            1,
            URL("https://www.wakanim.tv/ru/v2/catalogue/show/1251/kombatanty-budut-vyslany"),
            1, "Combatants Will Be Dispatched!",
            3, 12,
            LocalDate.of(2021, Month.APRIL, 18),
            ZonedScheduledTime.of(DayOfWeek.SUNDAY, LocalTime.of(15, 0),
                ZoneOffset.of("+3"), ZoneId.of("Europe/Moscow")), // Sundays 15:00 (MSK)
            18
        ),
        Ongoing(
            2,
            URL("https://www.wakanim.tv/ru/v2/catalogue/show/484/korzinka-fruktov-fruits-basket"),
            3, "Fruits Basket",
            2, 12,
            LocalDate.of(2021, Month.APRIL, 18),
            ZonedScheduledTime.of(DayOfWeek.MONDAY, LocalTime.of(20, 30),
                ZoneOffset.of("+3"), ZoneId.of("Europe/Moscow")), // Mondays 20:30 (MSK)
            16
        ),
        Ongoing(
            3,
            URL("https://www.wakanim.tv/ru/v2/catalogue/show/1294/polnoe-pogruzhenie-chto-yesli-luchshaya-rpg-s-polnym-pogruzheniem-budet-khuzhe-realnosti"),
            1, "Full Dive: This Ultimate Next-Gen Full Dive RPG Is Even Shittier than Real Life!",
            4, 12,
            LocalDate.of(2021, Month.APRIL, 18),
            ZonedScheduledTime.of(DayOfWeek.WEDNESDAY, LocalTime.of(17, 30),
                ZoneOffset.of("+3"), ZoneId.of("Europe/Moscow")), // Wednesdays 17:30 (MSK)
            18
        ),
        Ongoing(
            4,
            URL("https://www.wakanim.tv/ru/v2/catalogue/show/1303/super-cub"),
            1, "Super Cub",
            2, 24,
            LocalDate.of(2021, Month.APRIL, 18),
            ZonedScheduledTime.of(DayOfWeek.MONDAY, LocalTime.of(18, 0),
                ZoneOffset.of("+3"), ZoneId.of("Europe/Moscow")), // Wednesdays 18:00 (MSK)
            12
        ),
        Ongoing(
            5,
            URL("https://www.wakanim.tv/ru/v2/catalogue/show/1300/sinee-otrazhenie-luch-blue-reflection-ray"),
            1, "Blue Reflection Ray",
            5, 24,
            LocalDate.of(2021, Month.APRIL, 18),
            ZonedScheduledTime.of(DayOfWeek.FRIDAY, LocalTime.of(20, 55),
                ZoneOffset.of("+3"), ZoneId.of("Europe/Moscow")), // Fridays 20:55 (MSK)
            18
        ),
        Ongoing(
            6,
            URL("https://www.wakanim.tv/ru/v2/catalogue/show/1212/bek-arrou-back-arrow"),
            1, "BACK ARROW",
            3, 13,
            LocalDate.of(2021, Month.APRIL, 18),
            ZonedScheduledTime.of(DayOfWeek.FRIDAY, LocalTime.of(19, 30),
                ZoneOffset.of("+3"), ZoneId.of("Europe/Moscow")), // Fridays 19:30 (MSK)
            18
        ),
        Ongoing(
            7,
            URL("https://www.wakanim.tv/ru/v2/catalogue/show/1310/dom-teney-shadows-house"),
            1, "SHADOWS HOUSE",
            2, 24,
            LocalDate.of(2021, Month.APRIL, 18),
            ZonedScheduledTime.of(DayOfWeek.SATURDAY, LocalTime.of(20, 0),
                ZoneOffset.of("+3"), ZoneId.of("Europe/Moscow")), // Saturdays 20:00 (MSK)
            12
        ),
    )

    // https://stackoverflow.com/a/363692
    /* ThreadLocalRandom.current().nextInt(
        0,
        predefinedOngoings.size + 1
    )*/
    private fun randomIndex(): Int = Random.nextInt(0, predefinedOngoings.size)

    override fun parse(url: URL, ongoing: Ongoing): Boolean {
        // Just randomly return one of predefined ongoing objects
        //val randomOngoing = predefinedOngoings[randomIndex()]
        //ongoing.copyDataFields(randomOngoing)

        // Return next ongoing from predefined list
        val nextOngoing = predefinedOngoings[index++]
        if (index == predefinedOngoings.size) index = 0
        Log.e(TAG, "ONGOING INDEX: $index")

        ongoing.copyDataFields(nextOngoing)
        nextOngoing.url = url
        return true
    }

    companion object {
        private var index = 0
    }
}