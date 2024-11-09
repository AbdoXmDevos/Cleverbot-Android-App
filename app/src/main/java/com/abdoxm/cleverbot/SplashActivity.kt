package com.abdoxm.cleverbot

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.abdoxm.cleverbot.Intro.IntroActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_splash.*
import kotlin.random.Random


private const val ONESIGNAL_APP_ID = "23a48262-6d86-44db-b6b1-b036ff7c8a2c"

open class SplashActivity : AppCompatActivity() {

    private lateinit var randomStrings: Array<String>

    private val SPLASH_TIME_OUT: Long = 5000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isLimitReached", false)
        editor.commit()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)






        randomStrings = resources.getStringArray(R.array.Splash_Tips)
        val randomIndex = Random.nextInt(randomStrings.size)

        tipTxt.text = randomStrings[randomIndex]


        Handler().postDelayed({
            if (checkForInternet(this@SplashActivity)) {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, NoInternet::class.java))
                finish()
            }
        }, SPLASH_TIME_OUT)


    }


    override fun onDestroy() {
        super.onDestroy()
        val randomIndex = Random.nextInt(randomStrings.size)
        tipTxt.text = randomStrings[randomIndex]
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}

