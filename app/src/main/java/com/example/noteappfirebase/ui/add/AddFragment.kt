package com.example.noteappfirebase.ui.add

import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.noteappfirebase.R
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.toColorInt
import androidx.navigation.fragment.findNavController

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private val viewModel: AddViewModel by viewModels()
    private var title:String = ""
    private var desc:String = ""
    private var color:Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorButtons()
        setupSubmitButton()
    }

    private fun setupColorButtons() {
        binding.btnRed.setOnClickListener {
            color = Color.parseColor("#FFB2B2")
            binding.llAdd.setBackgroundColor(color)
        }
        binding.btnBlue.setOnClickListener {
            color = Color.parseColor("#ABE6FF")
            binding.llAdd.setBackgroundColor(color)
        }
        binding.btnGreen.setOnClickListener {
            color = Color.parseColor("#CEFFB2")
            binding.llAdd.setBackgroundColor(color)
        }
        binding.btnYellow.setOnClickListener {
            color = Color.parseColor("#FDFFB0")
            binding.llAdd.setBackgroundColor(color)
        }

    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            title = binding.etTitle.text.toString().trim()
            desc = binding.etDesc.text.toString().trim()
            if (title.isEmpty()) {
                showSnackBar("Title is empty")
                return@setOnClickListener
            }

            if (desc.isEmpty()) {
                showSnackBar("Description is empty")
                return@setOnClickListener
            }

            if(color == -1) {
                showSnackBar("Color is not selected")
                return@setOnClickListener
            }

            val note = Note(title = title, desc = desc, color = color)
            viewModel.addNewNote(note)

            val action = AddFragmentDirections.actionAddFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
}