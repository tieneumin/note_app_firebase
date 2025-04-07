package com.example.noteappfirebase.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.noteappfirebase.core.showErrorSnackbar
import com.example.noteappfirebase.core.showToast
import com.example.noteappfirebase.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUiComponents()
        setupStateObserver()
    }

    private fun setupUiComponents() {
        binding.btnGoogle.setOnClickListener {
            viewModel.handleIntent(LoginIntent.LoginWithGoogle(requireContext()))
        }
    }

    private fun setupStateObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                state.successMessage?.let {
                    showToast(requireContext(), it)
                    viewModel.handleIntent(LoginIntent.ClearMessages)
                    findNavController().navigate(LoginFragmentDirections.actionToHome())
                }
                state.errorMessage?.let {
                    showErrorSnackbar(requireView(), it, requireContext())
                    viewModel.handleIntent(LoginIntent.ClearMessages)
                }
            }
        }
    }
}