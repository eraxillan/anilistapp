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

package name.eraxillan.airinganimeschedule.ui.holder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.databinding.ListItemAiringAnimeDetailBinding

class MediaDetailHolder(
    private val binding: ListItemAiringAnimeDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val LOG_TAG = "54BE6C87_AADH" // AADH = MediaDetailHolder
    }

    fun bind(text: String, textColor: Int) {
        binding.apply {
            setText(text)
            setTextColor(textColor)
            executePendingBindings()
        }
    }
}
