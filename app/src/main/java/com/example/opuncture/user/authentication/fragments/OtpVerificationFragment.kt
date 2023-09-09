package com.example.opuncture.user.authentication.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_otp_verification, container, false)
        val phoneNumber = "+919702212438"
        auth = FirebaseAuth.getInstance()

        val et = view.findViewById<EditText>(R.id.OTPet)
        val btn = view.findViewById<Button>(R.id.verifyBtn)

        if(phoneNumber!= null){
            sendVerificationCode(phoneNumber)
        }else{

        }


        btn.setOnClickListener{
            verifyOTPCode(et.text.toString())
        }
        auth = FirebaseAuth.getInstance()

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
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")
            val code: String? = credential.smsCode
            if(!code.isNullOrBlank()){
                verifyOTPCode(code)
                Toast.makeText(context, "verify otp code function called", Toast.LENGTH_SHORT).show()

            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(ContentValues.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            Toast.makeText(context, "verification failed " + e.toString(), Toast.LENGTH_SHORT).show()


            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(ContentValues.TAG, "onCodeSent:$verificationId")
            Toast.makeText(context, "code sent successfully ", Toast.LENGTH_SHORT).show()


            // Save verification ID and resending token so we can use them later
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

                }else{
                    Toast.makeText(context, "failed failed failed", Toast.LENGTH_SHORT).show()

                }

            }
    }

}