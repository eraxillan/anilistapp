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

package name.eraxillan.anilistapp.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.IntDef
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import name.eraxillan.anilistapp.R
import name.eraxillan.anilistapp.databinding.ViewChippedEdittextBinding
import timber.log.Timber
import java.security.InvalidParameterException


/**
 * Text edit control with Material Design "chips" instead of plain text
 */
class ChippedEditText : ConstraintLayout {

    private var _binding: ViewChippedEdittextBinding? = null
    private val binding get() = _binding!!

    private lateinit var dropDownDrawable: Drawable
    private lateinit var clearDrawable: Drawable

    fun interface Listener {
        fun onComplete()
    }

    var isCompleteListener: Listener? = null

    var title: String
        get() {
            return binding.chippedEdittextTitle.text.toString()
        }
        set(value) {
            binding.chippedEdittextTitle.text = value
        }

    @SelectionModeAnnotation
    var selectionMode: Int = SINGLE_CHOICE

    @ElementTypeAnnotation
    var elementType: Int = STRING_ELEMENT_TYPE

    private var stringElements: List<String> = emptyList()
    set(value) {
        elementType = STRING_ELEMENT_TYPE
        checkedElements = Array(value.size) { false }.toBooleanArray()
        field = value

        isCompleteListener?.onComplete()
    }
    private var stringSkipEntry: String = ""

    private var integerElementFrom: Int? = null
    private var integerElementTo: Int? = null
    private var integerElements: List<Int> = emptyList()
    set(value) {
        elementType = INT_ELEMENT_TYPE
        checkedElements = Array(value.size) { false }.toBooleanArray()
        field = value

        isCompleteListener?.onComplete()
    }

    // Enum entry name, enum entry display string
    @PublishedApi internal
    var enumElements: List<Pair<String, String>> = emptyList()
        set(value) {
            elementType = ENUM_ELEMENT_TYPE
            checkedElements = Array(value.size) { false }.toBooleanArray()
            field = value

            isCompleteListener?.onComplete()
        }
    private var enumSkipEntry: String = ""

    private val displayableElements: List<String>
    get() {
        return when (elementType) {
            STRING_ELEMENT_TYPE -> stringElements
            INT_ELEMENT_TYPE -> integerElements.map { it.toString() }
            ENUM_ELEMENT_TYPE -> enumElements.map { it.second }
            else -> {
                Timber.e("Unknown element type $elementType!")
                emptyList()
            }
        }
    }

    // TODO: find a way to make this field private to avoid possible misuse bugs
    @PublishedApi internal
    var checkedElements: BooleanArray = booleanArrayOf()

    private val elementsSize: Int
    get() {
        return when (elementType) {
            STRING_ELEMENT_TYPE -> stringElements.size
            INT_ELEMENT_TYPE -> integerElements.size //integerElementTo - integerElementFrom + 1
            ENUM_ELEMENT_TYPE -> enumElements.size
            else -> {
                Timber.e("Unknown element type $elementType!")
                0
            }
        }
    }

    fun checkStringElement(element: String): Boolean {
        //check(selectionMode == SINGLE_CHOICE)
        check(elementType == STRING_ELEMENT_TYPE)
        check(element.isNotEmpty())
        check(stringElements.isNotEmpty())
        check(checkedElements.isNotEmpty())
        check(stringElements.size == checkedElements.size)

        val index = stringElements.indexOf(element)
        check(index != -1)

        addChip(
            context,
            binding.chippedEdittextInput.editableText,
            stringElements[index]
        )

        val wasChecked = checkedElements[index]
        checkedElements[index] = true
        return wasChecked
    }

    /*fun checkStringElements(vararg elements: String) {
        elements.forEach { checkStringElement(it) }
    }*/

    fun checkStringElements(elements: List<String>?) {
        elements?.forEach { checkStringElement(it) }
    }

    fun checkIntegerElement(element: Int?): Boolean {
        if (element == null) return false

        //check(selectionMode == SINGLE_CHOICE)
        check(elementType == INT_ELEMENT_TYPE)
        //check(element in integerElementFrom..integerElementTo)
        check(integerElements.isNotEmpty())
        check(checkedElements.isNotEmpty())
        check(integerElements.size == checkedElements.size)

        val index = integerElements.indexOf(element)
        check(index != -1)

        addChip(
            context,
            binding.chippedEdittextInput.editableText,
            integerElements[index].toString()
        )

        val wasChecked = checkedElements[index]
        checkedElements[index] = true
        return wasChecked
    }

    /*fun checkIntegerElements(vararg elements: Int) {
        elements.forEach { checkIntegerElement(it) }
    }*/

    inline fun <reified T : Enum<T>> checkEnumerationElement(element: T?): Boolean {
        if (element == null) return false
         return checkEnumerationElement(element.name)
    }

    /*inline fun <reified T : Enum<T>> checkEnumerationElements(vararg elements: T?) {
        elements.forEach { checkEnumerationElement(it) }
    }*/

    inline fun <reified T : Enum<T>> checkEnumerationElements(elements: List<T>?) {
        elements?.forEach { checkEnumerationElement(it) }
    }

    fun checkEnumerationElement(element: String): Boolean {
        if (element == enumSkipEntry) {
            Timber.e("Enum entry '$element' skipped")
            return false
        }

        //check(selectionMode == SINGLE_CHOICE)
        check(elementType == ENUM_ELEMENT_TYPE)
        check(element.isNotEmpty())
        check(enumElements.isNotEmpty())
        check(checkedElements.isNotEmpty())
        check(enumElements.size == checkedElements.size)

        val index = enumElements.indexOfFirst { it.first == element }
        check(index != -1)

        addChip(
            context,
            binding.chippedEdittextInput.editableText,
            enumElements[index].second
        )

        val wasChecked = checkedElements[index]
        checkedElements[index] = true
        return wasChecked
    }

    val checkedIndex: Int?
    get() {
        check(selectionMode == SINGLE_CHOICE)
        check(checkedElements.count { it } <= 1)

        val result = checkedElements.indexOfFirst { it }
        return if (result != -1) result else null
    }

    val checkedIndices: List<Int>?
    get() {
        check(selectionMode == MULTI_CHOICE)

        val result = checkedElements.indices.filter { checkedElements[it] }
        return if (result.isNotEmpty()) result else null
    }

    /*val checkedElementAsString: String?
    get() {
        return checkedIndex?.let { index -> stringElements[index] }
    }*/

    val checkedElementAsStrings: List<String>?
    get() {
        return checkedIndices?.map { index -> stringElements[index] }
    }

    val checkedElementAsInteger: Int?
        get() {
            return checkedIndex?.let { index -> integerElements[index] }
        }

    /*val checkedElementAsIntegers: List<Int>?
        get() {
            return checkedIndices?.map { index -> integerElements[index] }
        }
    */

    inline fun <reified T : Enum<T>> checkedElementAsEnumEntry(): T? {
        return checkedIndex?.let { index -> enumValueOf<T>(enumElements[index].first) }
    }

    inline fun <reified T : Enum<T>> checkedElementAsEnumEntries(): List<T>? {
        return checkedIndices?.map { index -> enumValueOf(enumElements[index].first) }
    }

    private fun setIntRangeElements(from: Int, to: Int) {
        val count = to - from + 1
        integerElements = List(count) { element -> element + from }.reversed()
    }

    /*inline fun <reified T : Enum<T>> setEnumElements() {
        val enumConstants = enumValues<T>()
        enumElements = enumConstants.map { Pair(it.name, it.toString()) }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////

    constructor(context: Context): super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun fillStringElements(typedArray: TypedArray) {
        if (typedArray.hasValue(R.styleable.ChippedEditText_stringSkipEntryName)) {
            stringSkipEntry = typedArray.getString(R.styleable.ChippedEditText_stringSkipEntryName) ?: ""
        }

        if (typedArray.hasValue(R.styleable.ChippedEditText_stringElements)) {
            stringElements = typedArray
                .getTextArray(R.styleable.ChippedEditText_stringElements)
                .map { element -> element.toString() }
                .filter { element -> element != stringSkipEntry }
            checkedElements = BooleanArray(stringElements.size) { false }
        }
    }

    private fun fillIntegerElements(typedArray: TypedArray) {
        var from: Int? = null; var to: Int? = null
        if (typedArray.hasValue(R.styleable.ChippedEditText_integerElementFrom)) {
            from = typedArray.getInt(R.styleable.ChippedEditText_integerElementFrom, -1)
            integerElementFrom = from
        }
        if (typedArray.hasValue(R.styleable.ChippedEditText_integerElementTo)) {
            to = typedArray.getInt(R.styleable.ChippedEditText_integerElementTo, -1)
            integerElementTo = to
        }
        if (from != null && to != null) {
            setIntRangeElements(from, to)
        }
    }

    private fun fillEnumerationElements(typedArray: TypedArray) {
        val enumerationJavaClass: Class<*>?

        if (typedArray.hasValue(R.styleable.ChippedEditText_enumerationSkipEntryName)) {
            enumSkipEntry = typedArray
                .getString(R.styleable.ChippedEditText_enumerationSkipEntryName) ?: ""
        }

        if (typedArray.hasValue(R.styleable.ChippedEditText_enumerationFullName)) {
            val enumerationFullName = typedArray
                .getString(R.styleable.ChippedEditText_enumerationFullName) ?: ""
            check(enumerationFullName.isNotEmpty())

            enumerationJavaClass = Class.forName(enumerationFullName)

            enumElements = enumerationJavaClass.enumConstants.map { enumEntryObj ->
                val enumEntry = enumEntryObj as Enum<*>
                Pair(enumEntry.name, enumEntry.toString())
            }.filter { enumEntryStr -> enumEntryStr.first != enumSkipEntry }
            checkedElements = BooleanArray(enumElements.size) { false }
        }
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     * the current context for the view.
     * @param attrs
     * the attributes from view XML-file
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        _binding = ViewChippedEdittextBinding.inflate(LayoutInflater.from(context), this)
        check(_binding != null)

        dropDownDrawable = AppCompatResources.getDrawable(
            context,
            R.drawable.ic_baseline_arrow_drop_down_24
        )!!
        clearDrawable = AppCompatResources.getDrawable(
            context,
            R.drawable.ic_baseline_clear_24
        )!!

        if (attrs != null) {
            val typedArray: TypedArray = context
                .obtainStyledAttributes(attrs, R.styleable.ChippedEditText)

            if (typedArray.hasValue(R.styleable.ChippedEditText_title)) {
                title = typedArray.getString(R.styleable.ChippedEditText_title) ?: ""
            }

            if (typedArray.hasValue(R.styleable.ChippedEditText_selectionMode)) {
                selectionMode = when (val intValue = typedArray.getInt(R.styleable.ChippedEditText_selectionMode, SINGLE_CHOICE)) {
                    SINGLE_CHOICE -> SINGLE_CHOICE
                    MULTI_CHOICE -> MULTI_CHOICE
                    else -> {
                        Timber.e("Invalid selection mode value $intValue!")
                        SINGLE_CHOICE
                    }
                }
            }

            if (typedArray.hasValue(R.styleable.ChippedEditText_elementType)) {
                when (val intValue = typedArray.getInt(R.styleable.ChippedEditText_elementType, STRING_ELEMENT_TYPE)) {
                    STRING_ELEMENT_TYPE -> {
                        elementType = STRING_ELEMENT_TYPE
                        fillStringElements(typedArray)
                    }
                    INT_ELEMENT_TYPE -> {
                        elementType = INT_ELEMENT_TYPE
                        fillIntegerElements(typedArray)
                    }
                    ENUM_ELEMENT_TYPE -> {
                        elementType = ENUM_ELEMENT_TYPE
                        fillEnumerationElements(typedArray)
                    }
                    else -> {
                        Timber.e("Invalid element type $intValue!")
                        STRING_ELEMENT_TYPE
                    }
                }
            } else {
                // Default type is string
                elementType = STRING_ELEMENT_TYPE
                fillStringElements(typedArray)
            }

            checkedElements = if (typedArray.hasValue(R.styleable.ChippedEditText_checkedElements)) {
                typedArray
                    .getTextArray(R.styleable.ChippedEditText_checkedElements)
                    .map { element -> element.toString().toBoolean() }
                    .toBooleanArray()
            } else {
                // NOTE: checked element array size must be synchronized with elements themselves
                BooleanArray(elementsSize) { false }
            }

            typedArray.recycle()
        }

        setupFilter()
    }

    private fun setupFilter() {
        binding.chippedEdittextInput.setOnFocusChangeListener { _, gotFocus -> if (gotFocus) showDialog() }
        binding.chippedEdittextInput.setOnClickListener { showDialog() }

        // Disable software keyboard, because modal dialog used instead
        binding.chippedEdittextInput.showSoftInputOnFocus = false

        setEndIcon(EndIconType.Dropdown)
    }

    private fun addChip(context: Context, editable: Editable, text: String) {
        // Prepare text to be replaced with chip(s)
        val prevLength = editable.length
        editable.append(text)

        // Create chip from resource file
        val drawable = ChipDrawable.createFromResource(context, R.xml.standalone_sort_chip)
        drawable.isChipIconVisible = false
        drawable.text = text
        drawable.bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        if (selectionMode == SINGLE_CHOICE)
            drawable.chipBackgroundColor = binding.chippedEdittextInput.backgroundTintList

        // Add chip as image span
        val span = ImageSpan(drawable)
        editable.setSpan(span, prevLength, editable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun removeChips(editable: Editable) {
        //editable.clearSpans()
        editable.clear()
    }

    private enum class EndIconType { Clear, Dropdown, None }

    private fun setEndIcon(type: EndIconType) {
        when (type) {
            EndIconType.Clear -> {
                // NOTE: this flag somehow force icon to disappear, so we have to use custom icons
                /*binding.chippedEdittextInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT*/
                binding.chippedEdittextInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.chippedEdittextInputLayout.endIconDrawable = clearDrawable
            }
            EndIconType.Dropdown -> {
                // NOTE: cause exception "EditText needs to be an AutoCompleteTextView if an Exposed Dropdown Menu is being used."
                //binding.chippedEdittextInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                binding.chippedEdittextInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.chippedEdittextInputLayout.endIconDrawable = dropDownDrawable
            }
            EndIconType.None -> {
                binding.chippedEdittextInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                binding.chippedEdittextInputLayout.endIconDrawable = null
            }
        }

        setEndIconListener()
    }

    private fun setEndIconListener() {
        binding.chippedEdittextInputLayout.setEndIconOnClickListener {
            if (checkedElements.indexOfFirst { it } != -1) {
                binding.chippedEdittextInput.editableText.clear()
                setEndIcon(EndIconType.Dropdown)

                checkedElements.fill(false)
            } else {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        val elements = displayableElements.toTypedArray()

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setNeutralButton(resources.getString(R.string.dialog_cancel_button)) { _, _ -> // dialog, which ->
                // TODO: Respond to neutral button press
            }
            .setPositiveButton(resources.getString(R.string.dialog_ok_button)) { _, _ -> //dialog, which ->
                // Respond to positive button press
                onDialogOk(elements)
            }

        when (selectionMode) {
            SINGLE_CHOICE -> {
                // Single-choice items (initialized with checked item)
                builder.setSingleChoiceItems(
                    elements,
                    checkedElements.indexOfFirst { it }) { _, which -> // dialog, which
                    // Respond to item chosen
                    Timber.d("Selected element index: $which")
                    check(which >= -1 && which < elements.size)
                    checkedElements.fill(false)
                    checkedElements[which] = true
                }.show()
            }
            MULTI_CHOICE -> {
                //Multi-choice items (initialized with checked items)
                builder.setMultiChoiceItems(
                    elements,
                    checkedElements
                ) { _, _, _ -> //dialog, which, checked ->
                    // TODO: Respond to item chosen
                }.show()
            }
            else -> throw InvalidParameterException("Invalid `selectionMode` value!")
        }
    }

    private fun onDialogOk(elements: Array<String>) {
        // Clear previously added chips if any
        removeChips(binding.chippedEdittextInput.editableText)

        // Add either genre chip if one genre was selected,
        // or genre chip and "+n" chip to show to user that multiple genres were selected
        val firstSelectedIndex = checkedElements.indexOfFirst { it }
        if (firstSelectedIndex != -1) {
            addChip(
                context,
                binding.chippedEdittextInput.editableText,
                elements[firstSelectedIndex]
            )

            // Is two or more elements selected?
            val selectedCount = checkedElements.count { it }
            if (selectedCount >= 2) {
                addChip(
                    context, binding.chippedEdittextInput.editableText,
                    "+" + (selectedCount - 1).toString()
                )
            }

            setEndIcon(EndIconType.Clear)
        }
    }

    companion object {
        // Data Binding properties support
        @JvmStatic
        @BindingAdapter(value = ["integerElementFrom", "integerElementTo"], requireAll = false)
        fun setIntegerElementToBinding(cet: ChippedEditText, from: Int?, to: Int?) {
            // Update properties from binding if already not set as static values in XML
            if (from != null && cet.integerElementFrom == null) {
                cet.integerElementFrom = from
            }
            if (to != null && cet.integerElementTo == null) {
                cet.integerElementTo = to
            }

            if (cet.integerElementFrom != null && cet.integerElementTo != null) {
                cet.setIntRangeElements(cet.integerElementFrom!!, cet.integerElementTo!!)
            }
        }

        @IntDef(SINGLE_CHOICE, MULTI_CHOICE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class SelectionModeAnnotation

        @IntDef(STRING_ELEMENT_TYPE, INT_ELEMENT_TYPE, ENUM_ELEMENT_TYPE)
        annotation class ElementTypeAnnotation

        const val SINGLE_CHOICE = 0
        const val MULTI_CHOICE = 1

        const val STRING_ELEMENT_TYPE = 0
        const val INT_ELEMENT_TYPE = 1
        const val ENUM_ELEMENT_TYPE = 2
    }
}
