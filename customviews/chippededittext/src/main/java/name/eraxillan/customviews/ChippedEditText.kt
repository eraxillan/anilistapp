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

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.Editable
import android.text.InputType
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.annotation.IntDef
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.parcelize.Parcelize
import name.eraxillan.chippededittext.R
import name.eraxillan.chippededittext.databinding.ViewChippedEdittextBinding
import timber.log.Timber
import java.security.InvalidParameterException


/**
 * Text edit control with Material Design "chips" instead of plain text
 */
public class ChippedEditText : ConstraintLayout {

    public var onCompleteListener: (() -> Unit)? = null

    public var title: String
        get() {
            return binding.chippedEdittextTitle.text.toString()
        }
        set(value) {
            binding.chippedEdittextTitle.text = value
        }

    @SelectionModeAnnotation
    public var selectionMode: Int = SINGLE_CHOICE_SELECTION

    @ElementTypeAnnotation
    public var elementType: Int = UNKNOWN_ELEMENT_TYPE

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private var _binding: ViewChippedEdittextBinding? = null
    private val binding get() = _binding!!

    private lateinit var dropDownDrawable: Drawable
    private lateinit var clearDrawable: Drawable

    @Parcelize
    internal data class ChipData(
        val text: String,
        val startIndex: Int,
        val endIndex: Int,
    ) : Parcelable

    private var chips: MutableList<ChipData> = mutableListOf()
    private var chipsDrawn: Int = 0

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private sealed class DataType<T>(var elements: List<T>, var skipElement: T?) {

        abstract fun displayableElements(): List<String>
        abstract fun <U> findWithIndex(element: U): Pair<Int, String?>

        fun getByIndex(index: Int) = elements[index]
        fun hasElements() = elements.isNotEmpty()
        fun elementsSize(): Int = elements.size

        protected fun findWithIndexImpl(index: Int): Pair<Int, String?> {
            if (index == -1)
                return -1 to null

            return index to elements[index].toString()
        }

        object Unknown : DataType<Nothing>(emptyList(), null) {
            override fun displayableElements(): List<String> {
                throw IllegalStateException()
            }

            override fun <U> findWithIndex(element: U): Pair<Int, String?> {
                throw IllegalStateException()
            }
        }

        class StringList(elements: List<String>, skipEntry: String?)
            : DataType<String>(elements, skipEntry) {

            override fun displayableElements(): List<String> {
                return elements.filter { it != skipElement }
            }

            override fun <U> findWithIndex(element: U): Pair<Int, String?> {
                val index = elements.indexOf(element as String)
                return findWithIndexImpl(index)
            }
        }

        class IntegerRange(elements: List<Int>, skipEntry: Int?, val from: Int? = null, val to: Int? = null)
            : DataType<Int>(elements, skipEntry) {

            override fun displayableElements(): List<String> {
                return elements.map { it.toString() }.filter { it != skipElement.toString() }
            }

            override fun <U> findWithIndex(element: U): Pair<Int, String?> {
                val index = elements.indexOf(element as Int)
                return findWithIndexImpl(index)
            }
        }

        class EnumElementsList(elements: List<Enum<*>>, skipEntry: Enum<*>?, val enumerationName: String)
            : DataType<Enum<*>>(elements, skipEntry) {

            override fun displayableElements(): List<String> {
                return elements.map { it.toString() }.filter { it != skipElement.toString() }
            }

            override fun <U> findWithIndex(element: U): Pair<Int, String?> {
                val index = elements.indexOf(element as Enum<*>)
                return findWithIndexImpl(index)
            }
        }
    }

    private var elements: DataType<*> = DataType.Unknown
    private var checkedElements: BooleanArray = booleanArrayOf()

    private fun <T> checkElementImpl(element: T): Boolean {
        check(elements.hasElements())
        check(checkedElements.isNotEmpty())
        check(elements.elementsSize() == checkedElements.size)

        val (indexFound, elementFound) = elements.findWithIndex(element)
        check(indexFound != -1)
        check(elementFound != null)

        if (chips.firstOrNull { it.text == element.toString() } == null) {
            addChip(elementFound)
        }

        val wasChecked = checkedElements[indexFound]
        checkedElements[indexFound] = true
        return wasChecked
    }

    public fun checkElement(element: String?): Boolean {
        check(elementType == STRING_ELEMENT_TYPE)
        if (element == null) return false
        check(element.isNotEmpty())
        return checkElementImpl(element)
    }

    public fun checkElement(element: Int?): Boolean {
        check(elementType == INTEGER_ELEMENT_TYPE)
        if (element == null) return false
        return checkElementImpl(element)
    }

    public fun <T : Enum<T>> checkElement(element: T?): Boolean {
        check(elementType == ENUMERATION_ELEMENT_TYPE)
        if (element == null) return false
        return checkElementImpl(element)
    }

    @JvmName("checkElementsString")
    public fun checkElements(elements: List<String>?) {
        elements?.forEach { checkElement(it) }
    }

    @JvmName("checkElementsInt")
    public fun checkElements(elements: List<Int>?) {
        elements?.forEach { checkElement(it) }
    }

    @JvmName("checkElementsEnum")
    public fun <T : Enum<T>> checkElements(elements: List<T>?) {
        elements?.forEach { checkElement(it) }
    }

    public val checkedIndex: Int?
    get() {
        check(selectionMode == SINGLE_CHOICE_SELECTION)
        check(checkedElements.count { it } <= 1)

        val result = checkedElements.indexOfFirst { it }
        return if (result != -1) result else null
    }

    public val checkedIndices: List<Int>?
    get() {
        check(selectionMode == MULTI_CHOICE_SELECTION)

        val result = checkedElements.indices.filter { checkedElements[it] }
        return if (result.isNotEmpty()) result else null
    }

    public fun <T> checkedElementsValues(): List<T>? {
        return checkedIndices?.map { index ->
            val element = elements.getByIndex(index)
            @Suppress("UNCHECKED_CAST")
            element as T
        }
    }

    public fun <T> checkedElementValue(): T? {
        return checkedIndex?.let { index ->
            val element = elements.getByIndex(index)
            @Suppress("UNCHECKED_CAST")
            element as T
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public constructor(context: Context): super(context) {
        init(context, null)
    }

    public constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(context, attrs)
    }

    public constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context, attrs)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            childrenStates = saveChildViewStates()

            hasFocus = binding.chippedEdittextInput.hasFocus()
            chips = this@ChippedEditText.chips
            checkedElements = this@ChippedEditText.checkedElements
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                state.childrenStates?.let { restoreChildViewStates(it) }

                chipsDrawn = 0
                binding.chippedEdittextInput.editableText.clear()
                state.chips.forEach { chip -> addChip(chip.text) }
                this.checkedElements = state.checkedElements

                if (state.hasFocus) {
                    check(binding.chippedEdittextInput.requestFocus())
                }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    // https://www.netguru.com/blog/how-to-correctly-save-the-state-of-a-custom-view-in-android

    protected override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        //super.dispatchSaveInstanceState(container)

        // As we save our own instance state, ensure our children don't save
        // and restore their state as well
        super.dispatchFreezeSelfOnly(container)
    }

    protected override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        //super.dispatchRestoreInstanceState(container)

        /** See comment in {@link #dispatchSaveInstanceState(android.util.SparseArray)} */
        super.dispatchThawSelfOnly(container)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun fillStringElements(stringElements: List<String>, skipEntry: String?) {
        elements = DataType.StringList(stringElements, skipEntry)
        checkedElements = BooleanArray(elements.elementsSize()) { false }
        onCompleteListener?.invoke()
    }

    private fun fillStringElements(typedArray: TypedArray) {
        var skipEntry : String? = null
        if (typedArray.hasValue(R.styleable.ChippedEditText_stringElementSkip)) {
            skipEntry = typedArray.getString(R.styleable.ChippedEditText_stringElementSkip) ?: ""
        }

        var stringElementEntries: List<String> = emptyList()
        if (typedArray.hasValue(R.styleable.ChippedEditText_stringElementEntries)) {
            stringElementEntries = typedArray
                .getTextArray(R.styleable.ChippedEditText_stringElementEntries)
                .map { element -> element.toString() }
                .filter { element -> element != skipEntry }
        }

        elements = DataType.StringList(stringElementEntries, skipEntry)
        checkedElements = BooleanArray(elements.elementsSize()) { false }
        onCompleteListener?.invoke()
    }

    private fun fillIntegerElementsFromRange(from: Int, to: Int, skipEntry: Int?) {
        val count = to - from + 1
        val integerElements = List(count) { element -> element + from }.reversed()

        elements = DataType.IntegerRange(integerElements, skipEntry, from, to)
        checkedElements = BooleanArray(elements.elementsSize()) { false }
        onCompleteListener?.invoke()
    }

    private fun fillIntegerElements(typedArray: TypedArray) {
        var skipEntry : Int? = null
        if (typedArray.hasValue(R.styleable.ChippedEditText_integerElementSkip)) {
            skipEntry = typedArray.getInt(R.styleable.ChippedEditText_integerElementSkip, -1)
        }

        var from: Int? = null; var to: Int? = null
        if (typedArray.hasValue(R.styleable.ChippedEditText_integerElementFrom)) {
            from = typedArray.getInt(R.styleable.ChippedEditText_integerElementFrom, -1)
        }
        if (typedArray.hasValue(R.styleable.ChippedEditText_integerElementTo)) {
            to = typedArray.getInt(R.styleable.ChippedEditText_integerElementTo, -1)
        }

        if (from != null && to != null) {
            fillIntegerElementsFromRange(from, to, skipEntry)
        } else {
            elements = DataType.IntegerRange(emptyList(), null, from, to)
        }
    }

    private fun fillEnumerationElements(className: String?, skipElement: String?) {
        if (className == null)
            return

        val enumerationJavaClass = Class.forName(className)
        check(enumerationJavaClass.isEnum)

        var skipEntry: Enum<*>? = null
        if (skipElement != null) {
            @Suppress("UNCHECKED_CAST")
            val enumerationJavaClassObj = enumerationJavaClass as Class<out Enum<*>>
            skipEntry = java.lang.Enum.valueOf(enumerationJavaClassObj, skipElement)
        }

        elements = DataType.EnumElementsList(
            enumerationJavaClass.enumConstants.map { it as Enum<*> },
            skipEntry,
            className
        )
        checkedElements = BooleanArray(enumerationJavaClass?.enumConstants?.size ?: 0) { false }
        onCompleteListener?.invoke()
    }

    private fun fillEnumerationElements(typedArray: TypedArray) {
        val enumerationElementClassName: String
        val enumerationJavaClass: Class<*>
        if (typedArray.hasValue(R.styleable.ChippedEditText_enumerationElementClassName)) {
            enumerationElementClassName = typedArray
                .getString(R.styleable.ChippedEditText_enumerationElementClassName) ?: ""
            check(enumerationElementClassName.isNotEmpty())

            enumerationJavaClass = Class.forName(enumerationElementClassName)
            check(enumerationJavaClass.isEnum)
        }
        else
            return

        var skipEntry : Enum<*>? = null
        if (typedArray.hasValue(R.styleable.ChippedEditText_enumerationElementSkip)) {
            val skipEntryString = typedArray
                .getString(R.styleable.ChippedEditText_enumerationElementSkip) ?: ""

            @Suppress("UNCHECKED_CAST")
            val enumerationJavaClassObj = enumerationJavaClass as Class<out Enum<*>>
            skipEntry = java.lang.Enum.valueOf(enumerationJavaClassObj, skipEntryString)
        }

        elements = DataType.EnumElementsList(
            enumerationJavaClass.enumConstants.map { it as Enum<*> },
            skipEntry,
            enumerationElementClassName
        )
        checkedElements = BooleanArray(enumerationJavaClass?.enumConstants?.size ?: 0) { false }
        onCompleteListener?.invoke()
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
                selectionMode = when (val intValue = typedArray.getInt(R.styleable.ChippedEditText_selectionMode, SINGLE_CHOICE_SELECTION)) {
                    SINGLE_CHOICE_SELECTION -> SINGLE_CHOICE_SELECTION
                    MULTI_CHOICE_SELECTION -> MULTI_CHOICE_SELECTION
                    else -> {
                        Timber.e("Invalid selection mode value $intValue!")
                        SINGLE_CHOICE_SELECTION
                    }
                }
            }

            if (typedArray.hasValue(R.styleable.ChippedEditText_elementType)) {
                when (typedArray.getInt(R.styleable.ChippedEditText_elementType, UNKNOWN_ELEMENT_TYPE)) {
                    STRING_ELEMENT_TYPE -> {
                        elementType = STRING_ELEMENT_TYPE
                        fillStringElements(typedArray)
                    }
                    INTEGER_ELEMENT_TYPE -> {
                        elementType = INTEGER_ELEMENT_TYPE
                        fillIntegerElements(typedArray)
                    }
                    ENUMERATION_ELEMENT_TYPE -> {
                        elementType = ENUMERATION_ELEMENT_TYPE
                        fillEnumerationElements(typedArray)
                    }
                    else -> {
                        check(false)
                        STRING_ELEMENT_TYPE
                    }
                }
            } else {
                // Default type is string
                elementType = STRING_ELEMENT_TYPE
                fillStringElements(typedArray)
            }

            // NOTE: checked element array size must be synchronized with elements themselves
            BooleanArray(elements.elementsSize()) { false }

            typedArray.recycle()
        }

        setupEditTextProperties()
        setupEditTextClickListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEditTextClickListener() {
        binding.chippedEdittextInput.setOnTouchListener { _, motionEvent ->
            if (motionEvent?.action == MotionEvent.ACTION_UP) {
                //performClick()
                showDialog()
            }
            false
        }
    }

    private fun setupEditTextProperties() {
        // Disable EditText functionality
        binding.chippedEdittextInput.inputType = InputType.TYPE_NULL

        // Disable software keyboard, because modal dialog used instead
        binding.chippedEdittextInput.showSoftInputOnFocus = false

        setEndIcon(EndIconType.Dropdown)
    }

    private fun createChipDrawable(text: String): ChipDrawable {
        val drawable = ChipDrawable.createFromResource(context, R.xml.standalone_sort_chip)
        drawable.isChipIconVisible = false
        drawable.text = text
        drawable.bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        if (selectionMode == SINGLE_CHOICE_SELECTION)
            drawable.chipBackgroundColor = binding.chippedEdittextInput.backgroundTintList
        return drawable
    }

    private fun addChip(text: String) {
        val startIndex = chips.lastOrNull()?.endIndex ?: 0
        val endIndex = startIndex + text.length
        chips.add(ChipData(text, startIndex, endIndex))

        binding.chippedEdittextInput.doOnLayout {
            // Check whether chips were already drawn
            if (chipsDrawn >= chips.size) {
                return@doOnLayout
            }

            // Check if summary width of chips more than EditText one
            var lastIndex = chips.size
            var chipsText = ""
            var chipsWidth = binding.chippedEdittextInput.paddingLeft
            val editTextWidth = width - binding.chippedEdittextInput.paddingLeft - binding.chippedEdittextInput.paddingRight
            val chipsDrawables: MutableList<ChipDrawable> = mutableListOf()
            for (i in 0 until chips.size) {
                // Create chip from resource file
                val drawable = createChipDrawable(chips[i].text)
                chipsDrawables.add(drawable)
                chipsWidth += drawable.intrinsicWidth

                if (chipsWidth >= editTextWidth) {
                    lastIndex = i
                    break
                }

                chipsText += chips[i].text
            }

            var leftoverText = ""
            val leftoverChipsSize = chips.size - lastIndex
            for (i in lastIndex until chips.size) {
                leftoverText += chips[i].text
            }

            // Prepare text to be replaced with chip(s)
            binding.chippedEdittextInput.editableText.clear()
            binding.chippedEdittextInput.editableText.append(chipsText)

            for (i in 0 until lastIndex) {
                val span = ImageSpan(chipsDrawables[i])
                binding.chippedEdittextInput.editableText.setSpan(
                    span, chips[i].startIndex, chips[i].endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (lastIndex != chips.size) {
                val plusStr = "+$leftoverChipsSize"
                val plusStrStartIndex = binding.chippedEdittextInput.editableText.length
                val plusStrEndIndex = plusStrStartIndex + plusStr.length
                binding.chippedEdittextInput.editableText.append(plusStr)

                val plusDrawable = createChipDrawable(plusStr)
                val span = ImageSpan(plusDrawable)
                binding.chippedEdittextInput.editableText.setSpan(
                    span, plusStrStartIndex, plusStrEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            chipsDrawn++
        }
    }

    private fun removeChips(editable: Editable) {
        //editable.clearSpans()
        chipsDrawn = 0
        editable.clear()

        chips.clear()
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
                removeChips(binding.chippedEdittextInput.editableText)
                setEndIcon(EndIconType.Dropdown)

                checkedElements.fill(false)
            } else {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        val displayableElements = elements.displayableElements().toTypedArray()

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setNeutralButton(resources.getString(R.string.dialog_cancel_button)) { _, _ -> // dialog, which ->
                // Respond to neutral button press here
            }
            .setPositiveButton(resources.getString(R.string.dialog_ok_button)) { _, _ -> //dialog, which ->
                // Respond to positive button press
                onDialogOk(displayableElements)
            }

        when (selectionMode) {
            SINGLE_CHOICE_SELECTION -> {
                // Single-choice items (initialized with checked item)
                builder.setSingleChoiceItems(
                    displayableElements,
                    checkedElements.indexOfFirst { it }) { _, which -> // dialog, which
                        // Respond to item chosen
                        Timber.d("Selected element index: $which")
                        check(which >= -1 && which < elements.elementsSize())
                        checkedElements.fill(false)
                        checkedElements[which] = true
                    }.show()
            }
            MULTI_CHOICE_SELECTION -> {
                //Multi-choice items (initialized with checked items)
                builder.setMultiChoiceItems(
                    displayableElements,
                    checkedElements
                ) { _, _, _ -> // dialog, which, checked ->
                    // Respond to item chosen here
                }.show()
            }
            else -> throw InvalidParameterException("Invalid `selectionMode` value!")
        }
    }

    private fun onDialogOk(elements: Array<String>) {
        // Clear previously added chips if any
        chipsDrawn = 0
        removeChips(binding.chippedEdittextInput.editableText)

        checkedElements.forEachIndexed { index, isChecked ->
            if (isChecked) {
                addChip(elements[index])
                setEndIcon(EndIconType.Clear)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Parcelize
    internal data class SavedState(
        val state: Parcelable?,
        var childrenStates: SparseArray<Parcelable>? = null,
        var hasFocus: Boolean = false,
        var chips: MutableList<ChipData> = mutableListOf(),
        var checkedElements: BooleanArray = booleanArrayOf(),
    ) : BaseSavedState(state) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SavedState

            if (childrenStates != other.childrenStates) return false
            if (hasFocus != other.hasFocus) return false
            if (chips != other.chips) return false
            if (!checkedElements.contentEquals(other.checkedElements)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = childrenStates?.hashCode() ?: 0
            result = 31 * result + hasFocus.hashCode()
            result = 31 * result + chips.hashCode()
            result = 31 * result + checkedElements.contentHashCode()
            return result
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public companion object {
        // Data Binding adapters

        //@JvmStatic
        @BindingAdapter(
            value = ["title"]
        )
        public fun setTitleBindingAdapter(cet: ChippedEditText, title: String?) {
            if (title != null)
                cet.title = title
        }

        //@JvmStatic
        @BindingAdapter(
            value = ["selectionMode"]
        )
        public fun setSelectionModeBindingAdapter(cet: ChippedEditText, selectionMode: Int?) {
            if (selectionMode != null)
                cet.selectionMode = selectionMode
        }

        //@JvmStatic
        @BindingAdapter(
            value = ["checkedElement"],
        )
        public fun setCheckedElementBindingAdapter(cet: ChippedEditText, checkedElement: String?) {
            if (checkedElement == null)
                return

            when (cet.elementType) {
                STRING_ELEMENT_TYPE -> cet.checkElement(checkedElement)
                INTEGER_ELEMENT_TYPE -> cet.checkElement(checkedElement.toInt())
                ENUMERATION_ELEMENT_TYPE -> {
                    check(cet.elements is DataType.EnumElementsList)
                    val elements = cet.elements as DataType.EnumElementsList
                    check(elements.enumerationName.isNotEmpty())

                    val checkedElementClass = Class.forName(elements.enumerationName)
                    check(checkedElementClass.isEnum)
                    @Suppress("UNCHECKED_CAST")
                    val checkedElementEnumClass = checkedElementClass as Class<out Enum<*>>
                    val checkedElementEnum =
                        java.lang.Enum.valueOf(checkedElementEnumClass, checkedElement)
                    //cet.checkElement<Enum<*>>(checkedElementEnum)
                    cet.checkElementImpl(checkedElementEnum)
                }
            }
        }

        //@JvmStatic
        @BindingAdapter(
            value = ["checkedElements"]
        )
        public fun setCheckedElementsBindingAdapter(cet: ChippedEditText, checkedElements: List<String>?) {
            if (checkedElements == null)
                return

            when (cet.elementType) {
                STRING_ELEMENT_TYPE -> cet.checkElements(checkedElements)
                INTEGER_ELEMENT_TYPE -> cet.checkElements(checkedElements.map { it.toInt() })
                ENUMERATION_ELEMENT_TYPE -> {
                    check(cet.elements is DataType.EnumElementsList)
                    val elements = cet.elements as DataType.EnumElementsList
                    check(elements.enumerationName.isNotEmpty())

                    val checkedElementClass = Class.forName(elements.enumerationName)
                    check(checkedElementClass.isEnum)
                    @Suppress("UNCHECKED_CAST")
                    val checkedElementEnumClass = checkedElementClass as Class<out Enum<*>>

                    val checkedElementsEnum = checkedElements.map { java.lang.Enum.valueOf(checkedElementEnumClass, it) }
                    checkedElementsEnum.forEach { cet.checkElementImpl(it) }
                }
            }
        }

        //@JvmStatic
        @BindingAdapter(
            value = ["elementType"]
        )
        public fun setElementTypeBindingAdapter(cet: ChippedEditText, elementType: Int?) {
            if (elementType == null)
                return

            cet.elementType = elementType
        }

        @JvmStatic
        @BindingAdapter(
            value = ["stringElementEntries", "stringElementSkip"],
            requireAll = false
        )
        public fun setStringElementsBindingAdapter(
            cet: ChippedEditText, stringElements: List<String>?, skipElement: String?
        ) {
            check(cet.elements is DataType.StringList)
            val elements = cet.elements as DataType.StringList

            val elementsTemp = if (stringElements != null && elements.elements.isEmpty())
                stringElements else elements.elements
            val skipElementTemp = if (skipElement != null && elements.skipElement == null)
                skipElement else elements.skipElement

            cet.fillStringElements(elementsTemp, skipElementTemp)
        }

        @JvmStatic
        @BindingAdapter(
            value = ["integerElementFrom", "integerElementTo", "integerElementSkip"],
            requireAll = false
        )
        public fun setIntegerElementsBinding(
            cet: ChippedEditText, from: Int?, to: Int?, skipElement: Int?
        ) {
            // Update properties from binding if already not set as static values in XML
            check(cet.elements is DataType.IntegerRange)
            val elements = cet.elements as DataType.IntegerRange

            val fromTemp = if (from != null && elements.from == null) from else elements.from
            val toTemp = if (to != null && elements.to == null) to else elements.to
            val skipElementTemp = if (skipElement != null && elements.skipElement == null)
                skipElement else elements.skipElement

            if (fromTemp != null && toTemp != null) {
                cet.fillIntegerElementsFromRange(fromTemp, toTemp, skipElementTemp)
            }
        }

        @JvmStatic
        @BindingAdapter(
            value = ["enumerationElementClassName", "enumerationElementSkip"],
            requireAll = false
        )
        public fun setEnumerationElementsBinding(
            cet: ChippedEditText, className: String?, skipElement: String?
        ) {
            check(cet.elements is DataType.EnumElementsList)
            val elements = cet.elements as DataType.EnumElementsList

            val classNameTemp = if (className != null && elements.enumerationName.isEmpty())
                className else elements.enumerationName
            val skipElementTemp = if (skipElement != null && elements.skipElement == null)
                skipElement else (elements.skipElement?.name ?: "")

            cet.fillEnumerationElements(classNameTemp, skipElementTemp)
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        @IntDef(SINGLE_CHOICE_SELECTION, MULTI_CHOICE_SELECTION)
        @Retention(AnnotationRetention.SOURCE)
        private annotation class SelectionModeAnnotation

        @IntDef(
            UNKNOWN_ELEMENT_TYPE,
            STRING_ELEMENT_TYPE,
            INTEGER_ELEMENT_TYPE,
            ENUMERATION_ELEMENT_TYPE
        )
        private annotation class ElementTypeAnnotation

        public const val SINGLE_CHOICE_SELECTION : Int = 0
        public const val MULTI_CHOICE_SELECTION : Int = 1

        public const val UNKNOWN_ELEMENT_TYPE : Int = 0
        public const val STRING_ELEMENT_TYPE : Int = 1
        public const val INTEGER_ELEMENT_TYPE : Int = 2
        public const val ENUMERATION_ELEMENT_TYPE : Int = 3
    }
}
