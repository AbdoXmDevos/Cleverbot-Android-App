package com.abdoxm.cleverbot.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.abdoxm.cleverbot.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_about_us.*


private lateinit var database: DatabaseReference


class AboutUsFragment : Fragment(R.layout.fragment_about_us) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("Socials")

        ic_tiktok.setOnClickListener() {
            database.child("tiktok").get().addOnSuccessListener {
                if (it.value.toString() != "") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.value.toString())))
                } else {
                    Toast.makeText(requireActivity(), "Tiktok Coming Soon !", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        ic_web.setOnClickListener() {
            database.child("web").get().addOnSuccessListener {
                if (it.value.toString() != "") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.value.toString())))
                } else {
                    Toast.makeText(requireActivity(), "Website Coming Soon !", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        ic_youtube.setOnClickListener() {

            database.child("youtube").get().addOnSuccessListener {

                if (it.value.toString() != "") {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.value.toString())))
                } else {
                    Toast.makeText(requireActivity(), "Youtube Coming Soon !", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }


}