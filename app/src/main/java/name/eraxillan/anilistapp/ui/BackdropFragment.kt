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

package name.eraxillan.anilistapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import name.eraxillan.anilistapp.databinding.FragmentBackdropBinding
import name.eraxillan.anilistapp.model.MediaFilter
import name.eraxillan.anilistapp.model.MediaSort
import name.eraxillan.anilistapp.utilities.autoCleared
import timber.log.Timber


@AndroidEntryPoint
class BackdropFragment : Fragment() {
    private var binding by autoCleared<FragmentBackdropBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //super.onCreateView(inflater, container, savedInstanceState)
        //check(savedInstanceState == null)

        // Inflate the layout for this fragment
        binding = FragmentBackdropBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        //check(savedInstanceState == null)

        configureBackdrop()
    }

    private fun configureBackdrop() {
        binding.backLayer.setupListeners {
                filterOptions: MediaFilter, sortOption: MediaSort ->

            Timber.d("Fetching filtered and sorted media list...")
            searchMedia(filterOptions, sortOption, findNavController())
            binding.fragmentBackdropBackLayer.openBottomSheet()
        }
    }
}
