package com.abdoxm.cleverbot.chat

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdoxm.cleverbot.R
import com.abdoxm.cleverbot.chat.Constants.BOT_ID
import com.abdoxm.cleverbot.chat.Constants.USER_ID
import com.abdoxm.cleverbot.chat.Constants.info
import com.abdoxm.cleverbot.utilities.Time
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.util.Assert
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.editText
import kotlinx.android.synthetic.main.activity_chat.fab_img
import kotlinx.android.synthetic.main.activity_chat_bckp.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.msglist.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.random.Random


private lateinit var database: DatabaseReference

private lateinit var auth: FirebaseAuth

private lateinit var randomWelcomers: Array<String>

open class ChatActivity : AppCompatActivity(), RecycleViewInterface {

    var messagesList = mutableListOf<Message>()


    private lateinit var adapter: MyAdapter
    var url = "https://api.openai.com/v1/chat/completions"
    var question: String = ""

    val timeStamp = Time.timeStamp()
    var apiKey: String? = null
    private final var TAG = "MainActivity"

    val AD_UNIT_ID = "ca-app-pub-4140935255351753/2159510857"


    private var interstitialAd: InterstitialAd? = null


    private val REQUEST_CODE_SPEECH_INPUT = 1


    @SuppressLint("RestrictedApi", "LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        loadAd()
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        var chatCount = 5
        var chatAd = 1
        //var msgCount = chatCountRestore()
        var msgCount = 5
        adapter = MyAdapter(this)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(applicationContext)
        auth = Firebase.auth

        if (user?.email.toString() == "guest@guest.com") {
            msgNumber.text = msgCount.toString()
        } else {
            msgNumber.text = "Unlimited"
        }




        database = FirebaseDatabase.getInstance().getReference("API")

        database.child("key").get().addOnSuccessListener {

            apiKey = it.value.toString()

            Log.i("firebase", "Value is ${it.value}")

        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }


        val id: String = auth.currentUser?.uid.toString()
        Handler().postDelayed({
            addMsg("Typing...", "bot")
            Handler().postDelayed({

                adapter.removeMessage(Message("Typing...", Constants.BOT_ID, timeStamp))
                messagesList.remove(Message("Typing...", Constants.BOT_ID, timeStamp))
                recyclerview.scrollToPosition(adapter.itemCount - 1)

                welcomeMsg()
            }, 1500)
        }, 500)




        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (editText.text.toString() == "") {
                    fab_img.setImageResource(R.drawable.ic_mic_white_24dp)
                    fab_img.tag = R.drawable.ic_mic_white_24dp
                } else {
                    fab_img.setImageResource(R.drawable.ic_send_white_24dp)
                    fab_img.tag = R.drawable.ic_send_white_24dp
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editText.text.toString() == "") {
                    fab_img.setImageResource(R.drawable.ic_mic_white_24dp)
                    fab_img.tag = R.drawable.ic_mic_white_24dp
                } else {
                    fab_img.setImageResource(R.drawable.ic_send_white_24dp)
                    fab_img.tag = R.drawable.ic_send_white_24dp
                }
            }


        })

        fab_img.tag = R.drawable.ic_mic_white_24dp

        backicon.setOnClickListener() {

            finish()

        }

        fab_img.setOnClickListener() {

            if (fab_img.tag == R.drawable.ic_mic_white_24dp) {

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
                } catch (e: Exception) {
                    // on below line we are displaying error message in toast
                    Toast
                        .makeText(
                            this@ChatActivity, " " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }

            } else if (fab_img.tag == R.drawable.ic_send_white_24dp) {

                if (editText.text.toString().isEmpty()) {

                    Toast.makeText(this, "Please enter your question..", Toast.LENGTH_SHORT).show()

                } else {

                    if (user?.email.toString() == "guest@guest.com") {
                        if (msgNumber.text.toString() != "0") {

                            messagesList.add(
                                Message(
                                    editText.text.toString(),
                                    Constants.USER_ID,
                                    timeStamp
                                )
                            )

                            adapter.insertMessage(
                                Message(
                                    editText.text.toString(),
                                    Constants.USER_ID,
                                    timeStamp
                                )
                            )

                            recyclerview.scrollToPosition(adapter.itemCount - 1)

                            recyclerview.scrollToPosition(adapter.itemCount - 1)

                            Handler().postDelayed({
                                messagesList.add(
                                    Message(
                                        "Typing...",
                                        Constants.BOT_ID,
                                        timeStamp
                                    )
                                )

                                adapter.insertMessage(
                                    Message(
                                        "Typing...",
                                        Constants.BOT_ID,
                                        timeStamp
                                    )
                                )
                                recyclerview.scrollToPosition(adapter.itemCount - 1)


                            }, 500)


                            getResponseChatGpt(editText.text.toString())
                            msgCount--
                            msgNumber.text = msgCount.toString()


                            if (chatAd % 3 == 0) {
                                showInterstitial()
                                loadAd()

                            } else {
                                chatAd++
                            }
                            chatCount--
                            chatCountSave(chatCount)
                        } else {
                            showInterstitial()
                            messagesList.add(
                                Message(
                                    "Sign in for unlimited chats",
                                    Constants.BOT_ID,
                                    timeStamp
                                )
                            )

                            adapter.insertMessage(
                                Message(
                                    "Sign in for unlimited chats",
                                    Constants.BOT_ID,
                                    timeStamp
                                )
                            )
                            recyclerview.scrollToPosition(adapter.itemCount - 1)

                        }
                    } else {

                        messagesList.add(
                            Message(
                                editText.text.toString(),
                                Constants.USER_ID,
                                timeStamp
                            )
                        )

                        adapter.insertMessage(
                            Message(
                                editText.text.toString(),
                                Constants.USER_ID,
                                timeStamp
                            )
                        )

                        recyclerview.scrollToPosition(adapter.itemCount - 1)

                        recyclerview.scrollToPosition(adapter.itemCount - 1)

                        Handler().postDelayed({
                            messagesList.add(
                                Message(
                                    "Typing...",
                                    Constants.BOT_ID,
                                    timeStamp
                                )
                            )

                            adapter.insertMessage(
                                Message(
                                    "Typing...",
                                    Constants.BOT_ID,
                                    timeStamp
                                )
                            )
                            recyclerview.scrollToPosition(adapter.itemCount - 1)


                        }, 500)


                        getResponseChatGpt(editText.text.toString())



                        if (chatAd % 5 == 0) {
                            showInterstitial()
                            loadAd()

                        } else {
                            chatAd++
                        }
                    }

                }

            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                editText.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }

    private fun getResponseChatGpt(query: String) {

        editText.setText("")

        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // creating a json object on below line.
        val jsonObject: JSONObject? = JSONObject()
        val ChatjsObject: JSONObject? = JSONObject()
        val ChatjsArray: JSONArray? = JSONArray()
        // adding params to json object.


        //val infos = "know that (this app was created by abdoxm, and the app name is ChatGpt, And you're name will be Alex, and you're a chat bot that helps people with their questions and prompts)"
        val prompt = question + query


        ChatjsObject?.put("role", "user")
        ChatjsObject?.put("content", prompt)

        ChatjsArray?.put(ChatjsObject)

        jsonObject?.apply {
            //put("messages", "$info \n  \n Human: $prompt \n AI:")
            put("model", "gpt-3.5-turbo")
            put("messages", ChatjsArray)
            put("max_tokens", 150)
            put("temperature",0.7)
        }.also {
            Log.i("test", it.toString())
        }

        // on below line making json object request.
        val postRequest: JsonObjectRequest =
            // on below line making json object request.
            @SuppressLint("LogNotTimber")
            object : JsonObjectRequest(
                Method.POST, url, jsonObject,
                Response.Listener { response ->
                    Log.d("TAG-Response", "getResponse: $response")
                    // on below line getting response message and setting it to text view.

                    val gson = Gson()
                    val responseMsg: String =

                        response.getJSONArray("choices").getJSONObject(0).getString("message")

                    val receivedMessage = gson.fromJson(responseMsg, ReceivedMessage::class.java)
                    Log.i("receivedMessage", receivedMessage.content)

                    adapter.removeMessage(Message("Typing...", Constants.BOT_ID, timeStamp))
                    messagesList.remove(Message("Typing...", Constants.BOT_ID, timeStamp))
                    recyclerview.scrollToPosition(adapter.itemCount - 1)


                    messagesList.add(
                        Message(
                            receivedMessage.content.trim(),
                            Constants.BOT_ID,
                            timeStamp
                        )
                    )

                    adapter.insertMessage(
                        Message(
                            receivedMessage.content.trim(),
                            Constants.BOT_ID,
                            timeStamp
                        )
                    )

                    recyclerview.scrollToPosition(adapter.itemCount - 1)

                    question = query + receivedMessage.content.trim()


                },

                // adding on error listener
                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)

                    adapter.removeMessage(Message("Typing...", Constants.BOT_ID, timeStamp))
                    messagesList.remove(Message("Typing...", Constants.BOT_ID, timeStamp))
                    recyclerview.scrollToPosition(adapter.itemCount - 1)


                    messagesList.add(
                        Message(
                            "Sorry could you repeat that ?",
                            Constants.BOT_ID,
                            timeStamp
                        )
                    )

                    adapter.insertMessage(
                        Message(
                            "Sorry could you repeat that ?",
                            Constants.BOT_ID,
                            timeStamp
                        )
                    )

                    recyclerview.scrollToPosition(adapter.itemCount - 1)
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["Authorization"] =
                        "Bearer ${apiKey.toString()}"
                    return params;
                }
            }

        // on below line adding retry policy for our request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // on below line adding our request to queue.
        queue.add(postRequest)
    }


    override fun onItemClick(position: Int) {
    }

    override fun onItemLongClick(position: Int) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose")
        builder.setMessage("Do you want to copy the message or to delete it ?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("COPY") { dialog, which ->

            val text2copy = messagesList[position].message

            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            val clipData = ClipData.newPlainText("label", text2copy)

            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Text Copied !", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("DELETE") { dialog, which ->

            adapter.removeMessage(messagesList[position])
            messagesList.remove(messagesList[position])
            adapter.notifyItemRemoved(position)
            recyclerview.scrollToPosition(adapter.itemCount - 1)

            Toast.makeText(
                applicationContext,
                "Message Deleted", Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNeutralButton("CANCEL") { dialog, which ->
        }
        builder.show()


    }

    private fun welcomeMsg() {

        randomWelcomers = resources.getStringArray(R.array.Welcome_msg)
        val randomIndex = Random.nextInt(randomWelcomers.size)

        messagesList.add(
            Message(
                randomWelcomers[randomIndex],
                Constants.BOT_ID,
                timeStamp
            )
        )

        adapter.insertMessage(
            Message(
                randomWelcomers[randomIndex],
                Constants.BOT_ID,
                timeStamp
            )
        )
        recyclerview.scrollToPosition(adapter.itemCount - 1)
    }


    private fun addMsg(text: String, id: String) {
        var botOrUser: String? = null

        if (id == "bot") {
            botOrUser = BOT_ID
        } else if (id == "user") {
            botOrUser = USER_ID
        }

        messagesList.add(
            Message(
                text,
                botOrUser.toString(),
                timeStamp
            )
        )

        adapter.insertMessage(
            Message(
                text,
                botOrUser.toString(),
                timeStamp
            )
        )
        recyclerview.scrollToPosition(adapter.itemCount - 1)

    }


    private fun restorePrefData(): Boolean {
        val pref =
            applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isLimitReached", false)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isLimitReached", true)
        editor.commit()
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

    private fun chatCountSave(chatCount: Int) {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("chatCount", chatCount)
        editor.apply()

    }
    private fun chatCountRestore(): Int {
        val pref =
            applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getInt("chatCount", 5)
    }

}