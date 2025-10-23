package com.example.proyectospotify.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectospotify.R

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                LoginScreen(
                    onContinueClick = {
                        val intent = Intent(this, LoginUserActivity::class.java)
                        startActivity(intent)
                    },
                    onRegisterClick = {
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onContinueClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // 🎨 Fondo degradado rojo oscuro
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0D0D), // negro profundo
            Color(0xFF1A0000), // rojo muy oscuro
            Color(0xFF000000)  // negro final
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // 🎵 Logo principal
            Image(
                painter = painterResource(id = R.drawable.ic_spotify_logo),
                contentDescription = "Logo de la app",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            // Título
            Text(
                text = "Bienvenido a RedSound",
                color = Color(0xFFFF5252),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Text(
                text = "Tu mundo musical en un solo toque.",
                color = Color.LightGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // 🔴 Botón principal (neón rojo)
            Button(
                onClick = onContinueClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    text = "Iniciar sesión con correo",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto divisor
            Text(
                text = "¿No tienes cuenta?",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 🔴 Botón de registro con borde rojo neón
            OutlinedButton(
                onClick = onRegisterClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF1744)),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Regístrate gratis",
                    color = Color(0xFFFF5252),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
