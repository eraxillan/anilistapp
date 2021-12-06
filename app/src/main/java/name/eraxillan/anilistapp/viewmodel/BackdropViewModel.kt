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

package name.eraxillan.anilistapp.viewmodel

import android.util.AttributeSet
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class BackdropViewModel @Inject constructor() : ViewModel() {

    override fun onCleared() {
        super.onCleared()

        Timber.d("BackdropViewModel destroyed")
    }

    var isCollapsed: Boolean = false
    var resultCount: Int = 0

    var containerAttrs: AttributeSet? = null

    /*private val _resultCount = MutableLiveData<Int>(0)
    val resultCount: LiveData<Int> = _resultCount*/

    /*fun setResultCount(resultCount: Int) {
        _resultCount.value = resultCount
    }*/
}
