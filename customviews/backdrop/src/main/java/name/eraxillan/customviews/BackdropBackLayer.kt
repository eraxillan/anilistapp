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
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior


class BackdropBackLayer : ConstraintLayout {
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    constructor(context: Context): super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.BackdropBackLayer
            )

            // ...

            typedArray.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        updateLayoutParams<MarginLayoutParams> {
            this.setMargins(0, actionBarHeight(context), 0, 0)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun show(isVisible: Boolean) {
        for (child in children) {
            val childOfChild = (child as ViewGroup)[0]
            childOfChild.isVisible = isVisible
        }
    }

    fun closeBottomSheet() {
        check(mBottomSheetBehavior != null)
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun openBottomSheet() {
        check(mBottomSheetBehavior != null)
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun setBehavior(behavior: BottomSheetBehavior<View?>) {
        mBottomSheetBehavior = behavior
    }
}
