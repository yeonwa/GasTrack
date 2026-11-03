package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    
    var firstName by remember { mutableStateOf("Juan") }
    var lastName by remember { mutableStateOf("Dela Cruz") }
    var email by remember { mutableStateOf("juan.delacruz@email.com") }
    var customerType by remember { mutableStateOf("Residential") }
    var contactNo by remember { mutableStateOf("+63 917 123 4567") }
    var address by remember { mutableStateOf("45 Mabini St, Brgy. Concepcion, Malabon City") }
    
    var expandedType by remember { mutableStateOf(false) }
    val customerTypes = listOf("Residential", "Commercial")

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(colors = listOf(GasTrackBlue, StaffPortalBlue)))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = 488.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!isEditMode) {
                            Button(
                                onClick = { isEditMode = true },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalBlue)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Edit Profile", fontWeight = FontWeight.Black, fontSize = 16.sp, textAlign = TextAlign.Center)
                            }
                        } else {
                            Button(
                                onClick = { isEditMode = false },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Save Changes", fontWeight = FontWeight.Black, fontSize = 16.sp, textAlign = TextAlign.Center)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { isEditMode = false },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                                shape = RoundedCornerShape(28.dp),
                                border = BorderStroke(1.dp, Color.Gray)
                            ) {
                                Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BASIC INFORMATION",
                modifier = Modifier.widthIn(max = 488.dp).fillMaxWidth(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextGray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileField("FIRST NAME", firstName, isEditMode) { firstName = it }
            Spacer(modifier = Modifier.height(28.dp))
            ProfileField("LAST NAME", lastName, isEditMode) { lastName = it }
            Spacer(modifier = Modifier.height(28.dp))
            ProfileField("EMAIL ADDRESS", email, isEditMode, keyboardType = KeyboardType.Email) { email = it }
            Spacer(modifier = Modifier.height(28.dp))

            Column(modifier = Modifier.widthIn(max = 488.dp).fillMaxWidth()) {
                Text(
                    text = "CUSTOMER TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp
                )
                if (isEditMode) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType }
                    ) {
                        OutlinedTextField(
                            value = customerType,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE9ECEF),
                                focusedBorderColor = GasTrackBlue,
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            customerTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        customerType = type
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = customerType,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            ProfileField("CONTACT NO.", contactNo, isEditMode, keyboardType = KeyboardType.Phone) { contactNo = it }
            Spacer(modifier = Modifier.height(28.dp))
            ProfileField("COMPLETE ADDRESS", address, isEditMode, minLines = 2) { address = it }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileField(
    label: String, 
    value: String, 
    isEditMode: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.widthIn(max = 488.dp).fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = TextGray,
            letterSpacing = 1.sp
        )
        if (isEditMode) {
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                minLines = minLines,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE9ECEF),
                    focusedBorderColor = GasTrackBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.ifEmpty { "—" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onBack = {})
}
