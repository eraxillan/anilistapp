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

package name.eraxillan.customviews

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import name.eraxillan.customviews.databinding.BackdropFrontLayerContainerBinding
import timber.log.Timber


class BackdropFrontLayerContainer: Fragment() {
    private var binding by autoCleared<BackdropFrontLayerContainerBinding>()

    //private val viewModel by viewModels<BackdropViewModel>()
    private val sharedViewModel: BackdropViewModel by activityViewModels()
    private lateinit var frontLayerFragment: ResultFragment
    private var attrs: AttributeSet? = null
    private var behavior: BottomSheetBehavior<View>? = null

    override fun onResume() {
        super.onResume()

        sharedViewModel.isCollapsed = PreferenceRepository.getInstance(requireContext()).isFilterCollapsed

        // Restore backdrop toolbar state from view model
        val currentState = if (sharedViewModel.isCollapsed)
            BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED

        binding.toolbarBackdropFrontView.currentState = currentState
        binding.toolbarBackdropFrontView.itemCount = sharedViewModel.resultCount
        binding.toolbarBackdropFrontView.updateState()

        check(binding.toolbarBackdropFrontView.openBottomSheetCallback != null)
        check(binding.toolbarBackdropFrontView.closeBottomSheetCallback != null)
        if (sharedViewModel.isCollapsed)
            binding.toolbarBackdropFrontView.closeBottomSheetCallback?.invoke()
        else
            binding.toolbarBackdropFrontView.openBottomSheetCallback?.invoke()
    }

    override fun onPause() {
        super.onPause()

        // Save toolbar state in model to restore it later in `onResume`
        val isCollapsed = binding.toolbarBackdropFrontView.currentState == BottomSheetBehavior.STATE_COLLAPSED
        sharedViewModel.isCollapsed = isCollapsed
        sharedViewModel.resultCount = binding.toolbarBackdropFrontView.itemCount

        PreferenceRepository.getInstance(requireContext()).isFilterCollapsed = isCollapsed
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //super.onCreateView(inflater, container, savedInstanceState)
        //check(savedInstanceState == null)

        binding = BackdropFrontLayerContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createListFragment()

        val (container, front, paddingTop, panel) = getParentViews()
        setPanelLayoutBehavior(front)
        setPanelBehavior(front, panel)
        setPanelCallbacks(container, paddingTop, panel)
        setToolbarCallbacks()
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)

        check(savedInstanceState == null)
        this.attrs = attrs
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (attrs != null) {
            sharedViewModel.containerAttrs = attrs
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setPanelLayoutBehavior(front: View) {
        check(sharedViewModel.containerAttrs != null)
        check(front.layoutParams is CoordinatorLayout.LayoutParams)

        behavior = GestureLockedBottomSheetBehavior(
            requireContext(), sharedViewModel.containerAttrs
        )
        check(behavior != null)
        behavior?.skipCollapsed = true
        behavior?.peekHeight = actionBarHeight(requireContext())

        val frontLayoutParams = front.layoutParams as CoordinatorLayout.LayoutParams
        frontLayoutParams.behavior = behavior
    }

    private fun createListFragment() {
        if (childFragmentManager.fragments.size == 0) {
            binding.toolbarBackdropFrontView.isVisible = true

            childFragmentManager.commitNow {
                setReorderingAllowed(true)
                add(R.id.backdrop_front_fragment_container, getResultFragment(), bundleOf())
            }
            Timber.d("Media list fragment was dynamically created")
        }

        frontLayerFragment = childFragmentManager
            .findFragmentById(R.id.backdrop_front_fragment_container) as ResultFragment
    }

    private data class ParentContainer(
        val containerView: View,
        val frontContainerView: View,
        val containerTopPadding: Int,
        val backLayer: BackdropBackLayer
    )

    private fun getParentViews(): ParentContainer {
        val container = binding.root.parent as View
        check(container.id == R.id.fragment_backdrop_container)
        val paddingTop = container.paddingTop

        check(parentFragment?.view != null)
        val childContainer = parentFragment?.view as ViewGroup

        val backView = childContainer.children.first { childView -> childView is BackdropBackLayer }
        val frontView = childContainer.children.first { childView -> childView is BackdropFrontLayer }
        val back = backView as BackdropBackLayer
        val front = frontView as BackdropFrontLayer

        return ParentContainer(container, front, paddingTop, back)
    }

    private fun getResultFragment(): Class<out Fragment> {
        val parentContainer = getParentViews()

        @Suppress("UNCHECKED_CAST")
        return parentContainer.containerView.tag as Class<out Fragment>
    }

    private fun setPanelBehavior(container: View, backLayer: BackdropBackLayer) {
        container.let { view1 ->
            BottomSheetBehavior.from(view1).let { bs ->
                bs.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        binding.toolbarBackdropFrontView.currentState = newState
                        binding.toolbarBackdropFrontView.updateState()

                        val isCollapsed = newState == BottomSheetBehavior.STATE_COLLAPSED
                        PreferenceRepository.getInstance(requireContext()).isFilterCollapsed = isCollapsed
                    }
                })

                // Set the bottom sheet expanded by default
                bs.state = BottomSheetBehavior.STATE_EXPANDED

                backLayer.setBehavior(bs)
            }
        }
    }

    private fun setPanelCallbacks(container: View, paddingTop: Int, backLayer: BackdropBackLayer) {
        binding.toolbarBackdropFrontView.openBottomSheetCallback = {
            backLayer.openBottomSheet()
            backLayer.show(false)

            // FIXME: a crunch
            container.setPadding(0, paddingTop, 0, 0)
        }
        binding.toolbarBackdropFrontView.closeBottomSheetCallback = {
            backLayer.closeBottomSheet()
            backLayer.show(true)

            // FIXME: a crunch
            container.setPadding(0, 0, 0, 0)
        }
    }

    private fun setToolbarCallbacks() {
        // Update result count on media list load completion
        frontLayerFragment.onDataLoaded = {
            sharedViewModel.resultCount = it
            binding.toolbarBackdropFrontView.itemCount = it
            binding.toolbarBackdropFrontView.updateState()
        }
    }
}
