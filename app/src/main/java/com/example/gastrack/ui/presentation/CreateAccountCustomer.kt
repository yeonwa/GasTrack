package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.window.Dialog
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountCustomerScreen(
    onBack: () -> Unit,
    onAccountCreated: () -> Unit = {}
) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var customerType by remember { mutableStateOf("Residential") }
    var mobileNumber by remember { mutableStateOf("") }
    
    // Address Fields
    var region by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var barangay by remember { mutableStateOf("") }
    var houseNoStreet by remember { mutableStateOf("") }

    val locationsMap = remember {
        val map = mutableMapOf<String, Map<String, Map<String, List<String>>>>()
        try {
            val jsonString = context.assets.open("psgc_locations.json").bufferedReader().use { it.readText() }
            val rootObj = JSONObject(jsonString)
            for (regKey in rootObj.keys()) {
                val provObj = rootObj.getJSONObject(regKey)
                val provMap = mutableMapOf<String, Map<String, List<String>>>()
                for (provKey in provObj.keys()) {
                    val cityObj = provObj.getJSONObject(provKey)
                    val cityMap = mutableMapOf<String, List<String>>()
                    for (cityKey in cityObj.keys()) {
                        val bgyArray = cityObj.getJSONArray(cityKey)
                        val bgyList = mutableListOf<String>()
                        for (i in 0 until bgyArray.length()) {
                            bgyList.add(bgyArray.getString(i))
                        }
                        cityMap[cityKey] = bgyList
                    }
                    provMap[provKey] = cityMap
                }
                map[regKey] = provMap
            }
        } catch (_: Exception) {}
        map
    }

    val availableRegions = remember(locationsMap) { locationsMap.keys.sorted() }
    val availableProvinces = remember(locationsMap, region) {
        locationsMap[region]?.keys?.sorted() ?: emptyList()
    }
    val availableCities = remember(locationsMap, region, province) {
        locationsMap[region]?.get(province)?.keys?.sorted() ?: emptyList()
    }
    val availableBarangays = remember(locationsMap, region, province, city) {
        locationsMap[region]?.get(province)?.get(city)?.sorted() ?: emptyList()
    }
    
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var expandedType by remember { mutableStateOf(false) }
    val customerTypes = listOf("Residential", "Commercial")
    
    var showOtpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = onBack,
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextDark
                )
            }
            
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
                text = "Create Your GasTrack Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextDark
            )
  
            Spacer(modifier = Modifier.height(32.dp))

            // --- Form Fields ---
            
            Text(
                text = "BASIC INFORMATION",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextGray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // First Name
            CustomerInputField(
                label = "FIRST NAME",
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = "e.g. Juan"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Last Name
            CustomerInputField(
                label = "LAST NAME",
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = "e.g. Dela Cruz"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Address
            CustomerInputField(
                label = "EMAIL ADDRESS",
                value = email,
                onValueChange = { email = it },
                placeholder = "e.g. juan@email.com"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Customer Type Dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CUSTOMER TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = customerType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedBorderColor = GasTrackBlue,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mobile Number (Modern Classic Style)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "MOBILE NUMBER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+63",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(0.6f).width(1.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                    TextField(
                        value = mobileNumber,
                        onValueChange = { if (it.length <= 10) mobileNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("987 654 321", color = Color.LightGray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "DELIVERY ADDRESS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = StaffPortalBlue,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 12.dp)
            )

            // Region
            CustomerDropdownField(
                label = "REGION",
                selectedOption = region,
                options = CustomerDropdownOptions(availableRegions),
                onSelect = { selectedReg ->
                    region = selectedReg
                    val provs = locationsMap[selectedReg]?.keys?.sorted() ?: emptyList()
                    province = if (provs.size == 1) provs[0] else ""
                    city = ""
                    barangay = ""
                }
            )

            // Province (Only appears when the region has multiple provinces to choose from)
            if (availableProvinces.size > 1) {
                Spacer(modifier = Modifier.height(16.dp))
                CustomerDropdownField(
                    label = "PROVINCE",
                    selectedOption = province,
                    options = CustomerDropdownOptions(availableProvinces),
                    onSelect = { 
                        province = it
                        city = ""
                        barangay = ""
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // City / Municipality
            CustomerDropdownField(
                label = "CITY / MUNICIPALITY",
                selectedOption = city,
                options = CustomerDropdownOptions(availableCities),
                onSelect = { 
                    city = it
                    barangay = ""
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barangay
            CustomerDropdownField(
                label = "BARANGAY",
                selectedOption = barangay,
                options = CustomerDropdownOptions(availableBarangays),
                onSelect = { 
                    barangay = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // House No. / Street
            CustomerInputField(
                label = "HOUSE NO. / STREET / BUILDING",
                value = houseNoStreet,
                onValueChange = { houseNoStreet = it },
                placeholder = "e.g. 45 Mabini St."
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CREATE PASSWORD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Set a secure password", color = Color.LightGray) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextGray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedBorderColor = GasTrackBlue,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Create Account Button
            Button(
                enabled = firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && mobileNumber.isNotEmpty() && password.isNotEmpty() && city.isNotEmpty() && barangay.isNotEmpty(),
                onClick = { showOtpDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonOrange,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Verify Email Address",
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
        }
    }

    if (showOtpDialog) {
        EmailVerificationDialog(
            email = email,
            onDismiss = { showOtpDialog = false },
            onVerified = {
                showOtpDialog = false
                onAccountCreated()
            }
        )
    }
}

@Composable
fun EmailVerificationDialog(
    email: String,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }

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
                    text = "Verify Email",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter the 6-digit verification code sent to\n$email",
                    fontSize = 14.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0 0 0 0 0 0", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedBorderColor = GasTrackBlue,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    enabled = otpCode.length == 6,
                    onClick = onVerified,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalBlue),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Verify & Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                TextButton(onClick = { /* Resend logic */ }) {
                    Text("Resend Code", color = GasTrackRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomerInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = TextGray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            shape = RoundedCornerShape(16.dp),
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                focusedBorderColor = GasTrackBlue,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
    }
}

@Immutable
data class CustomerDropdownOptions(val items: List<String>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDropdownField(
    label: String,
    selectedOption: String,
    options: CustomerDropdownOptions,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = TextGray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select $label", color = Color.LightGray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedBorderColor = GasTrackBlue,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.items.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountCustomerScreenPreview() {
    CreateAccountCustomerScreen(onBack = {})
}
