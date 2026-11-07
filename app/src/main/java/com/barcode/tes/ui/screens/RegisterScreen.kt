package com.barcode.tes.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import com.barcode.tes.ui.theme.*

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    val authError by viewModel.error.collectAsState()

    RegisterScreenContent(
        isLoading = isLoading,
        authError = authError,
        onSignUp = { email, password, fullName, whatsapp ->
            viewModel.signUp(email, password, fullName, whatsapp) {
                navController.popBackStack()
            }
        },
        onBackClick = {
            navController.popBackStack()
        }
    )
}

@Composable
fun RegisterScreenContent(
    isLoading: Boolean,
    authError: String?,
    onSignUp: (String, String, String, String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.45f)
                ),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 40.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "REGISTRASI",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Buat akun baru Anda",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Full Name Field
                    RegisterTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Nama Lengkap"
                    )

                    // WhatsApp Field
                    RegisterTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = "Nomor Whatsapp"
                    )

                    // Email Field
                    RegisterTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email"
                    )

                    // Password Field
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
                            focusedBorderColor = Color.Black.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.Transparent,
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

                    // Submit Button - Premium Glass
                    Button(
                        onClick = { onSignUp(email, password, fullName, whatsapp) },
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
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                        } else {
                            Text("SUBMIT", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("Sudah punya akun? ", color = Color.Black.copy(alpha = 0.6f))
                            Text("LOGIN", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Black.copy(alpha = 0.4f)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = Color.Black.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black.copy(alpha = 0.4f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TesTheme {
        RegisterScreenContent(
            isLoading = false,
            authError = null,
            onSignUp = { _, _, _, _ -> }
        )
    }
}
