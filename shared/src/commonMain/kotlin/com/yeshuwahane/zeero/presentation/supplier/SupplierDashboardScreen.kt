package com.yeshuwahane.zeero.presentation.supplier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.usecase.LogoutUseCase
import com.yeshuwahane.zeero.presentation.components.ImagePickerButton
import com.yeshuwahane.zeero.presentation.components.ProductImage
import com.yeshuwahane.zeero.presentation.components.byteArrayToImageBitmap
import com.yeshuwahane.zeero.presentation.login.LoginScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class SupplierDashboardScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SupplierViewModel>()
        val state by viewModel.state.collectAsState()
        val logoutUseCase = koinInject<LogoutUseCase>()
        val scope = rememberCoroutineScope()

        val categories = listOf("electronics", "audio", "fashion", "others")
        val tabTitles = listOf("My Listings", if (state.editingProductId != null) "Edit Stock" else "Add Stock")

        LaunchedEffect(Unit) {
            viewModel.onIntent(SupplierIntent.LoadData)
        }

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Supplier Dashboard",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        state.currentSupplier?.let { user ->
                            Text(
                                text = "Logged in as ${user.name}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    IconButton(onClick = {
                        scope.launch {
                            logoutUseCase()
                            navigator.replaceAll(LoginScreen())
                        }
                    }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Log Out / Switch Account"
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedTabIndex == index,
                            onClick = { viewModel.onIntent(SupplierIntent.SelectTab(index)) },
                            text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
                    }
                }

                if (state.selectedTabIndex == 0) {
                    SupplierListingsTab(state.supplierProducts, viewModel)
                } else {
                    SupplierUploadFormTab(state, viewModel, categories)
                }

                if (state.showSuccess) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { viewModel.onIntent(SupplierIntent.DismissDialog) },
                        title = { Text("Success") },
                        text = {
                            Text(
                                if (state.editingProductId != null) "Listing updated successfully!"
                                else "Listing uploaded and pending approval!"
                            )
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = { viewModel.onIntent(SupplierIntent.DismissDialog) }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }

                if (state.validationError.isNotEmpty()) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { viewModel.onIntent(SupplierIntent.DismissDialog) },
                        title = { Text("Error") },
                        text = { Text(state.validationError) },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = { viewModel.onIntent(SupplierIntent.DismissDialog) }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun SupplierListingsTab(
        products: List<Product>,
        viewModel: SupplierViewModel
    ) {
        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No stock listings found.\nClick the 'Add Stock' tab to upload your first product.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 15.sp
                )
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(products.size, key = { products[it].id }) { index ->
                    val product = products[index]
                    SupplierProductCard(product, viewModel)
                }
            }
        }
    }

    @Composable
    private fun SupplierProductCard(
        product: Product,
        viewModel: SupplierViewModel
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val firstImage = product.imageUrl.split(",").firstOrNull() ?: product.imageUrl
                    ProductImage(
                        category = firstImage,
                        title = product.title,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Base Price: $${product.price}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val badgeBg = if (product.isApproved) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        val badgeTextColor = if (product.isApproved) Color(0xFF2E7D32) else Color(0xFFE65100)
                        val badgeText = if (product.isApproved) "Approved" else "Pending Approval"

                        Box(
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.onIntent(SupplierIntent.EditProduct(product)) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.onIntent(SupplierIntent.RemoveProduct(product.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Remove",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SupplierUploadFormTab(
        state: SupplierUiState,
        viewModel: SupplierViewModel,
        categories: List<String>
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (state.editingProductId != null) "Edit Stock Listing" else "Submit New Stock Listing",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Listings require administrative approval before appearing in the marketplace feed.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onIntent(SupplierIntent.UpdateTitle(it)) },
                label = { Text("Product Title") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onIntent(SupplierIntent.UpdateDescription(it)) },
                label = { Text("Product Description") },
                minLines = 3,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.priceString,
                onValueChange = { viewModel.onIntent(SupplierIntent.UpdatePrice(it)) },
                label = { Text("Starting / Base Price ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Product Gallery Images",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Add one or more images of the item.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val maxRemaining = 8 - state.selectedImages.size
                        if (maxRemaining > 0) {
                            ImagePickerButton(
                                onImagesSelected = { images ->
                                    viewModel.onIntent(SupplierIntent.AddSelectedImages(images))
                                },
                                maxSelectionLimit = maxRemaining,
                                modifier = Modifier.height(44.dp)
                            )
                        } else {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Maximum 8 images added")
                            }
                        }
                    }

                    if (state.selectedImages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.selectedImages.size) { index ->
                                val (name, bytes) = state.selectedImages[index]
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                ) {
                                    val bitmap = remember(bytes) {
                                        try {
                                            byteArrayToImageBitmap(bytes)
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.secondaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name.takeLast(6),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                            .size(20.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(Color(0xFFEF5350))
                                            .clickable {
                                                viewModel.onIntent(SupplierIntent.RemoveSelectedImage(index))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Product Image & Category Theme",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = state.selectedCategory == category
                    val gradientColors = when (category) {
                        "electronics" -> listOf(Color(0xFF8A2387), Color(0xFFE94057))
                        "audio" -> listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                        "fashion" -> listOf(Color(0xFFF9D423), Color(0xFFFF4E50))
                        else -> listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(gradientColors))
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.onIntent(SupplierIntent.UpdateCategory(category))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.replaceFirstChar { it.uppercase() },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Auction Listing",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Enable bidding for users",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                Switch(
                    checked = state.isAuction,
                    onCheckedChange = { viewModel.onIntent(SupplierIntent.ToggleAuction(it)) }
                )
            }

            AnimatedVisibility(visible = state.isAuction) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.durationHoursString,
                        onValueChange = { viewModel.onIntent(SupplierIntent.UpdateDuration(it)) },
                        label = { Text("Auction Duration (Hours)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.editingProductId != null) {
                    Button(
                        onClick = { viewModel.onIntent(SupplierIntent.CancelEdit) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Button(
                    onClick = { viewModel.onIntent(SupplierIntent.SubmitUpload) },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (state.editingProductId != null) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = "Submit"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.editingProductId != null) "Save Changes" else "Upload Product",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}