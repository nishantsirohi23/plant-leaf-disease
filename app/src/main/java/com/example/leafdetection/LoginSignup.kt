package com.example.leafdetection

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import kotlin.math.sign

class LoginSignup : AppCompatActivity() {
    private var tabLayout:TabLayout?=null
    private var frameLayout:FrameLayout?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
        window.statusBarColor=resources.getColor(R.color.black)

        initialize()
        fragmentTransaction(LOGIN())
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                when(tab!!.position){
                    0-> fragmentTransaction(LOGIN())
                    1-> fragmentTransaction(SIGNUP())
                }
            }

            override fun onTabUnselected(tab: Tab?) {

            }

            override fun onTabReselected(tab: Tab?) {

            }

        })




    }
    private fun initialize(){
        tabLayout=findViewById(R.id.tabLayout)
        frameLayout=findViewById(R.id.frameLayout)
    }
    private fun fragmentTransaction(fragment:Fragment){
        //1st get the fragment manager
        val fragmentManager:FragmentManager=supportFragmentManager
        //2nd fragment transaction
        val fragmentTransaction:FragmentTransaction=fragmentManager.beginTransaction()
        //3rd replace
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        //4th set the transition

        //5th commit
        fragmentTransaction.commit()
    }
}