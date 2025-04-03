package com.example.noteappfirebase.ui.details

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.noteappfirebase.R
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.data.model.Note
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
        binding = FragmentDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupStateObserver()
    }

    private fun setupUi() {
        viewModel.handleIntent(DetailsIntent.FetchNote(args.id))
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

