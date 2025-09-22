package com.example.proyectospotify.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContent {
            WelcomeScreen(
                onRegisterClick = {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                },
                onLoginClick = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun WelcomeScreen(onRegisterClick: () -> Unit, onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Millones de canciones.\nGratis en Mi App.",
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Button(
            onClick = onRegisterClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Registrarte gratis", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Iniciar sesión", color = Color.White)
        }
    }
}
