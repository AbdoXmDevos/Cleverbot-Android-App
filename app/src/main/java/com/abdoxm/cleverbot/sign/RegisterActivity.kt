package com.abdoxm.cleverbot.sign

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.abdoxm.cleverbot.R
import com.abdoxm.cleverbot.main.MainActivity
import com.abdoxm.cleverbot.utilities.User
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.password
import kotlinx.android.synthetic.main.activity_register.username
import kotlinx.android.synthetic.main.activity_signin.*

class RegisterActivity : AppCompatActivity() {


    lateinit var Username: EditText
    lateinit var Email: EditText
    private lateinit var Password: EditText
    private lateinit var ConfirmPassword: EditText
    private lateinit var SignUp: Button
    lateinit var RedirectLogin: TextView

    lateinit var mAdView : AdView

    val AD_UNIT_ID = "ca-app-pub-4140935255351753/2159510857"


    private var interstitialAd: InterstitialAd? = null
    //facebook


    // create Firebase authentication object
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // View Bindings

        loadban()
        loadAd()

        database = Firebase.database.reference




        Username = findViewById(R.id.name)
        Email = findViewById(R.id.username)
        Password = findViewById(R.id.password)
        ConfirmPassword = findViewById(R.id.confirmPassword)
        SignUp = findViewById(R.id.btnRegister)
        RedirectLogin = findViewById(R.id.goto_signin)



        // Initialising auth object
        auth = Firebase.auth


        SignUp.setOnClickListener {
            signUpUser()
        }

        // switching from signUp Activity to Login Activity
        RedirectLogin.setOnClickListener {

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

            showInterstitial()
            finish()
        }



    }

    private fun signUpUser() {
        val user = Username.text.toString()
        val email = Email.text.toString()
        val pass = Password.text.toString()
        val CPass = ConfirmPassword.text.toString()

        // check pass
        if (email.isBlank() || pass.isBlank() || CPass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != CPass) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val name = name.text.toString()
        val username = username.text.toString()
        val password = password.text.toString()

        if (remember_meRegister.isChecked) {
            rememberMe(true)
        }


        database = FirebaseDatabase.getInstance().getReference("Users")

        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()

                auth.signInWithEmailAndPassword(email , pass)

                val id = auth.currentUser?.uid.toString()
                val User = User(id,name,username,password)

                database.child(id).setValue(User)



                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                finish()
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rememberMe(choice:Boolean) {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("Remember me", choice)
        editor.apply()
    }

    private fun loadban(){
        val adRequest = AdRequest.Builder().build()
        mAdView = findViewById(R.id.adView)

        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }
    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    val error =
                        "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"

                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                }
            }
        )
    }

    private fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                    }
                }
            interstitialAd?.show(this)
        }
    }

}
