package com.yeshuwahane.zeero.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yeshuwahane.zeero.presentation.components.ProductImage
import com.yeshuwahane.zeero.getCurrentTimeMillis
import com.yeshuwahane.zeero.getPlatform
import kotlinx.coroutines.delay

import com.yeshuwahane.zeero.presentation.components.shimmerLoadingAnimation
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape

class ProductDetailScreen(private val productId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ProductDetailViewModel>()
        val state by viewModel.state.collectAsState()

        if (state.showSuccess) {
            val isAuction = state.productResource.data?.isAuction ?: false
            val message = if (isAuction) "Bid placed successfully!" else "Purchase simulated successfully!"
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { viewModel.onIntent(DetailIntent.DismissDialog) },
                title = { Text("Success") },
                text = { Text(message) },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.onIntent(DetailIntent.DismissDialog) }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (state.validationError.isNotEmpty()) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { viewModel.onIntent(DetailIntent.DismissDialog) },
                title = { Text("Error") },
                text = { Text(state.validationError) },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.onIntent(DetailIntent.DismissDialog) }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        LaunchedEffect(productId) {
            viewModel.onIntent(DetailIntent.LoadProduct(productId))
        }

        val productResource = state.productResource
        val isAuction = productResource.data?.isAuction ?: false
        if (isAuction) {
            LaunchedEffect(Unit) {
                if (!getPlatform().name.contains("iOS", ignoreCase = true)) {
                    while (true) {
                        viewModel.onIntent(DetailIntent.TickTimer(getCurrentTimeMillis()))
                        delay(1000)
                    }
                }
            }
        }

        if (productResource.isLoading()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(56.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .shimmerLoadingAnimation()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerLoadingAnimation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerLoadingAnimation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerLoadingAnimation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerLoadingAnimation()
                    )
                }
            }
            return
        }

        if (productResource.isError()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productResource.error?.message ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            return
        }

        val product = productResource.data
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Product not found",
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            return
        }

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Product Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                val images = product.imageUrl.split(",")
                val imagePagerState = rememberPagerState(pageCount = { images.size })

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    HorizontalPager(
                        state = imagePagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        ProductImage(
                            category = images[page],
                            title = product.title,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    if (images.size > 1) {
                        Row(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(images.size) { index ->
                                val isSelected = imagePagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.isAuction) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Auction",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        val remaining = product.auctionEndTimeMillis - state.currentTimeMillis
                        val timerText = if (remaining <= 0L) {
                            "Ended"
                        } else {
                            val secs = remaining / 1000
                            val hours = secs / 3600
                            val mins = (secs % 3600) / 60
                            val seconds = secs % 60
                            "${hours.toString().padStart(2, '0')}:${
                                mins.toString().padStart(2, '0')
                            }:${seconds.toString().padStart(2, '0')}"
                        }

                        Text(
                            text = "Ends in: $timerText",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remaining < 3600000) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Buy Out Mode",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = product.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Base Price: $${product.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (product.isAuction) {
                    val currentHighest = product.currentHighestBid
                    val currentBidder = product.highestBidderName

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Bidding Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (currentHighest > 0.0) "$$currentHighest" else "$${product.price}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = if (currentHighest > 0.0) "Highest Bid" else "Starting Price",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (currentBidder.isNotEmpty()) currentBidder else "No bids yet",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Bidder",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))



                            OutlinedTextField(
                                value = state.bidAmount,
                                onValueChange = { viewModel.onIntent(DetailIntent.UpdateBidAmount(it)) },
                                label = { Text("Bid Amount ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.onIntent(DetailIntent.SubmitBid(product.id)) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                enabled = !state.isPlacingBid,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                if (state.isPlacingBid) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text("Place Bid", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.onIntent(DetailIntent.SubmitBuyout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Buy Now ($${product.price})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
