package com.ultimatirada.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.R
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.BrandBackground
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.shared.loginFieldColors

@Composable
internal fun LoginScreen(vm: MainViewModel) {
    BrandBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 76.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.logosff),
                contentDescription = null,
                modifier = Modifier.size(118.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                "ÚLTIMA TIRADA",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Blue, Purple)),
                    fontFamily = FontFamily.Serif,
                    fontSize = 39.sp,
                    fontWeight = FontWeight.Black,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Magic: The Gathering en Ceuta",
                color = Color(0xFFB7B1CC),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(48.dp))
            LoginPanel(vm)
        }
    }
}

@Composable
internal fun LoginPanel(vm: MainViewModel) {
    var credential by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xEE19172F))
            .border(1.5.dp, DeepPurple, RoundedCornerShape(18.dp))
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Correo o @nick", color = Color(0xFFC5BED8), fontWeight = FontWeight.Black, fontSize = 16.sp)
        OutlinedTextField(
            value = credential,
            onValueChange = { credential = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            colors = loginFieldColors(),
            shape = RoundedCornerShape(11.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text("Contraseña", color = Color(0xFFC5BED8), fontWeight = FontWeight.Black, fontSize = 16.sp)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            colors = loginFieldColors(),
            shape = RoundedCornerShape(11.dp),
            visualTransformation = PasswordVisualTransformation(),
        )
        error?.let { Text(it, color = Color(0xFFFF8A80)) }
        Button(
            onClick = {
                loading = true
                vm.login(credential, password) {
                    loading = false
                    error = it
                }
            },
            enabled = !loading && credential.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4D4A60),
                contentColor = Color(0xFFEDEBFF),
                disabledContainerColor = Color(0xFF494656),
                disabledContentColor = Color(0xFF8B8798),
            ),
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.ArrowCircleRight, null, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Iniciar sesión", fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
        }
    }
}
