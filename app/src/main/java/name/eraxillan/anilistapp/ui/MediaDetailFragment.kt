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
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.ui.adapter.MediaDetailAdapter
import name.eraxillan.anilistapp.databinding.FragmentMediaDetailBinding
import name.eraxillan.anilistapp.viewmodel.MediaViewModel


class MediaDetailFragment : Fragment() {

    private var _binding: FragmentMediaDetailBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private val args: MediaDetailFragmentArgs by navArgs()

    private val viewModel by viewModels<MediaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMediaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val mediaId = (args.media!!).anilistId

        binding.addToFavoriteButton.setOnClickListener {
            viewModel.addMediaToFavorite(args.media!!, findNavController())
        }

        viewModel.isMediaAddedToFavorite(mediaId).observe(
            viewLifecycleOwner, { isFav ->
                binding.addToFavoriteButton.isVisible = !isFav
            })
    }
}
