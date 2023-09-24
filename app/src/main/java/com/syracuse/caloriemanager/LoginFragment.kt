package com.syracuse.caloriemanager

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.syracuse.caloriemanager.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.v(TAG, "Login Fragment Loaded")
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener{

            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            try {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.v(TAG, "Successfully Logged In")
                            val trackingActivity = Intent(activity, TrackingActivity::class.java)
                            startActivity(trackingActivity)
                            activity?.finish()
                        } else {
                            Toast.makeText(requireActivity(),
                                task.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: IllegalArgumentException){
                Log.e(TAG, "Email or Password cannot be empty")
                Toast.makeText(requireActivity(),
                    "Email or Password cannot be empty", Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Toast.makeText(requireActivity(), e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        attachTextInputListeners()
    }

    private fun attachTextInputListeners() {
        binding.editEmail.doOnTextChanged { text, _, _, _ ->
            if(text?.isEmpty() == true){
                binding.textLayoutEmail.error = "Enter an email"
            } else if (!text?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }!!) {
                binding.textLayoutEmail.error = "Enter a valid email"
            } else {
                binding.textLayoutEmail.error = null
            }
        }

        binding.editPassword.doOnTextChanged { text, _, _, _ ->
            if(text?.isEmpty() == true){
                binding.textLayoutPassword.error = "Enter an password"
            } else if (text?.length!! < 8) {
                binding.textLayoutPassword.error = "Password should be at least 8 characters"
            } else {
                binding.textLayoutPassword.error = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}