package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInEmployeeScreen(
    onBack: () -> Unit,
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToHome: () -> Unit,
) {
    var loginIdentifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFE2E8F0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Employee Portal",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GasTrackBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                // --- Logo Section ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.favicon),
                        contentDescription = "Logo Icon",
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = GasTrackBlue, fontWeight = FontWeight.Black)) {
                                    append("Gas")
                                }
                                withStyle(style = SpanStyle(color = GasTrackRed, fontWeight = FontWeight.Black)) {
                                    append("Track")
                                }
                            },
                            fontSize = 32.sp,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Optimize Your LPG, Effortlessly.",
                            fontSize = 12.sp,
                            color = GasTrackBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- Email Input ---
                Column(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "EMAIL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextGray,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Email address" },
                        placeholder = {
                            Text(
                                "Email",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Email, contentDescription = null, tint = GasTrackBlue)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE9ECEF),
                            focusedBorderColor = GasTrackBlue,
                            unfocusedContainerColor = Color(0xFFFBFBFE),
                            focusedContainerColor = Color(0xFFFBFBFE),
                            unfocusedPlaceholderColor = Color.Black,
                            focusedPlaceholderColor = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Password Input ---
                Column(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextGray,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Password" },
                        placeholder = {
                            Text(
                                "Password",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = GasTrackBlue)
                        },
                        trailingIcon = {
                            val isVisible = passwordVisible
                            val icon = if (isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                            val description = if (isVisible) "Hide password" else "Show password"
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = description,
                                    tint = GasTrackBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE9ECEF),
                            focusedBorderColor = GasTrackBlue,
                            unfocusedContainerColor = Color(0xFFFBFBFE),
                            focusedContainerColor = Color(0xFFFBFBFE),
                            unfocusedPlaceholderColor = Color.Black,
                            focusedPlaceholderColor = Color.Black
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    Text(
                        text = "Forgot Password?",
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 12.dp)
                            .clickable { showForgotPasswordDialog = true },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GasTrackBlue
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // --- Log In Button ---
                Button(
                    enabled = loginIdentifier.isNotEmpty() && password.isNotEmpty(),
                    onClick = onNavigateToHome,
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GasTrackBlue,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFCBD5E1),
                        disabledContentColor = Color(0xFF374151)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LOGIN",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- Staff Support Footer ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Need help accessing your staff account?",
                        fontSize = 13.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contact your System Administrator",
                        fontSize = 13.sp,
                        color = GasTrackBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        EmployeeForgotPasswordDialog { showForgotPasswordDialog = false }
    }
}

@Composable
fun EmployeeForgotPasswordDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset Password",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter your work email to receive a password reset link.",
                    fontSize = 14.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("work@gastrack.com") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue,
                        unfocusedPlaceholderColor = Color(0xFF1F2937),
                        focusedPlaceholderColor = Color(0xFF374151)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    enabled = email.isNotEmpty(),
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GasTrackBlue)
                ) {
                    Text("Request Reset", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogInEmployeeScreenPreview() {
    LogInEmployeeScreen(onBack = {}, onNavigateToSignUp = {}, onNavigateToHome = {})
}
