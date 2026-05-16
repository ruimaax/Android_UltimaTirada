package com.ultimatirada.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimatirada.app.R
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.BrandBackground
import com.ultimatirada.app.ui.shared.Purple

@Composable
internal fun SplashScreen() {
    BrandBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logosff),
                contentDescription = null,
                modifier = Modifier.size(210.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                "ÚLTIMA TIRADA",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Blue, Purple)),
                    fontFamily = FontFamily.Serif,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                "Magic: The Gathering en Ceuta",
                color = Color(0xFFB7B1CC),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
        }
    }
}
