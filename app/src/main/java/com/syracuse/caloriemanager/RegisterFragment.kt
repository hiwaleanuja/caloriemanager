package com.syracuse.caloriemanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.syracuse.caloriemanager.databinding.FragmentRegisterBinding
import com.syracuse.caloriemanager.models.Dairy
import com.syracuse.caloriemanager.models.User

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "Register Fragment Loaded")
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener{

            val firstname = binding.editFirstname.text.toString()
            val lastname = binding.editLastname.text.toString()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.v(TAG, "Successfully Registered")
                            Log.wtf(TAG, "Create a user record with isOnBoarder = false")
                            val uid = firebaseAuth.currentUser?.uid
                            val user = User(firstname, lastname)
                            firebaseDatabase.reference.child("users").child(uid.toString()).setValue(user)
                            firebaseDatabase.reference.child("meals").child(uid.toString()).setValue("initial")
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

        binding.btnToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        attachTextInputListeners()
    }

    private fun attachTextInputListeners() {

        binding.editFirstname.doOnTextChanged { text, _, _, _ ->
            if(text?.isEmpty() == true){
                binding.textLayoutFirstname.error = "Enter your First Name"
            } else {
                binding.textLayoutFirstname.error = null
            }
        }

        binding.editLastname.doOnTextChanged { text, _, _, _ ->
            if(text?.isEmpty() == true){
                binding.textLayoutLastname.error = "Enter your Last Name"
            } else {
                binding.textLayoutLastname.error = null
            }
        }

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

        binding.editConfirmPassword.doOnTextChanged { text, _, _, _ ->
            val password = binding.editPassword.text.toString()
            if(text?.isEmpty() == true){
                binding.textLayoutConfirmPassword.error = "Enter your password again"
            } else if(password != text.toString()){
                binding.textLayoutConfirmPassword.error = "Passwords do not match"
            } else {
                binding.textLayoutConfirmPassword.error = null
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}