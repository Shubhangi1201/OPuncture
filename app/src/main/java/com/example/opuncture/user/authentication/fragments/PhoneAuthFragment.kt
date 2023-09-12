package com.example.opuncture.user.authentication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.opuncture.R
import com.google.firebase.auth.FirebaseAuth


class PhoneAuthFragment : Fragment() {
    private lateinit var navController : NavController

    private lateinit var auth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone_auth, container, false)

        auth = FirebaseAuth.getInstance()
        val btn  = view.findViewById<Button>(R.id.sendOTPbtn)
        btn.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_loginOtpFragment, Bundle().apply {
                putString("LoginPhoneNumber", "+919702212438")
            })
        }




        return view
    }


}