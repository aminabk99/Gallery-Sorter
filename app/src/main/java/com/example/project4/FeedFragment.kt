package com.example.project4

import android.Manifest
import android.app.AlertDialog
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.project4.databinding.FragmentFeedBinding

/**
 * Main screen: a vertical swipe feed of the device's photos and videos.
 * Swipe left on any item to delete it. Tap the profile button to view deleted items.
 */
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MediaCleanupViewModel by activityViewModels()
    private lateinit var adapter: MediaPagerAdapter

    private var pendingDeletePosition: Int = RecyclerView.NO_POSITION

    // Handles the runtime permission request for media access
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if (results.values.all { it }) {
                viewModel.loadMedia()
            } else {
                binding.progressBar.visibility = View.GONE
                showPermissionDialog()
                updateEmptyState(adapter.isEmpty())
            }
        }

    // Handles the system delete confirmation dialog (Android 11+)
    private val deleteLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val position = pendingDeletePosition
            if (result.resultCode == android.app.Activity.RESULT_OK && position != RecyclerView.NO_POSITION) {
                viewModel.removeMediaAt(position)
            } else if (position != RecyclerView.NO_POSITION) {
                adapter.notifyItemChanged(position)
                viewModel.restoreSwipe(position)
            }
            pendingDeletePosition = RecyclerView.NO_POSITION
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MediaPagerAdapter(mutableListOf(), onProfileTap = { openDeletedItemsScreen() })

        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        binding.btnGrantAccess.setOnClickListener { ensureMediaAccessAndLoad() }
        binding.btnProfile.setOnClickListener { openDeletedItemsScreen() }

        attachSwipeToDelete()
        observeViewModel()
        ensureMediaAccessAndLoad()
    }

    private fun observeViewModel() {
        viewModel.mediaItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            updateEmptyState(items.isEmpty())
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.consumeMessage()
            }
        }
    }

    private fun ensureMediaAccessAndLoad() {
        if (hasMediaPermissions()) {
            viewModel.loadMedia()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        permissionLauncher.launch(requiredPermissions())
    }

    private fun attachSwipeToDelete() {
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return

                val item = adapter.getItem(position)
                pendingDeletePosition = position

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val pendingIntent = MediaStore.createDeleteRequest(
                        requireContext().contentResolver,
                        listOf(item.uri)
                    )
                    try {
                        deleteLauncher.launch(
                            IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                        )
                    } catch (_: IntentSender.SendIntentException) {
                        adapter.notifyItemChanged(position)
                        pendingDeletePosition = RecyclerView.NO_POSITION
                        Toast.makeText(requireContext(), "Could not start delete request.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    viewModel.moveItemToTrash(position) { success ->
                        if (!success) adapter.notifyItemChanged(position)
                        pendingDeletePosition = RecyclerView.NO_POSITION
                    }
                }
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(recyclerView)
    }

    private fun openDeletedItemsScreen() =
        findNavController().navigate(R.id.action_feedFragment_to_deletedMediaFragment)

    private fun hasMediaPermissions(): Boolean =
        requiredPermissions().all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requiredPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Media access needed")
            .setMessage("This app needs photo and video access to load your gallery into the feed and delete items when you swipe left.")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
