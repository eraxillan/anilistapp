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
import androidx.fragment.app.Fragment
import name.eraxillan.customviews.databinding.BackdropFrontLayerBinding


class BackdropFrontLayer : ConstraintLayout {
    private var _binding: BackdropFrontLayerBinding? = null
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

    private fun init(context: Context, attrs: AttributeSet?) {
        _binding = BackdropFrontLayerBinding.inflate(LayoutInflater.from(context), this, true)
        check(_binding != null)

        if (attrs != null) {
            val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs, R.styleable.BackdropFrontLayer
            )

            if (typedArray.hasValue(R.styleable.BackdropFrontLayer_resultFragment)) {
                val name = typedArray.getString(R.styleable.BackdropFrontLayer_resultFragment) ?: ""
                @Suppress("UNCHECKED_CAST")
                val fragmentClass = Class.forName(name) as Class<out Fragment>
                binding.fragmentBackdropContainer.tag = fragmentClass
            }

            typedArray.recycle()
        }
    }
}
