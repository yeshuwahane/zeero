package com.yeshuwahane.zeero.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun ImagePickerButton(
    onImagesSelected: (List<Pair<String, ByteArray>>) -> Unit,
    maxSelectionLimit: Int,
    modifier: Modifier = Modifier
)

expect fun byteArrayToImageBitmap(bytes: ByteArray): ImageBitmap
