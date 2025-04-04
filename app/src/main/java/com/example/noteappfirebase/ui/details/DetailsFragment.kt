package com.example.noteappfirebase.ui.details

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteappfirebase.R
import com.example.noteappfirebase.R.layout.dialog_delete
import com.example.noteappfirebase.core.log
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.FragmentDetailsBinding
import com.example.noteappfirebase.ui.home.HomeIntent
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
        binding = FragmentDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handleIntent(DetailsIntent.FetchNote(args.id))
        setupUi()
        setupStateObserver()
    }

    private fun setupUi() {
        binding.run {
            btnEdit.setOnClickListener {
                val action = DetailsFragmentDirections.actionDetailsFragmentToEditFragment(args.id)
                findNavController().navigate(action)
            }
            btnDelete.setOnClickListener {
                val dialogView = LayoutInflater.from(requireContext()).inflate(dialog_delete, null)

                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create()

                dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                    dialog.dismiss()
                }

                dialogView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                    viewModel.handleIntent(DetailsIntent.DeleteNote(args.id))
                    viewModel.handleIntent(DetailsIntent.ClearMessage)
                    findNavController().popBackStack()
                    dialog.dismiss()
                }

                dialog.show()
            }
            btnBack.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun setupStateObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.run {
                    tvLoading.isVisible = state.isLoading
                    if (state.note != null && state.errorMessage.isNullOrEmpty() && !state.isLoading) {
                        llNote.isVisible = true
                        tvTitle.text = state.note.title
                        tvDesc.text = state.note.desc
                        llNote.setBackgroundColor(state.note.color)
                    }
                    state.errorMessage?.let {
                        showErrorSnackbar(requireView(), it, requireContext())
                    }
                }
            }
        }
    }
}

