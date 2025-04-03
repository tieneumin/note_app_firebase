package com.example.noteappfirebase.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.noteappfirebase.R
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.core.showToast
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.FragmentAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private val viewModel: AddViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)
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
                        AddIntent.ChangeColor(
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
                viewModel.handleIntent(AddIntent.SaveNote(note))
            }
            btnCancel.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun setupStateObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.run {
                    clAdd.setBackgroundColor(state.color)
                    state.successMessage?.let {
                        showToast(requireContext(), it)
                        viewModel.handleIntent(AddIntent.ClearMessages)
                        findNavController().popBackStack()
                    }
                    state.errorMessage?.let {
                        showErrorSnackbar(requireView(), it, requireContext())
                        viewModel.handleIntent(AddIntent.ClearMessages)
                    }
                }
            }
        }
    }


}