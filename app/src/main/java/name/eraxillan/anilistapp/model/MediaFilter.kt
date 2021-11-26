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

package name.eraxillan.anilistapp.model

import android.os.Parcelable
import androidx.navigation.NavArgs
import kotlinx.parcelize.Parcelize
import java.util.*

/** Media filter collection */
@Parcelize
data class MediaFilter(
    val search: String? = null,
    val genres: List<String>? = null,
    val tags: List<String>? = null,
    val year: Int? = null,
    val season: MediaSeason? = null,
    val formats: List<MediaFormatEnum>? = null,
    val status: MediaStatus? = null,
    val services: List<String>? = null,
    val country: MediaCountry? = null,
    val sources: List<MediaSourceEnum>? = null,
    val isLicensed: Boolean? = null,
) : NavArgs, Parcelable {

    fun isAbsent(): Boolean {
        return genres == null && tags == null && year == null && season == null &&
                formats == null && status == null && services == null && country == null &&
                sources == null && isLicensed == null
    }

    override fun hashCode(): Int {
        return Objects.hash(
            search,
            genres,
            tags,
            year,
            season,
            formats,
            status,
            services,
            country,
            sources,
            isLicensed,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaFilter

        if (search != other.search) return false
        if (genres != other.genres) return false
        if (tags != other.tags) return false
        if (year != other.year) return false
        if (season != other.season) return false
        if (formats != other.formats) return false
        if (status != other.status) return false
        if (services != other.services) return false
        if (country != other.country) return false
        if (sources != other.sources) return false
        if (isLicensed != other.isLicensed) return false

        return true
    }

    override fun toString(): String {
        return """{
            search: '$search',
            genres: '${genres?.joinToString(", ")}',
            tags: '${tags?.joinToString(", ")}',
            year: $year,
            season: '$season',
            formats: '${formats?.joinToString(", ")}',
            status: '$status',
            services: '${services?.joinToString(", ")}',
            country: '$country',
            sources: '${sources?.joinToString(", ")}',
            isLicensed: '$isLicensed'
        }""".trimIndent()
    }
}
