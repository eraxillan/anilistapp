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
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.util.TypedValue
import android.util.Xml
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.children
import org.xmlpull.v1.XmlPullParser
import timber.log.Timber


fun actionBarHeight(context: Context) = with(TypedValue().also {
    context.theme.resolveAttribute(android.R.attr.actionBarSize, it, true)
}) {
    TypedValue.complexToDimensionPixelSize(this.data, context.resources.displayMetrics)
}

fun printViewChildren(view: View, root: Int = 0) {
    val indent = "*".repeat(root)

    if (view !is ViewGroup) {
        Timber.e("${indent}View group: $view")
        return
    }
    Timber.e("${indent}View group: $view")

    for (child in view.children) {
        if (child !is ViewGroup) {
            Timber.e("${indent}View group: $child")
            continue
        }
        printViewChildren(child, root + 2)
    }
}


fun Context.loadAttributes(@LayoutRes id: Int) : AttributeSet? {
    var result: AttributeSet? = null

    // android.content.res.XmlBlock.Parser
    val parser: XmlResourceParser = resources.getLayout(id)

    var event = parser.eventType
    do {
        if (event == XmlPullParser.START_TAG) {
            result = Xml.asAttributeSet(parser)
        }
        event = parser.next()
    } while (event != XmlPullParser.END_DOCUMENT)

    return result
}
