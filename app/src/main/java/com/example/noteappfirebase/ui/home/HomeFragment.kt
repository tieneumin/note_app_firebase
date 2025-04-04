package com.example.noteappfirebase.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.FragmentHomeBinding
import com.example.noteappfirebase.R
import com.example.noteappfirebase.R.layout.dialog_delete
import com.example.noteappfirebase.ui.adapter.NoteAdapter
import com.example.noteappfirebase.ui.bottom_sheet.BottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupUiComponents()
        setupStateObserver()
    }

    private fun setupAdapter() {
        adapter = NoteAdapter(emptyList())
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        adapter.listener = object : NoteAdapter.Listener {
            override fun onClickNote(id: String) {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(id)
                findNavController().navigate(action)
            }

            override fun onLongClickNote(id: String) {
                if (id != null){
                    BottomSheetFragment(
                        onDelete = {
                            val dialogView = LayoutInflater.from(requireContext()).inflate(dialog_delete, null)

                            val dialog = AlertDialog.Builder(requireContext())
                                .setView(dialogView)
                                .create()

                            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                                dialog.dismiss()
                            }

                            dialogView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                                viewModel.handleIntent(HomeIntent.DeleteNote(id))
                                viewModel.handleIntent(HomeIntent.ClearMessage)
                                dialog.dismiss()
                            }

                            dialog.show()
                        },
                        onEdit = {
                            val action = HomeFragmentDirections.actionHomeFragmentToEditFragment(id)
                            findNavController().navigate(action)
                        }
                    ).show(childFragmentManager, "NoteBottomSheetFragment")
                }
            }
        }

    }

    private fun setupUiComponents() {
        binding.fabAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToAdd()
            findNavController().navigate(action)
        }
    }

    private fun setupStateObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.run {
                    adapter.setNotes(state.notes)
                    tvLoading.isVisible = state.isLoading
                    if (!state.isLoading && state.errorMessage.isNullOrEmpty()) {
                        tvEmpty.isVisible = state.notes.isEmpty()
                    }
                    state.errorMessage?.let {
                        showErrorSnackbar(requireView(), it, requireContext())
                    }
                }
            }
        }
    }
}