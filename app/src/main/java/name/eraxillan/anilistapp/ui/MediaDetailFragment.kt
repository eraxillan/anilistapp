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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.ui.adapter.MediaDetailAdapter
import name.eraxillan.anilistapp.databinding.FragmentMediaDetailBinding
import name.eraxillan.anilistapp.utilities.autoCleared
import name.eraxillan.anilistapp.viewmodel.FavoriteMediaViewModel


@AndroidEntryPoint
class MediaDetailFragment : Fragment() {
    private var binding by autoCleared<FragmentMediaDetailBinding>()
    private val args: MediaDetailFragmentArgs by navArgs()
    private val favoriteViewModel by viewModels<FavoriteMediaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMediaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupRecyclerView() {
        // Resolve color from theme attr
        val surfaceColor = MaterialColors.getColor(
            binding.mediaFieldList, R.attr.colorSurface
        )

        val adapter = MediaDetailAdapter(args.media!!, surfaceColor)

        binding.mediaFieldList.adapter = adapter
    }

    private fun setupFab() {
        val mediaId = (args.media!!).localMedia.anilistId

        binding.addToFavoriteButton.setOnClickListener {
            favoriteViewModel.addMediaToFavorite(args.media!!, findNavController())
        }

        favoriteViewModel.isMediaAddedToFavorite(mediaId).observe(
            viewLifecycleOwner, { isFav ->
                binding.addToFavoriteButton.isVisible = !isFav
            })
    }
}
