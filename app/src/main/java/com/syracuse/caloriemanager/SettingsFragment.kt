package com.syracuse.caloriemanager

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.text.set
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.syracuse.caloriemanager.databinding.FragmentSettingsBinding
import com.syracuse.caloriemanager.models.ActivityLevel
import com.syracuse.caloriemanager.models.User
import java.text.SimpleDateFormat
import java.util.*


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var fUser: FirebaseUser
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        fUser = firebaseAuth.currentUser!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activityLevels: MutableList<String> = ArrayList()
        for (activityLevel in ActivityLevel.values()) {
            activityLevels.add(activityLevel.description)
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, ActivityLevel.values())
        binding.activityLevel.setAdapter(adapter)
        binding.activityLevel.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            Log.wtf(TAG, "Selected activity level is ${item}")
        }

        firebaseDatabase.reference.child("users").child(fUser.uid).get()
            .addOnSuccessListener { snapshot ->
                mUser = snapshot.getValue(User::class.java)!!
                if(mUser.isOnBoarded){
                    binding.height.setText(mUser.height.toInt().toString())
                    binding.height.setText(mUser.height.toString())
                    binding.goalWeight.setText(mUser.goalWeight.toString())
                    binding.currentWeight.setText(mUser.currentWeight.toString())
                    binding.age.setText(mUser.age.toString())
                }
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }


        binding.save.setOnClickListener{
            try {
                mUser.height = binding.height.text.toString().toDouble()
                mUser.currentWeight = binding.currentWeight.text.toString().toDouble()
                mUser.goalWeight = binding.goalWeight.text.toString().toDouble()
                mUser.age = binding.age.text.toString().toDouble()

                mUser.isOnBoarded = mUser.height > 0 && mUser.currentWeight > 0 && mUser.goalWeight > 0 && mUser.age > 0

            } catch (error: Exception){
                Snackbar.make(binding.root, "Please check the values", Snackbar.LENGTH_SHORT).show()
            }

            Log.wtf(TAG, "Setting data for user ${fUser.uid}, mUser = ${mUser.toString()}")
            firebaseDatabase.reference.child("users").child(fUser.uid).setValue(mUser)
            Snackbar.make(binding.root, "We have updated your settings", Snackbar.LENGTH_SHORT).show()

        }
    }

    companion object {
        @JvmStatic val TAG = "SettingsFragment"
    }
}