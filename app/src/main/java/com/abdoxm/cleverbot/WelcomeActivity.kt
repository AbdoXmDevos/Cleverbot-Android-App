package com.abdoxm.cleverbot


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abdoxm.cleverbot.main.MainActivity
import com.abdoxm.cleverbot.sign.SignInActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.core.view.View
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.activity_signin.*


class WelcomeActivity : AppCompatActivity() {


    private lateinit var database: DatabaseReference

    private final var TAG = "MainActivity"
    lateinit var mAdView: AdView

    val AD_UNIT_ID = "ca-app-pub-4140935255351753/2159510857"


    private var interstitialAd: InterstitialAd? = null

    private var adLoaded = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        database = Firebase.database.reference
        val view: View
        mAdView = findViewById(R.id.adView)
        loadAd()
        loadban()
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
        }, 1500)

        //Guest onclicker
        btnGuest.setOnClickListener() {



            auth.signInWithEmailAndPassword("guest@guest.com", "guest123")
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {

                        auth.signInWithEmailAndPassword("guest@guest.com", "guest123")

                        showInterstitial()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else
                        Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
                }

        }


        //Sign onclicker

        btnSign_welcome.setOnClickListener {

            showInterstitial()

            startActivity(Intent(this@WelcomeActivity, SignInActivity::class.java))




        }

    }

    private fun loadban() {
        var adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
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

    private fun rememberMe() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("Remember me", true)
        editor.apply()
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
