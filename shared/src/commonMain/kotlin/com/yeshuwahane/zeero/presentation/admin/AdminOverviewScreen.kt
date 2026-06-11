package com.yeshuwahane.zeero.presentation.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.presentation.components.ProductImage
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import com.yeshuwahane.zeero.presentation.login.LoginScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.yeshuwahane.zeero.domain.usecase.LogoutUseCase
import com.yeshuwahane.zeero.presentation.components.shimmerLoadingAnimation

class AdminOverviewScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<AdminViewModel>()
        val state by viewModel.state.collectAsState()

        val successMessage = state.showSuccessMessage
        if (successMessage != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { viewModel.onIntent(AdminIntent.DismissDialog) },
                title = { Text("Success") },
                text = { Text(successMessage) },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.onIntent(AdminIntent.DismissDialog) }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        val errorMessage = state.showErrorMessage
        if (errorMessage != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { viewModel.onIntent(AdminIntent.DismissDialog) },
                title = { Text("Error") },
                text = { Text(errorMessage) },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.onIntent(AdminIntent.DismissDialog) }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        val logoutUseCase = koinInject<LogoutUseCase>()
        val scope = rememberCoroutineScope()
        val tabTitles = listOf("Pending Approvals", "User Directory")

        val pagerState = rememberPagerState(
            initialPage = state.selectedTabIndex,
            pageCount = { tabTitles.size }
        )

        var editingUser by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<User?>(null) }

        LaunchedEffect(pagerState.currentPage) {
            if (state.selectedTabIndex != pagerState.currentPage) {
                viewModel.onIntent(AdminIntent.SelectTab(pagerState.currentPage))
            }
        }

        LaunchedEffect(state.selectedTabIndex) {
            if (pagerState.currentPage != state.selectedTabIndex) {
                pagerState.animateScrollToPage(state.selectedTabIndex)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.onIntent(AdminIntent.LoadData)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Panel",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            state.currentAdminUser?.let { admin ->
                                val roleText = if (admin.id == "adm_01") "Chief Admin" else "Operations Manager"
                                val badgeBg = if (admin.id == "adm_01") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                val badgeTextColor = if (admin.id == "adm_01") Color(0xFF2E7D32) else Color(0xFFE65100)
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .background(badgeBg, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = roleText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeTextColor
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Platform Moderation & Control",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
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
                    .padding(horizontal = 4.dp)
            ) {
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedTabIndex == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            val res = state.pendingProductsResource
                            val isRefreshing = res.isLoading()
                            if (res.isLoading() && res.data.isNullOrEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    items(3) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .shimmerLoadingAnimation()
                                        )
                                    }
                                }
                            } else if (res.isError() && res.data.isNullOrEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = res.error?.message ?: "An error occurred",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                                    isRefreshing = isRefreshing,
                                    onRefresh = { viewModel.onIntent(AdminIntent.LoadData) },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    PendingApprovalsTab(res.data ?: emptyList(), state, viewModel)
                                }
                            }
                        }
                        1 -> {
                            val res = state.usersResource
                            val isRefreshing = res.isLoading()
                            if (res.isLoading() && res.data.isNullOrEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    items(5) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .shimmerLoadingAnimation()
                                        )
                                    }
                                }
                            } else if (res.isError() && res.data.isNullOrEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = res.error?.message ?: "An error occurred",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                                    isRefreshing = isRefreshing,
                                    onRefresh = { viewModel.onIntent(AdminIntent.LoadData) },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    UserDirectoryTab(res.data ?: emptyList(), state, viewModel, onEditClick = { editingUser = it })
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit User Dialog
        if (editingUser != null) {
            val user = editingUser!!
            var name by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(user.name) }
            var email by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(user.email) }
            var password by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(user.password) }
            var role by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(user.role) }

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { editingUser = null },
                title = { Text("Edit User Details") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text("Role", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(UserRole.CUSTOMER, UserRole.SUPPLIER, UserRole.ADMIN).forEach { r ->
                                val selected = role == r
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { role = r }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = r.name,
                                        fontSize = 10.sp,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.onIntent(AdminIntent.EditUser(user.id, name, email, password, role))
                        editingUser = null
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { editingUser = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    @Composable
    private fun PendingApprovalsTab(pendingProducts: List<Product>, state: AdminUiState, viewModel: AdminViewModel) {
        if (pendingProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "All Clear",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Listings Pending Approval",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(pendingProducts, key = { it.id }) { product ->
                    PendingProductCard(product, state, viewModel)
                }
            }
        }
    }

    @Composable
    private fun PendingProductCard(product: Product, state: AdminUiState, viewModel: AdminViewModel) {
        val isReadOnly = false

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Base Price: $${product.price}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .background(
                                    color = if (product.isAuction) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (product.isAuction) "Auction Listing" else "Fixed Price",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (product.isAuction) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isReadOnly) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Moderation restricted to Chief Admin",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    val isProcessing = state.processingProductId == product.id
                    val isAnyProcessing = state.processingProductId != null

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.onIntent(AdminIntent.RejectProduct(product.id)) },
                            enabled = !isAnyProcessing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Reject",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Reject",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.onIntent(AdminIntent.ApproveProduct(product.id)) },
                            enabled = !isAnyProcessing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE8F5E9),
                                disabledContainerColor = Color(0xFFE8F5E9).copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Approve",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Approve",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun UserDirectoryTab(
        users: List<User>,
        state: AdminUiState,
        viewModel: AdminViewModel,
        onEditClick: (User) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(users, key = { it.id }) { user ->
                UserRowCard(user, state, viewModel, onEditClick)
            }
        }
    }

    @Composable
    private fun UserRowCard(
        user: User,
        state: AdminUiState,
        viewModel: AdminViewModel,
        onEditClick: (User) -> Unit
    ) {
        val currentAdmin = state.currentAdminUser
        val isChiefAdmin = currentAdmin?.id == "adm_01"
        val isSelf = user.id == currentAdmin?.id

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = user.name + if (isSelf) " (You)" else "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = user.email,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Password: ${user.password}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val (bgChipColor, textChipColor) = when (user.role) {
                        UserRole.CUSTOMER -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
                        UserRole.SUPPLIER -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
                        UserRole.ADMIN -> Pair(Color(0xFFF3E5F5), Color(0xFF6A1B9A))
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = bgChipColor,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = user.role.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = textChipColor
                        )
                    }

                    if (isChiefAdmin && !isSelf) {
                        val isDeleting = state.deletingUserId == user.id
                        IconButton(
                            onClick = { onEditClick(user) },
                            enabled = state.deletingUserId == null,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit User",
                                tint = if (state.deletingUserId == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (isDeleting) {
                            Box(
                                modifier = Modifier.size(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFFE53935)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.onIntent(AdminIntent.DeleteUser(user.id)) },
                                enabled = state.deletingUserId == null,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete User",
                                    tint = if (state.deletingUserId == null) Color(0xFFE53935) else Color(0xFFE53935).copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else if (currentAdmin?.id == "adm_02" && !isSelf) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Read-Only",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}