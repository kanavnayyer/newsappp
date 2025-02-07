package com.awesome.news_app.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.awesome.news_app.R


import com.awesome.news_app.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class SortBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!

    private var selectedSortOptions: MutableSet<String> = mutableSetOf()
    private var onSortOptionSelected: ((Set<String>) -> Unit)? = null


    companion object {
        private const val ARG_SELECTED_SORT_OPTIONS = "selectedSortOptions"

        fun newInstance(
            selectedSortOptions: MutableSet<String>,
            onSortOptionSelected: (Set<String>) -> Unit
        ): SortBottomSheetDialog {
            val fragment = SortBottomSheetDialog()
            val args = Bundle().apply {
                putStringArrayList(ARG_SELECTED_SORT_OPTIONS, ArrayList(selectedSortOptions))
            }
            fragment.arguments = args
            fragment.onSortOptionSelected = onSortOptionSelected
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        arguments?.let {
            selectedSortOptions = it.getStringArrayList(ARG_SELECTED_SORT_OPTIONS)?.toMutableSet() ?: mutableSetOf()
        }

        setupChip(binding.chipAuthor, R.string.author.toString())
        setupChip(binding.chipTitle, R.string.title.toString())
        setupChip(binding.chipDate, R.string.date.toString())
        setupChip(binding.chipSource, R.string.source.toString())
    }

    private fun setupChip(chip: Chip, sortType: String) {
        chip.isChecked = selectedSortOptions.contains(sortType)
        updateChipAppearance(chip)

        chip.setOnClickListener {
            if (chip.isChecked) {
                selectedSortOptions.add(sortType)
            } else {
                selectedSortOptions.remove(sortType)
            }

            updateChipAppearance(chip)
            onSortOptionSelected?.invoke(selectedSortOptions)
        }
    }

    private fun updateChipAppearance(chip: Chip) {
        chip.chipBackgroundColor = ContextCompat.getColorStateList(
            requireContext(),
            if (chip.isChecked) R.color.color2 else R.color.color
        )
        chip.isCloseIconVisible = chip.isChecked
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
