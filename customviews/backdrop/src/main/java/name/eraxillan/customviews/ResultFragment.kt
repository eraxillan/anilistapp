package name.eraxillan.customviews

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class ResultFragment: BottomSheetDialogFragment() {
    var onDataLoaded: ((count: Int) -> Unit)? = null
}
