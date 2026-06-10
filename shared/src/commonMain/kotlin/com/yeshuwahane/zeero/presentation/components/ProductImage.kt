package com.yeshuwahane.zeero.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductImage(
    category: String,
    title: String,
    modifier: Modifier = Modifier
) {
    val colors = when (category.lowercase()) {
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
        val icon = when (category.lowercase()) {
            "electronics" -> Icons.Default.ShoppingCart
            "audio" -> Icons.Default.Star
            "fashion" -> Icons.Default.List
            else -> Icons.Default.Warning
        }
        
        Icon(
            imageVector = icon,
            contentDescription = category,
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
