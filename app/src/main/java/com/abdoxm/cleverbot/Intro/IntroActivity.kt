package com.abdoxm.cleverbot.Intro

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.abdoxm.cleverbot.main.MainActivity
import com.abdoxm.cleverbot.WelcomeActivity
import com.abdoxm.cleverbot.R
import com.abdoxm.cleverbot.SplashActivity
import com.abdoxm.cleverbot.databinding.ActivityIntroBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class IntroActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var btnCreateAccount: Button
    lateinit var auth: FirebaseAuth
    lateinit var mAdView : AdView

    val AD_UNIT_ID = "ca-app-pub-4140935255351753/2159510857"


    private var interstitialAd: InterstitialAd? = null

    private lateinit var binding: ActivityIntroBinding

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()



        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loadAd()

        if (restorePrefData()) {

            if (remeberMe() == false) {
                auth.signOut()
                showInterstitial()
                startActivity(Intent(applicationContext, MainActivity::class.java))

                Log.i("Sign", "The Remember me is not checked and user is not logged !")
            } else {
                Log.i(
                    "Sign", "Remember me is checked and user logged in as ${auth.currentUser?.uid}"
                )
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }

            finish()
        }


        mViewPager = binding.viewPager
        mViewPager.adapter = IntroViewPager(this, this)
        mViewPager.offscreenPageLimit = 1
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    btnCreateAccount.text = getText(R.string.finish)
                    btnCreateAccount.background = getDrawable(R.drawable.btn_bg_green)
                } else {
                    btnCreateAccount.text = getText(R.string.next)
                    btnCreateAccount.background = getDrawable(R.drawable.btn_bg)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })

        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()

        btnCreateAccount = binding.btnCreateAccount
        btnCreateAccount.setOnClickListener {
            if (getItem() > mViewPager.childCount) {

                showInterstitial()
                savePrefsData()
                startActivity(Intent(this, MainActivity::class.java))



            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }

    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

    private fun remeberMe(): Boolean {
        val pref =
            applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("Remember me", false)
    }

    private fun restorePrefData(): Boolean {
        val pref =
            applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpened", false)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }



    override fun onRestart() {
    super.onRestart()
        startActivity(Intent(this@IntroActivity, SplashActivity::class.java))
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