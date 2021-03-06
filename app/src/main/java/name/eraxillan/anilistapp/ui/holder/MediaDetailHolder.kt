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

package name.eraxillan.anilistapp.ui.holder

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.android.material.elevation.ElevationOverlayProvider
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.databinding.ListItemMediaDetailBinding


class MediaDetailHolder(
    private val binding: ListItemMediaDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(text: String, textColor: Int) {
        binding.apply {
            setText(text)
            setTextColor(textColor)
            executePendingBindings()
        }
    }

    @ColorInt
    fun elevatedBackgroundColor(): Int {
        val backgroundColor = MaterialColors.getColor(
            binding.detailCard.context, R.attr.colorSurface, Color.TRANSPARENT
        )
        val provider = ElevationOverlayProvider(binding.detailCard.context)
        return provider.compositeOverlayIfNeeded(backgroundColor, binding.detailCard.cardElevation)
    }
}
