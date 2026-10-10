package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterLPGCompanyScreen(
    onBack: () -> Unit,
    onRegisterComplete: () -> Unit,
) {
    var currentStep by remember { mutableIntStateOf(1) }

    var companyName by remember { mutableStateOf("") }
    var primaryBranch by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var agreedToTerms by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Register LPG Company",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (currentStep == 2) currentStep = 1 else onBack() }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Left Hero Guide Card (Dark Blue) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = GasTrackBlue
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Register your LPG Company",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Join GasTrack to digitize your inventory management and gain real-time visibility across all your branches.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Step 1
                    RegistrationStepItem(
                        stepNumber = "1",
                        title = "Company details",
                        desc = "Fill in your registered business information.",
                        isActive = currentStep == 1
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 2
                    RegistrationStepItem(
                        stepNumber = "2",
                        title = "Admin account",
                        desc = "Create the primary administrator login.",
                        isActive = currentStep == 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 3
                    RegistrationStepItem(
                        stepNumber = "3",
                        title = "Review and submit",
                        desc = "Review details and complete authorization.",
                        isActive = false
                    )
                }
            }

            // --- Form Card (White Surface) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentStep == 1) "Company Details" else "Admin Account",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDark
                        )
                        Surface(
                            color = GasTrackBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "STEP $currentStep OF 2",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = GasTrackBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (currentStep == 1) "Fill in your registered business information." else "Create the primary administrator login credentials.",
                        fontSize = 12.sp,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (currentStep == 1) {
                        // --- STEP 1: COMPANY INFORMATION & LOCATION ---
                        RegistrationSectionHeader("COMPANY INFORMATION")

                        Spacer(modifier = Modifier.height(8.dp))

                        FormLabel("Registered Company Name")
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            placeholder = { Text("e.g. Acme Gas Corporation", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        RegistrationSectionHeader("BRANCH / LOCATION")

                        Spacer(modifier = Modifier.height(8.dp))

                        FormLabel("Primary Branch Name")
                        OutlinedTextField(
                            value = primaryBranch,
                            onValueChange = { primaryBranch = it },
                            placeholder = { Text("e.g. San Juan Branch", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FormLabel("Complete Address")
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = { Text("e.g. Building No., Street Name, Barangay, City", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            enabled = companyName.isNotEmpty() && primaryBranch.isNotEmpty() && address.isNotEmpty(),
                            onClick = { currentStep = 2 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GasTrackBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Next: Admin Account",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    } else {
                        // --- STEP 2: ADMINISTRATOR ACCOUNT & SUBMIT ---
                        RegistrationSectionHeader("ADMINISTRATOR ACCOUNT")

                        Spacer(modifier = Modifier.height(8.dp))

                        FormLabel("First name")
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            placeholder = { Text("First name", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FormLabel("Last name")
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            placeholder = { Text("Last name", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FormLabel("Email address")
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("admin@company.com", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FormLabel("Password")
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Min. 8 characters", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = icon, contentDescription = "Toggle password visibility", tint = TextGray)
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        if (password.isNotEmpty() && password.length < 8) {
                            Text(
                                text = "Password must be at least 8 characters long",
                                fontSize = 11.sp,
                                color = GasTrackRed,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        FormLabel("Confirm password")
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = { Text("Re-enter password", color = Color.LightGray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(imageVector = icon, contentDescription = "Toggle password visibility", tint = TextGray)
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                            Text(
                                text = "Passwords do not match",
                                fontSize = 11.sp,
                                color = GasTrackRed,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Terms Checkbox
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.clickable { agreedToTerms = !agreedToTerms }
                        ) {
                            Checkbox(
                                checked = agreedToTerms,
                                onCheckedChange = { agreedToTerms = it },
                                colors = CheckboxDefaults.colors(checkedColor = GasTrackRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = buildAnnotatedString {
                                    append("I confirm that the information provided is accurate and that I am authorized to register this company. I agree to the ")
                                    withStyle(style = SpanStyle(color = GasTrackRed, fontWeight = FontWeight.Bold)) {
                                        append("Terms of Service")
                                    }
                                    append(" and ")
                                    withStyle(style = SpanStyle(color = GasTrackRed, fontWeight = FontWeight.Bold)) {
                                        append("Privacy Policy")
                                    }
                                    append(" in accordance with RA 10173.")
                                },
                                fontSize = 11.sp,
                                color = TextGray,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { currentStep = 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                            ) {
                                Text(
                                    text = "Back",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }

                            Button(
                                enabled = agreedToTerms && firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.length >= 8 && password == confirmPassword,
                                onClick = { showSuccessDialog = true },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GasTrackBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Submit registration",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showSuccessDialog) {
        PortalSuccessDialog(
            message = "Company Registration Submitted Successfully! Welcome to GasTrack.",
            onDismiss = {
                showSuccessDialog = false
                onRegisterComplete()
            }
        )
    }
}

@Composable
fun RegistrationSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = GasTrackRed,
        letterSpacing = 1.sp
    )
}

@Composable
fun RegistrationStepItem(stepNumber: String, title: String, desc: String, isActive: Boolean = true) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = if (isActive) GasTrackRed else Color.White.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = if (isActive) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.4f),
                lineHeight = 15.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterLPGCompanyScreenPreview() {
    RegisterLPGCompanyScreen(onBack = {}) {}
}
