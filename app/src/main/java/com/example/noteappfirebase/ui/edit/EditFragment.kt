package com.example.noteappfirebase.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.noteappfirebase.R
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.core.showToast
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.FragmentEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private val viewModel: EditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUiComponents()
        setupStateObserver()
    }

    private fun setupUiComponents() {
        binding.run {
            val colorButtons = listOf(
                btnRed to R.color.option_red,
                btnOrange to R.color.option_orange,
                btnYellow to R.color.option_yellow,
                btnGreen to R.color.option_green,
                btnBlue to R.color.option_blue,
            )
            colorButtons.forEach { (button, color) ->
                button.setOnClickListener {
                    viewModel.handleIntent(
                        EditIntent.ChangeColor(
                            ContextCompat.getColor(requireContext(), color)
                        )
                    )
                }
            }
            btnSubmit.setOnClickListener {
                val note = Note(
                    title = etTitle.text.toString(),
                    desc = etDesc.text.toString(),
                    color = viewModel.state.value.color
                )
                viewModel.handleIntent(EditIntent.SaveNote(note))
            }
            btnCancel.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun setupStateObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.run {
                    tvLoading.isVisible = state.isLoading
                    if (state.note != null && state.successMessage == null) {
                        llEdit.isVisible = true
                        etTitle.setText(state.note.title)
                        etDesc.setText(state.note.desc)
                        llEdit.setBackgroundColor(state.color)
                    }
                }
                state.successMessage?.let {
                    showToast(requireContext(), it)
                    viewModel.handleIntent(EditIntent.ClearMessages)
                    findNavController().popBackStack()
                }
                state.errorMessage?.let {
                    showErrorSnackbar(requireView(), it, requireContext())
                    viewModel.handleIntent(EditIntent.ClearMessages)
                }
            }
        }
    }
}