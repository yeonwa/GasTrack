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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
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
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    var loginIdentifier by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var showForgotPasswordDialog by remember { mutableStateOf(value = false) }

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
                .drawBehind {
                    drawCircle(
                        color = StaffPortalButtonBlue.copy(alpha = 0.05f),
                        radius = 120.dp.toPx(),
                        center = Offset(size.width - 20.dp.toPx(), -20.dp.toPx())
                    )
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                // --- Subtitle ---
                Text(
                    text = "Staff Portal",
                    fontSize = 16.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Email or Employee ID Input ---
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
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = StaffPortalButtonBlue)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE9ECEF),
                            focusedBorderColor = StaffPortalButtonBlue,
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

                Spacer(modifier = Modifier.height(16.dp))

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
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = StaffPortalButtonBlue)
                        },
                        trailingIcon = {
                            val isVisible = passwordVisible
                            val icon = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (isVisible) "Hide password" else "Show password"
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = description,
                                    tint = TextGray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE9ECEF),
                            focusedBorderColor = StaffPortalButtonBlue,
                            unfocusedContainerColor = Color(0xFFFBFBFE),
                            focusedContainerColor = Color(0xFFFBFBFE),
                            unfocusedPlaceholderColor = Color.Black,
                            focusedPlaceholderColor = Color.Black
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Forgot Password?",
                        modifier = Modifier
                            .align(Alignment.End)
                            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp)
                            .clickable(onClickLabel = "Forgot Password") { showForgotPasswordDialog = true }
                            .padding(8.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StaffPortalButtonBlue
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

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
                        containerColor = StaffPortalButtonBlue,
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

                // --- Modern Footer ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "New LPG Business?",
                        fontSize = 14.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable(onClickLabel = "Register Company") { onNavigateToSignUp() }
                            .semantics(mergeDescendants = true) {
                                role = Role.Button
                                contentDescription = "Register Company"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Register Company",
                            fontSize = 14.sp,
                            color = StaffPortalButtonBlue,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    }
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
                        focusedBorderColor = StaffPortalButtonBlue,
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
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
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
