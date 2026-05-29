package com.example.project4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.project4.databinding.FragmentDeletedMediaBinding

/**
 * Displays a numbered list of all deleted media item names.
 * Navigates back to the feed via the back button.
 */
class DeletedMediaFragment : Fragment() {

    private var _binding: FragmentDeletedMediaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MediaCleanupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeletedMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToFeed.setOnClickListener { findNavController().navigateUp() }

        viewModel.deletedNames.observe(viewLifecycleOwner) { names ->
            binding.tvDeletedList.text = if (names.isEmpty()) {
                "No deleted items yet."
            } else {
                buildString {
                    append("Deleted items\n\n")
                    names.forEachIndexed { index, name ->
                        append("${index + 1}. $name\n")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
