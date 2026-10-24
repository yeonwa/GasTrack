@file:Suppress("UnstableCollections", "ComposeUnstableCollections", "Stability")

package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    ) {
        EmployeePortalHeader(title = "Settings", onBack = onBack)

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            EmployeeTabSelector(
                tabs = listOf("Profile", "Operations", "Financial", "System & POS"),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = selectedTab, label = "SettingsTabs") { tab ->
                when (tab) {
                    "Profile" -> SettingsProfileTab { showSuccessDialog = true }
                    "Operations" -> SettingsOperationsTab { showSuccessDialog = true }
                    "Financial" -> SettingsFinancialTab { showSuccessDialog = true }
                    "System & POS" -> SettingsSystemTab { showSuccessDialog = true }
                }
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
        SettingsCard(title = "Business Profile", icon = Icons.Default.Storefront) {
            SettingsTextField("Company Name", companyName, { companyName = it })
            SettingsTextField("Address", address, { address = it })
            SettingsTextField("Contact Email", email, { email = it }, KeyboardType.Email)
            SettingsTextField("Phone", phone, { phone = it }, KeyboardType.Phone)
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("Upload Logo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                color = Color(0xFFFBFBFE)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TextGray, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Choose an image file", fontSize = 12.sp, color = StaffPortalButtonBlue, fontWeight = FontWeight.Bold)
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
    var maxDelivery by remember { mutableStateOf("2") }
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDays by remember { mutableStateOf(setOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SettingsCard(title = "Business Operations & Order Policies", icon = Icons.Default.Schedule) {
            Text("Business Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                days.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { day ->
                            Row(
                                modifier = Modifier.weight(1f).padding(bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedDays.contains(day),
                                    onCheckedChange = { isChecked ->
                                        selectedDays = if (isChecked) selectedDays + day else selectedDays - day
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue)
                                )
                                Text(day, fontSize = 13.sp, color = TextDark)
                            }
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    SettingsTextField("Opening Time", openingTime, { openingTime = it })
                }
                Column(modifier = Modifier.weight(1f)) {
                    SettingsTextField("Closing Time", closingTime, { closingTime = it })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SettingsTextField("Pickup Expiration (days)", pickupExp, { pickupExp = it }, KeyboardType.Number)
            Text("Set how many business days a paid pickup order can remain unclaimed before it expires.", fontSize = 10.sp, color = TextGray)
            
            Spacer(modifier = Modifier.height(12.dp))
            SettingsTextField("Maximum Delivery Attempts", maxDelivery, { maxDelivery = it }, KeyboardType.Number)
            Text("Set the maximum number of delivery attempts allowed before resolution is required.", fontSize = 10.sp, color = TextGray)
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
        SettingsCard(title = "Financial Rules", icon = Icons.Default.AccountBalance) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enable Tax Calculations", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Apply tax percentage to all retail sales", fontSize = 11.sp, color = TextGray)
                }
                Switch(
                    checked = taxEnabled,
                    onCheckedChange = { taxEnabled = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = StaffPortalButtonBlue)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (taxEnabled) {
                SettingsTextField("Tax Rate (%)", taxRate, { taxRate = it }, KeyboardType.Number)
            }

            SettingsDropdownField("Currency", currency, listOf("PHP", "USD", "EUR")) { currency = it }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Rounding Rules", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = twoDecimal, onCheckedChange = { twoDecimal = it }, colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue))
                Text("Standard Two Decimals (e.g. ₱10.50)", fontSize = 13.sp, color = TextDark)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = roundUp, onCheckedChange = { roundUp = it }, colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue))
                Text("Always round up to nearest whole number", fontSize = 13.sp, color = TextDark)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = roundDown, onCheckedChange = { roundDown = it }, colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue))
                Text("Always round down to nearest whole number", fontSize = 13.sp, color = TextDark)
            }
        }

        SaveActionRow(onSave)
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
        SettingsCard(title = "Receipt and POS Output", icon = Icons.Default.Receipt) {
            Text("Receipt Header Text", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = receiptHeader,
                onValueChange = { receiptHeader = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderColor, focusedBorderColor = StaffPortalButtonBlue)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showLogo, onCheckedChange = { showLogo = it }, colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue))
                Text("Show Logo on Receipt", fontSize = 13.sp, color = TextDark)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showTax, onCheckedChange = { showTax = it }, colors = CheckboxDefaults.colors(checkedColor = StaffPortalButtonBlue))
                Text("Show Tax Breakdown", fontSize = 13.sp, color = TextDark)
            }

            Spacer(modifier = Modifier.height(12.dp))
            SettingsTextField("Footer Message", footerMsg, { footerMsg = it })
            SettingsDropdownField("Print Size", printSize, listOf("58mm", "80mm")) { printSize = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(title = "System Behavior Rules", icon = Icons.Default.Computer) {
            SettingsTextField("Auto Logout Timer (minutes)", autoLogout, { autoLogout = it }, KeyboardType.Number)
            SettingsDropdownField("System Timezone", timezone, listOf("Asia/Manila", "UTC", "America/Los_Angeles")) { timezone = it }
            SettingsDropdownField("Date Format", dateFormat, listOf("MM/DD/YYYY", "DD/MM/YYYY", "YYYY-MM-DD")) { dateFormat = it }
            SettingsDropdownField("Language", language, listOf("English", "Spanish")) { language = it }
            SettingsDropdownField("Theme", theme, listOf("Light", "Dark")) { theme = it }
        }

        SaveActionRow(onSave)
    }
}

@Composable
fun SettingsCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = StaffPortalButtonBlue.copy(alpha = 0.1f)) {
                    Icon(icon, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.padding(8.dp).size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
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
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderColor,
                focusedBorderColor = StaffPortalButtonBlue
            )
        )
    }
}

@Suppress("UnstableCollections")
@Composable
fun SettingsDropdownField(
    label: String,
    selectedOption: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    SettingsDropdownField(
        label = label,
        selectedOption = selectedOption,
        options = DropdownOptions(options),
        onSelect = onSelect
    )
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
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(selectedOption, color = TextDark, fontSize = 14.sp)
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
fun SaveActionRow(onSave: () -> Unit) {
    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth().height(52.dp).shadow(4.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
    ) {
        Text("Save Changes", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Preview(showBackground = true)
@Composable
fun SettingsEmployeeScreenPreview() {
    SettingsEmployeeScreen()
}
