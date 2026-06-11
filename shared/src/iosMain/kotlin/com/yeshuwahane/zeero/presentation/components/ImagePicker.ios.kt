package com.yeshuwahane.zeero.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import com.yeshuwahane.zeero.getCurrentTimeMillis
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSCodingProtocol
import platform.Foundation.NSItemProviderReadingProtocol

private var activeDelegate: PHPickerViewControllerDelegateProtocol? = null

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return byteArray
}

@Composable
actual fun ImagePickerButton(
    onImagesSelected: (List<Pair<String, ByteArray>>) -> Unit,
    maxSelectionLimit: Int,
    modifier: Modifier
) {
    Button(
        onClick = {
            val window = UIApplication.sharedApplication.keyWindow 
                ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
            var topViewController = window?.rootViewController
            while (topViewController?.presentedViewController != null) {
                topViewController = topViewController.presentedViewController
            }

            if (topViewController != null) {
                val configuration = PHPickerConfiguration()
                configuration.filter = PHPickerFilter.imagesFilter()
                configuration.selectionLimit = maxSelectionLimit.toLong()

                val picker = PHPickerViewController(configuration)
                val delegate = object : NSObject(), PHPickerViewControllerDelegateProtocol {
                    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                        picker.dismissViewControllerAnimated(true, null)
                        activeDelegate = null

                        val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                        if (results.isEmpty()) return

                        val selectedImages = mutableListOf<Pair<String, ByteArray>>()
                        var remaining = results.size

                        val uiImageClass = platform.objc.objc_getClass("UIImage") as platform.Foundation.NSItemProviderReadingProtocol

                        for (result in results) {
                            val itemProvider = result.itemProvider
                            if (itemProvider.canLoadObjectOfClass(uiImageClass)) {
                                itemProvider.loadObjectOfClass(uiImageClass) { imageObj, error ->
                                    val uiImage = imageObj as? UIImage
                                    if (uiImage != null) {
                                        val jpegData = UIImageJPEGRepresentation(uiImage, 0.8)
                                        if (jpegData != null) {
                                            val bytes = jpegData.toByteArray()
                                            val name = "ios_image_${getCurrentTimeMillis()}_${selectedImages.size}.jpg"
                                            selectedImages.add(name to bytes)
                                        }
                                    }
                                    
                                    remaining--
                                    if (remaining == 0) {
                                        dispatch_async(dispatch_get_main_queue()) {
                                            onImagesSelected(selectedImages)
                                        }
                                    }
                                }
                            } else {
                                remaining--
                                if (remaining == 0) {
                                    dispatch_async(dispatch_get_main_queue()) {
                                        onImagesSelected(selectedImages)
                                    }
                                }
                            }
                        }
                    }
                }

                activeDelegate = delegate
                picker.delegate = delegate
                topViewController.presentViewController(picker, animated = true, completion = null)
            }
        },
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Images")
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add Images from iOS Gallery")
    }
}

actual fun byteArrayToImageBitmap(bytes: ByteArray): ImageBitmap {
    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}
