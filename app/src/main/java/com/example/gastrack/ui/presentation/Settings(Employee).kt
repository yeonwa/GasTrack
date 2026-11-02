@file:Suppress("UnstableCollections", "ComposeUnstableCollections", "Stability")

package com.example.gastrack.ui.presentation


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.ui.draw.scale
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

@Composable
fun SettingsEmployeeScreen(onBack: () -> Unit = {}) {
    var selectedTab by remember { mutableStateOf("Profile") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        PortalSuccessDialog(message = "Settings saved successfully.") {
            showSuccessDialog = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground)
            .navigationBarsPadding()
    ) {
        EmployeePortalHeader(title = "Settings", onBack = onBack)

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            EmployeeTabSelector(
                tabs = listOf("Profile", "Operations", "Financial", "System & POS"),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            when (selectedTab) {
                "Profile" -> SettingsProfileTab { showSuccessDialog = true }
                "Operations" -> SettingsOperationsTab { showSuccessDialog = true }
                "Financial" -> SettingsFinancialTab { showSuccessDialog = true }
                "System & POS" -> SettingsSystemTab { showSuccessDialog = true }
            }
        }
    }
}

@Composable
fun SettingsProfileTab(onSave: () -> Unit) {
    var companyName by remember { mutableStateOf("GasTrack Inc.") }
    var address by remember { mutableStateOf("Pasig City, Metro Manila") }
    var email by remember { mutableStateOf("admin@gastrack.com") }
    var phone by remember { mutableStateOf("09123456789") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SettingsCard(title = "Business Profile") {
            SettingsTextField("Company Name", companyName, { companyName = it })
            SettingsTextField("Address", address, { address = it })
            SettingsTextField("Contact Email", email, { email = it }, KeyboardType.Email)
            SettingsTextField("Phone", phone, { phone = it }, KeyboardType.Phone)
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("Upload Logo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = BorderColor,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                        )
                    },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFBFBFE)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TextGray, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Choose an image file", fontSize = 12.sp, color = Color(0xFF0056A6), fontWeight = FontWeight.Bold)
                }
            }
        }

        SaveActionRow(onSave)
    }
}

@Composable
fun SettingsOperationsTab(onSave: () -> Unit) {
    var openingTime by remember { mutableStateOf("08:00 AM") }
    var closingTime by remember { mutableStateOf("05:00 PM") }

    var pickupExp by remember { mutableStateOf("3") }
    var maxDelivery by remember { mutableStateOf("3") }
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDays by remember { mutableStateOf(setOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SettingsCard(title = "Business Operations & Order Policies") {
            Text("Business Days", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Select the days your business operates.", fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    val isSelected = selectedDays.contains(day)
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                selectedDays = if (isSelected) selectedDays - day else selectedDays + day
                            },
                        shape = CircleShape,
                        color = if (isSelected) StaffPortalButtonBlue else Color(0xFFF1F3F4)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = day.take(3),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            
            Text("Business Hours", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Set the opening and closing times.", fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Opening Time
                Column(modifier = Modifier.weight(1f)) {
                    Text("Opening time", fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsTimeField(timeString = openingTime, onTimeSelected = { openingTime = it })
                }
                // Closing Time
                Column(modifier = Modifier.weight(1f)) {
                    Text("Closing time", fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsTimeField(timeString = closingTime, onTimeSelected = { closingTime = it })
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            
            Text("Pickup Expiration (business days)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Set how many business days a paid pickup order can remain unclaimed before it expires.", fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(12.dp))
            SettingsTextField(label = "", value = pickupExp, onValueChange = { pickupExp = it }, keyboardType = KeyboardType.Number)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Maximum Delivery Attempts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Set the maximum number of delivery attempts allowed for an order before further resolution is required.", fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(12.dp))
            SettingsTextField(label = "", value = maxDelivery, onValueChange = { maxDelivery = it }, keyboardType = KeyboardType.Number)
        }

        SaveActionRow(onSave)
    }
}

@Composable
fun SettingsFinancialTab(onSave: () -> Unit) {
    var taxRate by remember { mutableStateOf("12") }
    var taxEnabled by remember { mutableStateOf(true) }
    var currency by remember { mutableStateOf("PHP") }
    var roundUp by remember { mutableStateOf(false) }
    var roundDown by remember { mutableStateOf(false) }
    var twoDecimal by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        var allowRefunds by remember { mutableStateOf(false) }
        var enableCancelFee by remember { mutableStateOf(false) }

        SettingsCard(title = "Financial & Tax Rules") {
            Text("Configure taxes, currency and receipt rounding below.", fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Allow Issue Refund
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Allow Issue Refund", fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Allow authorized users to record refunds across all order types.", fontSize = 11.sp, color = TextGray)
                }
                Switch(
                    checked = allowRefunds,
                    onCheckedChange = { allowRefunds = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = StaffPortalButtonBlue),
                    modifier = Modifier.scale(0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Enable Cancellation Fee
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enable Cancellation Fee", fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Deduct fees from qualifying prepaid cancellation refunds. Requires refunds to be on.", fontSize = 11.sp, color = TextGray)
                }
                Switch(
                    checked = enableCancelFee,
                    onCheckedChange = { enableCancelFee = it },
                    enabled = allowRefunds,
                    colors = SwitchDefaults.colors(checkedTrackColor = StaffPortalButtonBlue),
                    modifier = Modifier.scale(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tax Enable
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tax Enable", fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Include tax in sale calculations.", fontSize = 11.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Switch(
                        checked = taxEnabled,
                        onCheckedChange = { taxEnabled = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = StaffPortalButtonBlue),
                        modifier = Modifier.scale(0.8f)
                    )
                }
                
                // Tax Rate
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tax Rate (%)", fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Set the rate used when tax is enabled.", fontSize = 11.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    BasicTextField(
                        value = taxRate,
                        onValueChange = { taxRate = it },
                        enabled = taxEnabled,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = if(taxEnabled) TextDark else Color.Gray),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(1.dp, if(taxEnabled) Color.DarkGray else Color.LightGray, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Currency
                Column(modifier = Modifier.weight(1f)) {
                    Text("Currency", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Set the currency code used by receipts.", fontSize = 11.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSmallDropdown(currency, DropdownOptions(listOf("PHP", "USD", "EUR"))) { currency = it }
                }
                
                // Rounding
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rounding", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = roundUp, onClick = { roundUp = true; roundDown = false; twoDecimal = false }, colors = RadioButtonDefaults.colors(selectedColor = StaffPortalButtonBlue), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Round up", fontSize = 13.sp, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = roundDown, onClick = { roundDown = true; roundUp = false; twoDecimal = false }, colors = RadioButtonDefaults.colors(selectedColor = StaffPortalButtonBlue), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Round down", fontSize = 13.sp, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = twoDecimal, onClick = { twoDecimal = true; roundUp = false; roundDown = false }, colors = RadioButtonDefaults.colors(selectedColor = StaffPortalButtonBlue), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("2 decimal standard", fontSize = 13.sp, color = TextDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(24.dp))

            // Discounts Section
            var discounts by remember {
                mutableStateOf(
                    listOf(
                        "No Discount" to "0%",
                        "Senior/PWD" to "5%",
                        "Member" to "10%"
                    )
                )
            }
            var showAddDiscountForm by remember { mutableStateOf(false) }
            var newDiscountName by remember { mutableStateOf("") }
            var newDiscountPercentage by remember { mutableStateOf("") }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Discounts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Manage discount options and their availability.", fontSize = 11.sp, color = TextGray)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StaffPortalButtonBlue.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, StaffPortalButtonBlue.copy(alpha = 0.2f)),
                    modifier = Modifier.clickable { showAddDiscountForm = !showAddDiscountForm }
                ) {
                    Text(
                        text = "+ Add discount", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = StaffPortalButtonBlue, 
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            if (showAddDiscountForm) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Discount Name
                            Column(modifier = Modifier.weight(2f)) {
                                Text("Discount name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Spacer(modifier = Modifier.height(6.dp))
                                BasicTextField(
                                    value = newDiscountName,
                                    onValueChange = { newDiscountName = it },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = TextDark),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.White, RoundedCornerShape(8.dp))
                                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (newDiscountName.isEmpty()) {
                                                Text("e.g., Student", fontSize = 13.sp, color = Color.LightGray)
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }

                            // Percentage
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Percentage (%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Spacer(modifier = Modifier.height(6.dp))
                                BasicTextField(
                                    value = newDiscountPercentage,
                                    onValueChange = { newDiscountPercentage = it },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = TextDark),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.White, RoundedCornerShape(8.dp))
                                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (newDiscountPercentage.isEmpty()) {
                                                Text("e.g., 15", fontSize = 13.sp, color = Color.LightGray)
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.clickable {
                                    showAddDiscountForm = false
                                    newDiscountName = ""
                                    newDiscountPercentage = ""
                                }
                            ) {
                                Text(
                                    "Cancel",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StaffPortalButtonBlue,
                                modifier = Modifier.clickable {
                                    if (newDiscountName.isNotBlank()) {
                                        val formattedP = if (newDiscountPercentage.endsWith("%")) newDiscountPercentage else if (newDiscountPercentage.isBlank()) "0%" else "$newDiscountPercentage%"
                                        discounts = discounts + (newDiscountName to formattedP)
                                        newDiscountName = ""
                                        newDiscountPercentage = ""
                                        showAddDiscountForm = false
                                    }
                                }
                            ) {
                                Text(
                                    "Add discount",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Discount List
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    discounts.forEachIndexed { index, (title, percentage) ->
                        DiscountRow(
                            title = title, 
                            percentage = percentage, 
                            isLast = index == discounts.lastIndex,
                            onRemove = {
                                discounts = discounts.filterIndexed { i, _ -> i != index }
                            }
                        )
                    }
                }
            }
        }

        SaveActionRow(onSave)
    }
}

@Composable
fun DiscountRow(title: String, percentage: String, isLast: Boolean, onRemove: () -> Unit = {}) {
    var isChecked by remember { mutableStateOf(true) }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isChecked, 
                    onCheckedChange = { isChecked = it }, 
                    colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue), 
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontSize = 13.sp, color = TextDark)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF1F3F4)
                ) {
                    Text(percentage, fontSize = 11.sp, color = TextDark, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Remove", fontSize = 12.sp, color = GasTrackRed, modifier = Modifier.clickable { onRemove() })
            }
        }
        if (!isLast) {
            HorizontalDivider(color = Color(0xFFF1F3F4))
        }
    }
}

@Composable
fun SettingsSystemTab(onSave: () -> Unit) {
    var receiptHeader by remember { mutableStateOf("GasTrack POS\nThank you!") }
    var showLogo by remember { mutableStateOf(true) }
    var showTax by remember { mutableStateOf(true) }
    var footerMsg by remember { mutableStateOf("Visit us again!") }
    var printSize by remember { mutableStateOf("80mm") }
    
    var autoLogout by remember { mutableStateOf("30") }
    var timezone by remember { mutableStateOf("Asia/Manila") }
    var dateFormat by remember { mutableStateOf("MM/DD/YYYY") }
    var language by remember { mutableStateOf("English") }
    var theme by remember { mutableStateOf("Light") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SettingsCard(title = "Receipt and POS Output") {
            Text("Receipt Header Text", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = receiptHeader,
                onValueChange = { receiptHeader = it },
                modifier = Modifier.widthIn(max = 488.dp).fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.DarkGray, focusedBorderColor = StaffPortalButtonBlue)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showLogo, onCheckedChange = { showLogo = it }, colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue))
                Text("Show Logo on Receipt", fontSize = 13.sp, color = TextDark)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showTax, onCheckedChange = { showTax = it }, colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue))
                Text("Show Tax Breakdown", fontSize = 13.sp, color = TextDark)
            }

            Spacer(modifier = Modifier.height(20.dp))
            SettingsTextField("Footer Message", footerMsg, { footerMsg = it })
            SettingsDropdownField("Print Size", printSize, DropdownOptions(listOf("58mm", "80mm"))) { printSize = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(title = "System Behavior Rules") {
            SettingsTextField("Auto Logout Timer (minutes)", autoLogout, { autoLogout = it }, KeyboardType.Number)
            SettingsDropdownField("System Timezone", timezone, DropdownOptions(listOf("Asia/Manila", "UTC", "America/Los_Angeles"))) { timezone = it }
            SettingsDropdownField("Date Format", dateFormat, DropdownOptions(listOf("MM/DD/YYYY", "DD/MM/YYYY", "YYYY-MM-DD"))) { dateFormat = it }
            SettingsDropdownField("Language", language, DropdownOptions(listOf("English", "Spanish"))) { language = it }
            SettingsDropdownField("Theme", theme, DropdownOptions(listOf("Light", "Dark"))) { theme = it }
        }

        SaveActionRow(onSave)
    }
}

@Composable
fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun SettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        if (label.isNotEmpty()) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .widthIn(max = 488.dp)
                .fillMaxWidth()
                .height(44.dp)
                .onFocusChanged { isFocused = it.isFocused },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = TextDark),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = if (isFocused) StaffPortalButtonBlue else Color.DarkGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        innerTextField()
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTimeField(
    timeString: String,
    onTimeSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    
    val initialHour = if (timeString.contains("PM") && !timeString.startsWith("12")) {
        (timeString.substring(0, 2).toIntOrNull() ?: 8) + 12 
    } else if (timeString.contains("AM") && timeString.startsWith("12")) {
        0 
    } else {
        timeString.substring(0, 2).toIntOrNull() ?: 8
    }
    
    val initialMinute = timeString.substring(3, 5).toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    Surface(
        modifier = Modifier.fillMaxWidth().height(44.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.DarkGray),
        color = Color.Transparent,
        onClick = { showDialog = true }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(timeString, fontSize = 15.sp, color = TextDark)
            Icon(Icons.Default.Schedule, contentDescription = "Select Time", tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour >= 12) "PM" else "AM"
                    val hour12 = if (hour % 12 == 0) 12 else hour % 12
                    
                    val formattedTime = java.util.Locale.US.let { locale ->
                        String.format(locale, "%02d:%02d %s", hour12, minute, amPm)
                    }
                    onTimeSelected(formattedTime)
                    showDialog = false
                }) {
                    Text("OK", color = StaffPortalButtonBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { 
                    Text("Cancel", color = TextGray) 
                }
            },
            text = {
                TimePicker(state = timePickerState)
            },
            containerColor = Color.White
        )
    }
}


@Composable
fun SettingsDropdownField(
    label: String,
    selectedOption: String,
    options: DropdownOptions,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        if (label.isNotEmpty()) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
        }
        Box {
            Surface(
                onClick = { expanded = true },
                modifier = Modifier.widthIn(max = 488.dp).fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.DarkGray),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedOption, color = TextDark, fontSize = 15.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.items.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false })
                }
            }
        }
    }
}

@Composable
fun SettingsSmallDropdown(
    selectedOption: String,
    options: DropdownOptions,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.DarkGray),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedOption, color = TextDark, fontSize = 13.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.items.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 13.sp) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

@Composable
fun SaveActionRow(onSave: () -> Unit) {
    Spacer(modifier = Modifier.height(24.dp))
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onSave,
            modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().defaultMinSize(minHeight = 52.dp).shadow(4.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0056A6))
        ) {
            Text(
                text = "Save Changes", 
                fontSize = 15.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Preview(showBackground = true)
@Composable
fun SettingsEmployeeScreenPreview() {
    SettingsEmployeeScreen()
}
