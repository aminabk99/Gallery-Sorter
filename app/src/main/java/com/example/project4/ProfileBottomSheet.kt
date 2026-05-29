package com.example.project4

import android.os.Bundle
import android.widget.TextView
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Bottom sheet that displays a numbered list of deleted media item names.
 */
class ProfileBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val context = requireContext()
        val names = arguments?.getStringArrayList(ARG_NAMES).orEmpty()
        val paddingPx = (24 * resources.displayMetrics.density).toInt()

        val textView = TextView(context).apply {
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            textSize = 16f
            isVerticalScrollBarEnabled = true
            text = buildDeletedList(names)
        }

        return BottomSheetDialog(context, theme).apply {
            setContentView(textView)
        }
    }

    private fun buildDeletedList(names: List<String>): String {
        if (names.isEmpty()) return "No deleted items yet."
        return buildString {
            append("Deleted items\n\n")
            names.forEachIndexed { index, name ->
                append("${index + 1}. $name\n")
            }
        }
    }

    companion object {
        private const val ARG_NAMES = "names"

        fun newInstance(names: List<String>): ProfileBottomSheet =
            ProfileBottomSheet().apply {
                arguments = bundleOf(ARG_NAMES to ArrayList(names))
            }
    }
}
