package com.example.leafdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.navigationBarColor=resources.getColor(R.color.black)
        window.statusBarColor=resources.getColor(R.color.black)

        // creating a handler obj
        val handler = Handler()
        handler.postDelayed(Runnable {
            val intent = Intent(this,LoginSignup::class.java)
            startActivity(intent)
            finish()
        },4000)
    }
}