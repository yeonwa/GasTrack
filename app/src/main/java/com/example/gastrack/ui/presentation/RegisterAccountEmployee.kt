package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountEmployeeScreen(
    onBack: () -> Unit,
    onRegisterComplete: () -> Unit,
) {
    var selectedRole by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var expandedRole by remember { mutableStateOf(value = false) }

    val roles = listOf(
        "Admin",
        "Manager",
        "Operations Supervisor",
        "Assistant Operations Supervisor",
        "Store Supervisor",
        "Assistant Store Supervisor",
        "Stockman",
        "Head Maintenance",
        "Driver",
        "Helper",
    )

    Scaffold(
        containerColor = Color(0xFFE2E8F0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Register Account",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- Branded Logo Section ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.favicon),
                    contentDescription = "Logo Icon",
                    modifier = Modifier.size(60.dp)
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
                        fontSize = 30.sp,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "STAFF REGISTRATION",
                        fontSize = 10.sp,
                        color = GasTrackBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Role Dropdown Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ROLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextGray,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedRole,
                        onExpandedChange = { expandedRole = !expandedRole },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedRole,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select your role", color = Color.LightGray) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Work, contentDescription = null, tint = StaffPortalButtonBlue)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .background(Color(0xFFFBFBFE), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = StaffPortalButtonBlue,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRole,
                            onDismissRequest = { expandedRole = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            roles.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(text = role, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        selectedRole = role
                                        expandedRole = false
                                    }
                                )
                            }
                        }
                    }
                }

                // First Name Field
                EmployeeInputField(
                    label = "FIRST NAME",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "Enter First Name",
                    icon = Icons.Default.Person
                )

                // Last Name Field
                EmployeeInputField(
                    label = "LAST NAME",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Enter Last Name",
                    icon = Icons.Default.Person
                )

                // Email Field
                EmployeeInputField(
                    label = "EMAIL ADDRESS",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "work@gastrack.com",
                    icon = Icons.Default.Email
                )

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextGray,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .background(Color(0xFFFBFBFE), RoundedCornerShape(16.dp)),
                        placeholder = { Text("Set strong password", color = Color.LightGray) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = StaffPortalButtonBlue)
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = null, tint = TextGray)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE9ECEF),
                            focusedBorderColor = StaffPortalButtonBlue,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Register Button
                Button(
                    enabled = selectedRole.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty(),
                    onClick = onRegisterComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StaffPortalButtonBlue,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Register",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Cancel Button
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextGray
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterAccountEmployeeScreenPreview() {
    RegisterAccountEmployeeScreen(onBack = {}) {}
}
