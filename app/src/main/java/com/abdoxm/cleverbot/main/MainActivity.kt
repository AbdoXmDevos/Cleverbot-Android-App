package com.abdoxm.cleverbot.main

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AlertDialogLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.abdoxm.cleverbot.R
import com.abdoxm.cleverbot.WelcomeActivity
import com.abdoxm.cleverbot.chat.ChatActivity
import com.abdoxm.cleverbot.databinding.ActivityMainBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    private final var TAG = "MainActivity"

    val AD_UNIT_ID = "ca-app-pub-4140935255351753/2159510857"
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        auth = FirebaseAuth.getInstance()

        var loaded = false

        loadAd()

        Handler().postDelayed({
            loaded = true
        }, 1500)




        binding.NavigationBottom.setOnItemSelectedListener { menuItem: MenuItem ->

            when (menuItem.itemId) {
                R.id.Settings -> {
                    showInterstitial()
                    loadAd()
                    replaceFragment(AboutUsFragment())
                    true
                }
                R.id.Home -> {
                    showInterstitial()
                    loadAd()
                    replaceFragment(HomeFragment())
                    true
                }

                else -> {
                    false
                }
            }


        }


        logOutMain.setOnClickListener() {

            showInterstitial()
            loadAd()
            val dialog = Dialog(this)

            val builder = AlertDialog.Builder(this, R.style.AlertDialog)
            builder.setTitle("Confirm log out !")
            builder.setMessage("Are you sure you want to Log out ?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton("Log out") { dialog, which ->

                auth.signOut()
                rememberMe(false)
                Toast.makeText(this@MainActivity, "You just Logged Out !", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(applicationContext, WelcomeActivity::class.java))
                finish()

            }
            builder.setNeutralButton("CANCEL") { dialog, which ->
            }
            builder.show()

        }


        goto_chat.setOnClickListener() {

            showInterstitial()
            loadAd()
            startActivity(Intent(applicationContext, ChatActivity::class.java))

        }

    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentmanager: FragmentManager = supportFragmentManager

        val fragmenttransaction: FragmentTransaction = fragmentmanager.beginTransaction()

        fragmenttransaction.replace(R.id.frame_layout_main, fragment)
        fragmenttransaction.commit()

    }

    private fun rememberMe(choice: Boolean) {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("Remember me", choice)
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


