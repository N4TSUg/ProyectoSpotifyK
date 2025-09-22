package com.example.proyectospotify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.proyectospotify.screens.HomeActivity
import com.example.proyectospotify.screens.WelcomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}
