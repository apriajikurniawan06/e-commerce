package com.barcode.tes.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
 import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.barcode.tes.BuildConfig
import com.barcode.tes.R
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import com.barcode.tes.ui.navigation.Screen
import com.barcode.tes.ui.theme.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    val authError by viewModel.error.collectAsState()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }

    // Observe session for navigation (especially for Google Login)
    LaunchedEffect(viewModel.currentUser) {
        if (viewModel.currentUser != null) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    LoginContent(
        isLoading = isLoading,
        authError = authError,
        onLogin = { email, password ->
            viewModel.login(email, password) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        },
        onGoogleLogin = {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                    viewModel.loginWithGoogleNative(googleIdTokenCredential.idToken)
                } catch (e: GetCredentialException) {
                    // Handle error
                } catch (e: Exception) {
                    // Handle error
                }
            }
        },
        onRegisterClick = {
            navController.navigate(Screen.Register.route)
        }
    )
}

@Composable
fun LoginContent(
    isLoading: Boolean,
    authError: String?,
    onLogin: (String, String) -> Unit,
    onGoogleLogin: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- Enhanced Background ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB2EBF2), // Light Cyan
                            Color(0xFFE1F5FE), // Very Light Blue
                            Color(0xFFF3E5F5)  // Light Purple
                        )
                    )
                )
        )

        // Floating Glass Orbs
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-120).dp, y = (-80).dp)
                .background(Color(0xFF00BFFF).copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 100.dp, y = (-150).dp)
                .background(Color(0xFF8A2BE2).copy(alpha = 0.12f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 100.dp)
                .background(Color(0xFF00E676).copy(alpha = 0.1f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- Main Glass Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.45f) // High transparency for glass feel
                ),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f)), // Soft white border
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat glass look often looks better without heavy shadows
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 40.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BISNIZKU",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Silahkan masuk ke akun Anda",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Email Field - Glass Design (Dynamic Border)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color.Black.copy(alpha = 0.4f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color.Black.copy(alpha = 0.5f), // Border muncul saat diklik (focus)
                            unfocusedBorderColor = Color.Transparent, // Border hilang saat tidak fokus
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black.copy(alpha = 0.4f)
                        )
                    )

                    // Password Field - Glass Design (Dynamic Border)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.Black.copy(alpha = 0.4f)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = Color.Black.copy(alpha = 0.7f))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color.Black.copy(alpha = 0.5f), // Border muncul saat diklik (focus)
                            unfocusedBorderColor = Color.Transparent, // Border hilang saat tidak fokus
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black.copy(alpha = 0.4f)
                        )
                    )
                    
                    if (authError != null) {
                        Text(
                            authError, 
                            color = Color(0xFFD32F2F), 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button - Premium Glass
                    Button(
                        onClick = { onLogin(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.8f),
                                        Color.White.copy(alpha = 0.6f)
                                    )
                                ),
                                RoundedCornerShape(18.dp)
                            ),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // Using brush from modifier
                            disabledContainerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                        } else {
                            Text("LOGIN", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.1f))
                        Text(
                            " ATAU ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.1f))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Google Login - Subtle Glass
                    OutlinedButton(
                        onClick = onGoogleLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google Login",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text("Masuk dengan Google", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("Belum punya akun? ", color = Color.Black.copy(alpha = 0.6f))
                            Text("REGISTRASI", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TesTheme {
        LoginContent(
            isLoading = false,
            authError = null,
            onLogin = { _, _ -> },
            onGoogleLogin = { },
            onRegisterClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingPreview() {
    TesTheme {
        LoginContent(
            isLoading = true,
            authError = null,
            onLogin = { _, _ -> },
            onGoogleLogin = { },
            onRegisterClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenErrorPreview() {
    TesTheme {
        LoginContent(
            isLoading = false,
            authError = "Email atau password salah",
            onLogin = { _, _ -> },
            onGoogleLogin = { },
            onRegisterClick = { }
        )
    }
}
