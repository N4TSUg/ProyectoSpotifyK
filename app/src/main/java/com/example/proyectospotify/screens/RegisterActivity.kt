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

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                RegisterScreen(
                    onContinueClick = {
                        val intent = Intent(this, RegisterNewUserActivity::class.java)
                        startActivity(intent)
                    },
                    onBackToLoginClick = {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onContinueClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    // 🎨 Fondo degradado rojo oscuro y negro
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1A0000),
            Color(0xFF000000)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 🎵 Logo
            Image(
                painter = painterResource(id = R.drawable.ic_spotify_logo),
                contentDescription = "Logo de la app",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp)
            )

            // 🩸 Título principal
            Text(
                text = "Crea tu cuenta",
                color = Color(0xFFFF3D5C),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Únete a la experiencia musical definitiva.",
                color = Color(0xFFCCCCCC),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // 🔴 Botón principal
            Button(
                onClick = onContinueClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    text = "Continuar con correo",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ⚫ Texto divisor
            Text(
                text = "¿Ya tienes una cuenta?",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // 🔴 Botón outlined
            OutlinedButton(
                onClick = onBackToLoginClick,
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.3.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF3D5C)),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Iniciar sesión",
                    color = Color(0xFFFF3D5C),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
