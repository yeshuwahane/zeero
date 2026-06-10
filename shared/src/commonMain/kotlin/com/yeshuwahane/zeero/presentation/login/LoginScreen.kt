package com.yeshuwahane.zeero.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.presentation.admin.AdminOverviewScreen
import com.yeshuwahane.zeero.presentation.marketplace.CustomerMarketplaceScreen
import com.yeshuwahane.zeero.presentation.supplier.SupplierDashboardScreen

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<LoginViewModel>()
        val state by viewModel.state.collectAsState()
        val effect by viewModel.effect.collectAsState()

        var isSignUpMode by remember { mutableStateOf(false) }
        var mockNameInput by remember { mutableStateOf("") }

        LaunchedEffect(effect) {
            effect?.let {
                when (it) {
                    LoginEffect.NavigateToCustomerMarketplace -> navigator.replaceAll(
                        CustomerMarketplaceScreen()
                    )
                    LoginEffect.NavigateToSupplierDashboard -> navigator.replaceAll(
                        SupplierDashboardScreen()
                    )
                    LoginEffect.NavigateToAdminDashboard -> navigator.replaceAll(AdminOverviewScreen())
                }
                viewModel.resetEffect()
            }
        }

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ZEEROSTOCK",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = "B2B Trading & Auction Terminal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                TabRow(
                    selectedTabIndex = state.selectedRole.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    UserRole.values().forEach { role ->
                        Tab(
                            selected = state.selectedRole == role,
                            onClick = {
                                viewModel.onIntent(LoginIntent.SelectRole(role))
                                isSignUpMode = false
                            },
                            text = {
                                Text(
                                    text = role.name.replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isSignUpMode) "Supplier/Client Signup" else "${state.selectedRole.name} Login",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedVisibility(visible = isSignUpMode) {
                            Column {
                                OutlinedTextField(
                                    value = mockNameInput,
                                    onValueChange = { mockNameInput = it },
                                    label = { Text("Display / Company Name") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "Name"
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.onIntent(LoginIntent.UpdateEmail(it)) },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onIntent(LoginIntent.UpdatePassword(it)) },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Password"
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(visible = state.errorMessage.isNotEmpty()) {
                            Text(
                                text = state.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.onIntent(LoginIntent.SubmitLogin) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (state.selectedRole) {
                                    UserRole.CUSTOMER -> MaterialTheme.colorScheme.primary
                                    UserRole.SUPPLIER -> MaterialTheme.colorScheme.secondary
                                    UserRole.ADMIN -> MaterialTheme.colorScheme.tertiary
                                }
                            ),
                            enabled = !state.isLoading
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = if (isSignUpMode) "Register & Authenticate" else "Secure Login",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (state.selectedRole != UserRole.ADMIN) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isSignUpMode) "Already have an account? " else "Need a new account? ",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isSignUpMode) "Log In" else "Sign Up",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        isSignUpMode = !isSignUpMode
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Auto-Fill Simulator Accounts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (state.selectedRole) {
                        UserRole.CUSTOMER -> {
                            AutoFillButton(
                                label = "Alice Smith (Customer)",
                                email = "alice@customer.com",
                                psw = "alice123",
                                onClick = {
                                    viewModel.onIntent(LoginIntent.UpdateEmail("alice@customer.com"))
                                    viewModel.onIntent(LoginIntent.UpdatePassword("alice123"))
                                }
                            )
                            AutoFillButton(
                                label = "Bob Jones (Customer)",
                                email = "bob@customer.com",
                                psw = "bob123",
                                onClick = {
                                    viewModel.onIntent(LoginIntent.UpdateEmail("bob@customer.com"))
                                    viewModel.onIntent(LoginIntent.UpdatePassword("bob123"))
                                }
                            )
                        }
                        UserRole.SUPPLIER -> {
                            AutoFillButton(
                                label = "Global Tech (Supplier)",
                                email = "info@globaltech.com",
                                psw = "global123",
                                onClick = {
                                    viewModel.onIntent(LoginIntent.UpdateEmail("info@globaltech.com"))
                                    viewModel.onIntent(LoginIntent.UpdatePassword("global123"))
                                }
                            )
                            AutoFillButton(
                                label = "Apex Electronics (Supplier)",
                                email = "sales@apexelectronics.com",
                                psw = "apex123",
                                onClick = {
                                    viewModel.onIntent(LoginIntent.UpdateEmail("sales@apexelectronics.com"))
                                    viewModel.onIntent(LoginIntent.UpdatePassword("apex123"))
                                }
                            )
                        }
                        UserRole.ADMIN -> {
                            AutoFillButton(
                                label = "Chief Admin (Admin)",
                                email = "admin@zeerostock.com",
                                psw = "admin123",
                                onClick = {
                                    viewModel.onIntent(LoginIntent.UpdateEmail("admin@zeerostock.com"))
                                    viewModel.onIntent(LoginIntent.UpdatePassword("admin123"))
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    @Composable
    private fun AutoFillButton(
        label: String,
        email: String,
        psw: String,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Autofill",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Email: $email | Psw: $psw",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
