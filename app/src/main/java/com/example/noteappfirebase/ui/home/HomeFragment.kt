package com.example.noteappfirebase.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.noteappfirebase.core.showDeleteNoteDialog
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.core.showToast
import com.example.noteappfirebase.databinding.FragmentHomeBinding
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
        setupUiComponents()
        setupStateObserver()
    }

    private fun setupUiComponents() {
        setupAdapter()
        binding.run {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) = true
                override fun onQueryTextChange(query: String): Boolean {
                    viewModel.handleIntent(HomeIntent.SearchByQuery(query))
                    return true
                }
            })
            // viewModel.handleIntent(HomeIntent.LoadUserImage) does not work
            Glide.with(ivProfile).load(viewModel.getUserImage()).into(ivProfile)
            tvLogout.setOnClickListener {
                viewModel.handleIntent(HomeIntent.Logout)
                findNavController().navigate(HomeFragmentDirections.actionHomeToLogin())
            }
            fabAdd.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeToAdd())
            }
        }
    }

    private fun setupAdapter() {
        adapter = NoteAdapter(emptyList())
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        adapter.listener = object : NoteAdapter.Listener {
            override fun onClickNote(id: String) {
                findNavController().navigate(HomeFragmentDirections.actionHomeToDetails(id))
            }

            override fun onLongClickNote(id: String) {
                BottomSheetFragment(
                    onClickEdit = {
                        findNavController().navigate(HomeFragmentDirections.actionToEdit(id))
                    },
                    onClickDelete = {
                        showDeleteNoteDialog(requireContext()) {
                            viewModel.handleIntent(HomeIntent.DeleteNote(id))
                        }
                    }
                ).show(parentFragmentManager, null)
            }
        }
    }

    private fun setupStateObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.tvLoading.isVisible = state.isLoading
                if (state.notes != null) {
                    adapter.setNotes(state.notes)
                    binding.tvEmpty.isVisible = state.notes.isEmpty()
                }
                state.successMessage?.let {
                    showToast(requireContext(), it)
                    viewModel.handleIntent(HomeIntent.ClearMessages)
                }
                state.errorMessage?.let {
                    showErrorSnackbar(requireView(), it, requireContext())
                    viewModel.handleIntent(HomeIntent.ClearMessages)
                }
            }
        }
    }
}