package com.example.opuncture.user.authentication.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.opuncture.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class OtpVerificationFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var progressBar: ProgressBar
    private lateinit var btn: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_otp_verification, container, false)

        auth = FirebaseAuth.getInstance()
        
        val phoneNumber = "+91" + requireArguments().getString("LoginPhoneNumber")
        
        val et = view.findViewById<EditText>(R.id.OTPet)
        val errorTV = view.findViewById<TextView>(R.id.errorIncorrectOTPTV)
        val resendTV = view.findViewById<TextView>(R.id.ResendOTPtv)
        btn = view.findViewById<Button>(R.id.verifyOtpBtn)
        val VerifyOTPtv = view.findViewById<TextView>(R.id.verifyHeadingTV)
        progressBar = view.findViewById(R.id.verify_progressBar)

        VerifyOTPtv.text = "Verify the OTP sent to $phoneNumber"
        
        resendTV.setOnClickListener{
            Toast.makeText(context, "OTP will resend", Toast.LENGTH_SHORT).show()
        }



        if(phoneNumber!= null){
            sendVerificationCode(phoneNumber)
            Toast.makeText(context, "phone number is " + phoneNumber, Toast.LENGTH_SHORT).show()
        }else{

        }



        btn.setOnClickListener{
            verifyOTPCode(et.text.toString())
            showProgressBar()
        }


        return view
    }



    fun sendVerificationCode(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }



    private val callbacks  = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")
            val code: String? = credential.smsCode
            if(!code.isNullOrBlank()){
                verifyOTPCode(code)
                Toast.makeText(context, "verify otp code function called", Toast.LENGTH_SHORT).show()

            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(ContentValues.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {

            } else if (e is FirebaseTooManyRequestsException) {

            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {

            }

            Toast.makeText(context, "verification failed " + e.toString(), Toast.LENGTH_SHORT).show()
            hideProgressBar()

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d(ContentValues.TAG, "onCodeSent:$verificationId")
            Toast.makeText(context, "code sent successfully ", Toast.LENGTH_SHORT).show()

            storedVerificationId = verificationId

        }
    }

    fun verifyOTPCode(code: String){
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signinbycredentials(credential)
    }

    fun signinbycredentials(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    Toast.makeText(context, "authentication successful", Toast.LENGTH_SHORT).show()

                    hideProgressBar()

                }else{
                    Toast.makeText(context, "failed failed failed", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
            }
    }



    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE



    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE


    }


}