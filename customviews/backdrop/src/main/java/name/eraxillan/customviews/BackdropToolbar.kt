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
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import name.eraxillan.customviews.databinding.BackdropToolbarBinding


/**
 * Backdrop toolbar control to allow user show/hide back layer
 */
class BackdropToolbar: ConstraintLayout {
    var itemCount: Int = 0

    private var _binding: BackdropToolbarBinding? = null
    private val binding get() = _binding!!

    constructor(context: Context): super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, @Suppress("UNUSED_PARAMETER") attrs: AttributeSet?) {
        _binding = BackdropToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        check(_binding != null)

        if (attrs != null) {
            val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.BackdropToolbar
            )

            // ...

            typedArray.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setListeners()
        updateState()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    var currentState: Int = BottomSheetBehavior.STATE_EXPANDED
    var openBottomSheetCallback: (() -> Unit)? = null
    var closeBottomSheetCallback: (() -> Unit)? = null

    private fun setListeners() {
        binding.resultText.setOnClickListener {
            check(openBottomSheetCallback != null)
            check(closeBottomSheetCallback != null)
            if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                closeBottomSheetCallback?.invoke()
            } else {
                openBottomSheetCallback?.invoke()
            }
        }

        binding.filterImage.setOnClickListener {
            check(openBottomSheetCallback != null)
            check(closeBottomSheetCallback != null)
            if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                closeBottomSheetCallback?.invoke()
            } else {
                openBottomSheetCallback?.invoke()
            }
        }
    }

    fun updateState() {
        when (currentState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                binding.resultText.text = if (itemCount == 0)
                    context.resources.getString(R.string.search_results_loading)
                else
                    context.resources.getString(R.string.search_results_count, itemCount)
                binding.filterImage.setImageResource(R.drawable.ic_baseline_filter_list_24)
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                binding.resultText.text = context.resources.getString(R.string.see_the_results)
                binding.filterImage.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
        }
    }
}
