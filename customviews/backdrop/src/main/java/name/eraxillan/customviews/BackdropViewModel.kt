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

import android.util.AttributeSet
import androidx.lifecycle.ViewModel


// https://developer.android.com/topic/libraries/architecture/viewmodel
// https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate
// https://medium.com/androiddevelopers/viewmodels-persistence-onsaveinstancestate-restoring-ui-state-and-loaders-fc7cc4a6c090
// https://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
// if you want to be the most sure that you won’t lose data, persist it as soon as the user enters it
// Caution: A ViewModel must never reference a view, Lifecycle,
// or any class that may hold a reference to the activity context.
class BackdropViewModel(
    /*private val state: SavedStateHandle*/
): ViewModel() {

    var isCollapsed: Boolean = false
    var resultCount: Int = 0
    var containerAttrs: AttributeSet? = null
}
