package name.eraxillan.anilistapp.model

/** The current releasing status of the media */
enum class MediaStatus {
    /** Has completed and is no longer being released */
    FINISHED { override fun toString() = "Finished" },

    /** Currently releasing */
    RELEASING { override fun toString() = "Airing" },

    /** To be released at a later date */
    NOT_YET_RELEASED { override fun toString() = "Not yet aired" },

    /** Ended before the work could be finished */
    CANCELLED { override fun toString() = "Cancelled" },

    /** Is currently paused from releasing and will resume at a later date */
    HIATUS { override fun toString() = "Hiatus" },

    /** Constant for unknown enum values */
    UNKNOWN { override fun toString() = "?" },
}

/** The format the media was released in */
enum class MediaFormatEnum {
    /** Anime broadcast on television */
    TV { override fun toString() = "TV" },

    /** Anime which are under 15 minutes in length and broadcast on television */
    TV_SHORT { override fun toString() = "TV Short" },

    /** Anime movies with a theatrical release */
    MOVIE { override fun toString() = "Movie" },

    /**
     * Special episodes that have been included in DVD/Blu-ray releases,
     * picture dramas, pilots, etc
     */
    SPECIAL { override fun toString() = "Special" },

    /**
     * OVA: Original Video Animation
     *
     * Anime that have been released directly on DVD/Blu-ray without
     * originally going through a theatrical release or television broadcast
     */
    OVA { override fun toString() = "OVA" },

    /**
     * ONA: Original Net Animation
     *
     * Anime that have been originally released online or are only available
     * through streaming services.
     */
    ONA { override fun toString() = "ONA" },

    /** Short anime released as a music video */
    MUSIC { override fun toString() = "Music" },

    /** Professionally published manga with more than one chapter */
    MANGA { override fun toString() = "Manga" },

    /** Written books released as a series of light novels */
    NOVEL { override fun toString() = "Light Novel" },

    /** Manga with just one chapter */
    ONE_SHOT { override fun toString() = "One Shot" },

    /** Constant for unknown enum values */
    UNKNOWN { override fun toString() = "?" },
}

//enum class MediaType { ANIME, MANGA, UNKNOWN }

/** The season the media was initially released in */
enum class MediaSeason {
    /** Months December to February */
    WINTER { override fun toString() = "Winter" },

    /** Months March to May */
    SPRING { override fun toString() = "Spring" },

    /** Months June to August */
    SUMMER { override fun toString() = "Summer" },

    /** Months September to November */
    FALL { override fun toString() = "Fall" },

    /** Constant for unknown enum values */
    UNKNOWN { override fun toString() = "?" },
}

/** Source type the media was adapted from */
enum class MediaSourceEnum {
    /** An original production not based of another work */
    ORIGINAL { override fun toString() = "Original" },

    /** Asian comic book */
    MANGA { override fun toString() = "Manga" },

    /** Written work published in volumes */
    LIGHT_NOVEL { override fun toString() = "Light Novel" },

    /** Video game driven primary by text and narrative */
    VISUAL_NOVEL { override fun toString() = "Visual Novel" },

    /** Video game (except visual novel) */
    VIDEO_GAME { override fun toString() = "Video Game" },

    /** Something other than above */
    OTHER { override fun toString() = "Other" },

    /** Version 2+ only. Written works not published in volumes */
    NOVEL { override fun toString() = "Novel" },

    /** Version 2+ only. Self-published works */
    DOUJINSHI { override fun toString() = "Doujinshi" },

    /** Version 2+ only. Japanese Anime */
    ANIME { override fun toString() = "Anime" },

    /**
     * Version 3 only. Written works published online
     */
    WEB_NOVEL { override fun toString() = "Web Novel" },

    /**
     * Version 3 only. Live action media such as movies or TV show
     */
    LIVE_ACTION { override fun toString() = "Live Action" },

    /**
     * Version 3 only. Games excluding video games
     */
    GAME { override fun toString() = "Game" },

    /**
     * Version 3 only. Comics excluding manga
     */
    COMIC { override fun toString() = "Comic" },

    /**
     * Version 3 only. Multimedia project
     */
    MULTIMEDIA_PROJECT { override fun toString() = "Multimedia Project" },

    /**
     * Version 3 only. Picture book
     */
    PICTURE_BOOK { override fun toString() = "Picture Book" },

    /** Constant for unknown enum values */
    UNKNOWN { override fun toString() = "?" },
}

/**
 * The type of media ranking
 */
enum class MediaRankingType {
    /** Ranking is based on the media's ratings/score */
    RATED,

    /** Ranking is based on the media's popularity */
    POPULAR,

    /** Constant for unknown enum values */
    UNKNOWN,
}

/**
 * ISO 3166-1 alpha-2, i.e. two-letter country code
 */
enum class MediaCountry {
    JP { override fun toString() = "Japan" },
    KR { override fun toString() = "South Korea" },
    CN { override fun toString() = "China" },
    TW { override fun toString() = "Taiwan" },
    UNKNOWN { override fun toString() = "?" },
}

/**
 * The kind of media list sorting
 */
enum class MediaSort {
    BY_TITLE { override fun toString() = "Title" },
    BY_POPULARITY { override fun toString() = "Popularity" },
    BY_AVERAGE_SCORE { override fun toString() = "Average Score" },
    BY_TRENDING { override fun toString() = "Trending" },
    BY_FAVORITES { override fun toString() = "Favorites" },
    BY_DATE_ADDED { override fun toString() = "Date Added" },
    BY_RELEASE_DATE { override fun toString() = "Release Date" },

    /** Constant for unknown enum values */
    UNKNOWN,
}
