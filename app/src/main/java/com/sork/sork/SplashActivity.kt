package com.sork.sork

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sork.sork.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}