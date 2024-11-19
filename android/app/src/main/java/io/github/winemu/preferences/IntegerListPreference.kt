package io.github.winemu.preferences

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.winemu.R

class IntegerListPreference @JvmOverloads constructor(
    context: Context, 
    attrs: AttributeSet? = null, 
    defStyleAttr: Int = android.R.attr.dialogPreferenceStyle,  
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    private var entries: Array<CharSequence> = emptyArray()
    private var entryValues: Array<Int> = emptyArray()
    private var selectedValue: Int = 0

    init {
        // Use standard android:entries and android:entryValues attributes
        val a = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.entries, android.R.attr.entryValues))
        try {
            val entriesId = a.getResourceId(0, 0)
            if (entriesId != 0) {
                entries = context.resources.getTextArray(entriesId)
            }
            val entryValuesId = a.getResourceId(1, 0)
            if (entryValuesId != 0) {
                entryValues = context.resources.getIntArray(entryValuesId).toTypedArray()
            } else if (entries.isNotEmpty()) {
                // Auto-generate entryValues as indices if not provided
                entryValues = Array(entries.size) { it }
            }
        } finally {
            a.recycle()
        }
    }


    var value: Int
        get() = selectedValue
        set(value) {
            selectedValue = value
            persistInt(value)  // Persist the selected value as an Int
            summary = entries[findIndexOfValue(value)]  // Update the summary to show the selected entry
        }

    override fun onClick() {
        // Create the dialog with items but no OK/Cancel buttons
        MaterialAlertDialogBuilder(context)
            .setTitle(dialogTitle ?: title)
            .setSingleChoiceItems(entries, findIndexOfValue(selectedValue)) { dialog, which ->
                selectedValue = entryValues[which]  // Set selected value when an item is clicked
                if (callChangeListener(selectedValue)) {
                    persistInt(selectedValue)  // Persist the value only if listener allows the change
                    summary = entries[which]  // Update the summary
                }
                dialog.dismiss()  // Dismiss the dialog immediately after selecting
            }
            .show()
    }

    private fun findIndexOfValue(value: Int): Int {
        return entryValues.indexOf(value)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        selectedValue = getPersistedInt(defaultValue as? Int ?: entryValues[0])
        summary = entries[findIndexOfValue(selectedValue)]  // Set the summary to the current value
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.value = selectedValue
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            selectedValue = state.value
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : BaseSavedState {
        var value: Int = 0

        constructor(superState: Parcelable?) : super(superState)
        constructor(parcel: Parcel) : super(parcel) {
            value = parcel.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(value)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
