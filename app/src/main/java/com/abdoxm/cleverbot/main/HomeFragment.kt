package com.abdoxm.cleverbot.main


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abdoxm.cleverbot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

private lateinit var database1: DatabaseReference
private lateinit var database2: DatabaseReference



class HomeFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database1 = FirebaseDatabase.getInstance().getReference("Users")
        database2 = FirebaseDatabase.getInstance().getReference("News")





            email_main.setText("")
            Guest.setText("Welcome to Chatgpt App !")
            username_main.setText("")



        database2.get().addOnSuccessListener {

            if (it.child("New1").child("Number").value.toString() == "0"){
                NewsTitle1.visibility = View.GONE
                newsDescription1.visibility = View.GONE
                newsDate1.visibility = View.GONE
                news1.visibility = View.GONE
            }
            if (it.child("New2").child("Number").value.toString() == "0"){
                NewsTitle2.visibility = View.GONE
                newsDescription2.visibility = View.GONE
                newsDate2.visibility = View.GONE
                news2.visibility = View.GONE
            }
            if (it.child("New3").child("Number").value.toString() == "0"){
                NewsTitle3.visibility = View.GONE
                newsDescription3.visibility = View.GONE
                newsDate3.visibility = View.GONE
                news3.visibility = View.GONE
            }

            if (it.child("New1").child("New").value.toString() == "false"){
                new1.setText("")
            }else{
                new1.setText("New !")
            }
            if (it.child("New2").child("New").value.toString() == "false"){
                new2.setText("")
            }else{
                new2.setText("New !")
            }
            if (it.child("New3").child("New").value.toString() == "false"){
                new3.setText("")
            }else{
                new3.setText("New !")
            }

            for (i in 1 .. 3){

                if (it.child("New$i").child("Number").value.toString() == "1"){
                    NewsTitle1.text = it.child("New$i").child("Title").value.toString()
                    newsDescription1.text = it.child("New$i").child("Description").value.toString()
                    newsDate1.text = it.child("New$i").child("Date").value.toString()
                }
                if (it.child("New$i").child("Number").value.toString() == "2"){
                    NewsTitle2.text = it.child("New$i").child("Title").value.toString()
                    newsDescription2.text = it.child("New$i").child("Description").value.toString()
                    newsDate2.text = it.child("New$i").child("Date").value.toString()
                }
                if (it.child("New$i").child("Number").value.toString() == "3"){
                    NewsTitle3.text = it.child("New$i").child("Title").value.toString()
                    newsDescription3.text = it.child("New$i").child("Description").value.toString()
                    newsDate3.text = it.child("New$i").child("Date").value.toString()
                }

            }
        }


    }

}