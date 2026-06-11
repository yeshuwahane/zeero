package com.yeshuwahane.zeero.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import org.koin.compose.koinInject

@Composable
fun ProductImage(
    category: String,
    title: String,
    modifier: Modifier = Modifier
) {
    val httpClient = koinInject<HttpClient>()
    var imageBitmap by remember(category) { mutableStateOf<ImageBitmap?>(null) }
    var isLoadingImage by remember(category) { mutableStateOf(false) }

    val isNetworkImage = category.startsWith("http") || category.startsWith("/") || category.contains(".")

    LaunchedEffect(category) {
        if (isNetworkImage) {
            isLoadingImage = true
            try {
                val bytes = httpClient.get(category).readBytes()
                imageBitmap = byteArrayToImageBitmap(bytes)
            } catch (e: Exception) {
                // Fallback to gradient
            } finally {
                isLoadingImage = false
            }
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = title,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        val actualCategory = if (isNetworkImage) "others" else category
        val colors = when (actualCategory.lowercase()) {
            "electronics" -> listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))
            "audio" -> listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
            "fashion" -> listOf(Color(0xFFF9D423), Color(0xFFFF4E50))
            else -> listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
        }

        Box(
            modifier = modifier
                .background(Brush.linearGradient(colors)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingImage) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                val icon = when (actualCategory.lowercase()) {
                    "electronics" -> Icons.Default.ShoppingCart
                    "audio" -> Icons.Default.Star
                    "fashion" -> Icons.Default.List
                    else -> Icons.Default.Warning
                }

                Icon(
                    imageVector = icon,
                    contentDescription = actualCategory,
                    modifier = Modifier.fillMaxSize(0.35f),
                    tint = Color.White.copy(alpha = 0.4f)
                )

                Text(
                    text = title.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
