package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountEmployeeScreen(
    onBack: () -> Unit,
    onActivateAccount: () -> Unit,
) {
    var employeeId by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var jobRole by remember { mutableStateOf("") }
    var securityPin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "Request ID Activation",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Side-by-side Logo Section (Faithful to Reference)
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
                        text = "Optimize Your LPG, Effortlessly.",
                        fontSize = 10.sp,
                        color = GasTrackBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Account Activation",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextDark,

            )
            Text(
                text = "Setup your staff operational access",
                fontSize = 14.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Info Box ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = StaffPortalButtonBlue.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, StaffPortalButtonBlue.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "You need your official Employee ID provided by HR to set up your mobile access.",
                        fontSize = 13.sp,
                        color = StaffPortalBlue,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Form Fields ---
            
            EmployeeInputField(
                label = "OFFICIAL EMPLOYEE ID",
                value = employeeId,
                onValueChange = { employeeId = it.uppercase() },
                placeholder = "EMP-XXXX",
                icon = Icons.Default.Badge
            )

            Spacer(modifier = Modifier.height(20.dp))

            EmployeeInputField(
                label = "FULL NAME",
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "As registered in HR",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(20.dp))

            var expandedRole by remember { mutableStateOf(false) }
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "JOB ROLE / DEPARTMENT",
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
                        value = jobRole,
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
                                    jobRole = role
                                    expandedRole = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PIN Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SET SECURITY PIN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = securityPin,
                    onValueChange = { if ((it.length <= 4) && it.all { char -> char.isDigit() }) securityPin = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(Color(0xFFFBFBFE), RoundedCornerShape(16.dp)),
                    placeholder = { Text("Set 4-digit PIN", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = StaffPortalButtonBlue)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = StaffPortalButtonBlue,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Activate Button
            Button(
                enabled = employeeId.isNotEmpty() && fullName.isNotEmpty() && securityPin.length == 4,
                onClick = onActivateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StaffPortalButtonBlue,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    disabledContentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Activate Account",
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
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CreateAccountEmployeeScreenPreview() {
    CreateAccountEmployeeScreen(onBack = {}) {}
}
