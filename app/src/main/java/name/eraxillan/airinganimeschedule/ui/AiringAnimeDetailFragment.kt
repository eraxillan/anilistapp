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

package name.eraxillan.airinganimeschedule.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import name.eraxillan.airinganimeschedule.ui.adapter.AiringAnimeDetailAdapter
import name.eraxillan.airinganimeschedule.databinding.FragmentAnimeDetailBinding
import name.eraxillan.airinganimeschedule.viewmodel.AiringAnimeViewModel


class AiringAnimeDetailFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "54BE6C87_AADF" // AADF = AiringAnimeDetailFragment
    }

    private var _binding: FragmentAnimeDetailBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

    private val args: AiringAnimeDetailFragmentArgs by navArgs()

    private val viewModel by viewModels<AiringAnimeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)

        /*binding.also { ui ->
            ui.lifecycleOwner = viewLifecycleOwner
            ui.animeId = (args.anime!!).id?.toInt() ?: -1
            ui.viewModel = viewModel
        }*/

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
        val adapter = AiringAnimeDetailAdapter(args.anime!!)

        binding.animeFieldList.adapter = adapter
    }

    private fun setupFab() {
        val animeId = (args.anime!!).id?.toInt() ?: -1
        /*binding.also { ui ->
            ui.animeId = animeId
            ui.viewModel = viewModel
        }*/

        binding.addToFavoriteButton.setOnClickListener {
            viewModel.addAiringAnime(args.anime!!, findNavController())
        }

        viewModel.isAddedToFavorite(animeId).observe(
            viewLifecycleOwner, { isFav ->
                Log.e("name.eraxillan.app", "IS_FAV: $isFav")
                binding.addToFavoriteButton.isVisible = !isFav
            })
    }
}
