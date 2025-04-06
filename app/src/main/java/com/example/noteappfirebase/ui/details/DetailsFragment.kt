package com.example.noteappfirebase.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteappfirebase.core.showDeleteNoteDialog
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.core.showToast
import com.example.noteappfirebase.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handleIntent(DetailsIntent.RefreshNote(args.id))
        setupUiComponents()
        setupStateObserver()
    }

    private fun setupUiComponents() {
        binding.run {
            btnEdit.setOnClickListener {
                val action = DetailsFragmentDirections.actionToEdit(args.id)
                findNavController().navigate(action)
            }
            btnDelete.setOnClickListener {
                showDeleteNoteDialog(requireContext()) {
                    viewModel.handleIntent(
                        DetailsIntent.DeleteNote(args.id)
                    )
                }
            }
            btnBack.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun setupStateObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.run {
                    tvLoading.isVisible = state.isLoading
                    if (!state.isLoading && state.note != null) {
                        llDetails.isVisible = true
                        tvTitle.text = state.note.title
                        tvDesc.text = state.note.desc
                        llDetails.setBackgroundColor(state.note.color)
                    }
                    state.successMessage?.let {
                        showToast(requireContext(), it)
                        viewModel.handleIntent(DetailsIntent.ClearMessages)
                        findNavController().popBackStack()
                    }
                    state.errorMessage?.let {
                        showErrorSnackbar(requireView(), it, requireContext())
                        viewModel.handleIntent(DetailsIntent.ClearMessages)
                    }
                }
            }
        }
    }
}