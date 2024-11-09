package com.abdoxm.cleverbot.Intro

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abdoxm.cleverbot.R

class IntroViewPager(
    fragmentActivity: FragmentActivity,
    private val context: Context
    ) :
    FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FragmentIntro.newInstance(
                    context.resources.getString(R.string.title_onboarding_1),
                    context.resources.getString(R.string.description_onboarding_1),
                    R.drawable.robotics
                )
                1 -> FragmentIntro.newInstance(
                    context.resources.getString(R.string.title_onboarding_2),
                    context.resources.getString(R.string.description_onboarding_2),
                    R.drawable.question
                )
                else -> FragmentIntro.newInstance(
                    context.resources.getString(R.string.title_onboarding_3),
                    context.resources.getString(R.string.description_onboarding_3),
                    R.drawable.developer_resized
                )
            }
        }

        override fun getItemCount(): Int {
            return 3
        }
    }