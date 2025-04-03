package com.example.noteappfirebase.ui.edit

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteappfirebase.R
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.core.showToast
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.FragmentEditBinding
import com.example.noteappfirebase.ui.add.AddIntent
import com.example.noteappfirebase.ui.edit.EditIntent
import com.example.noteappfirebase.ui.edit.EditIntent.ClearMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private val viewModel: EditViewModel by viewModels()
    private val args: EditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handleIntent(EditIntent.FetchNote(args.id))
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
                    if (!state.isLoading && state.note != null && state.errorMessage.isNullOrEmpty()) {
                        llEdit.isVisible = true
                        etTitle.setText(state.note.title)
                        etDesc.setText(state.note.desc)
                        clEdit.setBackgroundColor(state.color)
                    }
                    state.successMessage?.let {
                        showToast(requireContext(), it)
                        viewModel.handleIntent(ClearMessage)
                        findNavController().popBackStack()
                    }
                    state.errorMessage?.let {
                        showErrorSnackbar(requireView(), it, requireContext())
                        viewModel.handleIntent(ClearMessage)
                    }
                }
            }
        }
    }
}