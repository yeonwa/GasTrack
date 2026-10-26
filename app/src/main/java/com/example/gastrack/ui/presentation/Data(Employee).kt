package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*
import java.util.*

@Composable
fun DataModuleEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Export") }
    var isProcessing by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    if (isProcessing) {
        ActionFeedbackDialog(
            message = feedbackMessage,
            onDismiss = { isProcessing = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F8))
    ) {
        EmployeePortalHeader(
            title = when (selectedTab) {
                "Export", "Export Logs" -> "Data Export"
                else -> "Data Import"
            },
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            EmployeeTabSelector(
                tabs = listOf("Export", "Export Logs", "Import", "Import Logs"),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = selectedTab, label = "DataTabTransition") { tab ->
                when (tab) {
                    "Export" -> ExportTab(
                        onAction = { msg ->
                            feedbackMessage = msg
                            isProcessing = true
                        }
                    )
                    "Export Logs" -> LogsTab(activityType = "Export")
                    "Import" -> ImportTab()
                    "Import Logs" -> LogsTab(activityType = "Import")
                }
            }
        }
    }
}

@Composable
fun ExportTab(
    onAction: (String) -> Unit = {}
) {
    var exportDataType by remember { mutableStateOf("Sales Data (summary report)") }
    var exportDateRange by remember { mutableStateOf("Today") }
    var exportDateFrom by remember { mutableStateOf("") }
    var exportDateTo by remember { mutableStateOf("") }
    var reportYear by remember { mutableStateOf((Calendar.getInstance().get(Calendar.YEAR) - 1).toString()) }
    var reportBrand by remember { mutableStateOf("GasTrack Premium") }

    val isAnnualReport = exportDataType.startsWith("Annual Report")
    val isReimportable = exportDataType.startsWith("Sales Line Items")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Export Options", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(16.dp))

                // Data Type Selection
                Text("Data Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))
                DropdownSelector(
                    options = DropdownOptions(listOf(
                        "Sales Data (summary report)",
                        "Sales Line Items (re-importable)",
                        "Inventory Data",
                        "Products Data",
                        "Restocking Logs",
                        "Supplier Records",
                        "Annual Report (DOE Annex AR-E-2)"
                    )),
                    selectedOption = exportDataType,
                    onOptionSelected = { exportDataType = it },
                    icon = Icons.Default.Analytics
                )

                if (!isAnnualReport) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Date Range", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    DropdownSelector(
                        options = DropdownOptions(listOf("Today", "This Week", "This Month", "Custom")),
                        selectedOption = exportDateRange,
                        onOptionSelected = { exportDateRange = it },
                        icon = Icons.Default.DateRange
                    )
                }

                if (!isAnnualReport && exportDateRange == "Custom") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("From", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = exportDateFrom,
                                onValueChange = { exportDateFrom = it },
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("To", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = exportDateTo,
                                onValueChange = { exportDateTo = it },
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                if (isAnnualReport) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Brand / Trademark", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    DropdownSelector(
                        options = DropdownOptions(listOf("GasTrack Premium", "GasTrack Standard", "ProGas", "EcoGas")),
                        selectedOption = reportBrand,
                        onOptionSelected = { reportBrand = it },
                        icon = Icons.Default.LocalFireDepartment
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Covered Year", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reportYear,
                        onValueChange = { reportYear = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Generates DOE Annex AR-E-2 — Annual Update Report on LPG Supply and Demand Balance for the selected brand.",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Note: Inventory Data, Products Data, and Supplier Records export as full current snapshot; date range filters Sales and Restocking logs.",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }

                if (isReimportable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "✓ This CSV can be re-imported directly via the Sales module.",
                        fontSize = 11.sp,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ready to Export / Format Action Card
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, tint = TextDark, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ready to Export", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark)
                Text("Select your preferred format to download", fontSize = 12.sp, color = TextGray)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!isAnnualReport) {
                        Button(
                            onClick = { onAction("Exporting $exportDataType to CSV...") },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = TextDark),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("CSV", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                    Button(
                        onClick = { onAction("Exporting $exportDataType to Excel...") },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Excel", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                    }
                    Button(
                        onClick = { onAction("Exporting $exportDataType to PDF...") },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextDark)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("PDF", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ImportTab() {
    var importDataType by remember { mutableStateOf("Sales Data") }
    var importDateRange by remember { mutableStateOf("Today") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "For importing Sales data specifically, use the 'Import Sales Data' button on the Sales page. General bulk import for other data types isn't wired up yet.",
                            fontSize = 12.sp,
                            color = StaffPortalButtonBlue,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Import Options", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Data Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))
                DropdownSelector(
                    options = DropdownOptions(listOf("Sales Data", "Inventory Data", "Products Data", "Supplier Records")),
                    selectedOption = importDataType,
                    onOptionSelected = { importDataType = it },
                    icon = Icons.Default.Analytics
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Date Range", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))
                DropdownSelector(
                    options = DropdownOptions(listOf("Today", "This Week", "This Month", "Custom")),
                    selectedOption = importDateRange,
                    onOptionSelected = { importDateRange = it },
                    icon = Icons.Default.DateRange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, tint = TextDark, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ready to Import", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark)
                Text(
                    if (importDataType == "Sales Data") "Go to the Sales page to import" else "Not yet available for this data type",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LogsTab(activityType: String) {
    var selectedLogId by remember { mutableStateOf<String?>(null) }

    val logs = remember {
        mutableStateListOf(
            DataActivityData("ACT-552", "EMP-001", "Export", "Sales Data", "sales_may_2026.pdf", "PDF", "2026-05-01", "2026-05-15", "Successful", "2026-05-15 10:30 AM"),
            DataActivityData("ACT-551", "EMP-001", "Import", "Inventory Data", "inv_update.csv", "CSV", "N/A", "N/A", "Failed", "2026-05-15 09:15 AM"),
            DataActivityData("ACT-550", "EMP-002", "Export", "Inventory Data", "stock_audit.xlsx", "Excel", "2026-01-01", "2026-05-14", "Successful", "2026-05-14 04:45 PM")
        )
    }

    val filteredLogs = logs.filter { it.activityType.equals(activityType, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                        }
                    }
                    Text(
                        text = if (activityType == "Export") "Data Export History" else "Data Import History",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextDark,
                        letterSpacing = (-0.1).sp
                    )
                }

                HorizontalDivider(color = Color(0xFFF3F4F6))

                // Table Layout
                if (filteredLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No $activityType history yet.", color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E3A5F))
                                .padding(vertical = 14.dp, horizontal = 24.dp)
                        ) {
                            Text("File", modifier = Modifier.weight(1.5f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Type", modifier = Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Format", modifier = Modifier.weight(0.8f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Status", modifier = Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text("Date", modifier = Modifier.weight(1.2f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }

                        filteredLogs.forEachIndexed { index, log ->
                            val isSelected = selectedLogId == log.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) Color(0xFFF0F7FF) else Color.White)
                                    .clickable { selectedLogId = log.id }
                                    .padding(vertical = 16.dp, horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(log.fileName, modifier = Modifier.weight(1.5f), color = TextDark, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                Text(log.dataType, modifier = Modifier.weight(1f), color = TextGray, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1)
                                Text(log.fileFormat, modifier = Modifier.weight(0.8f), color = TextGray, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1)
                                
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Surface(
                                        color = if (log.status == "Successful") Color(0xFF22C55E) else Color(0xFFEF4444),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = log.status,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                
                                Text(log.activityDate, modifier = Modifier.weight(1.2f), color = TextGray, fontWeight = FontWeight.Medium, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1)
                            }
                            if (index < filteredLogs.size - 1) {
                                HorizontalDivider(color = Color(0xFFF3F4F6))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Immutable
data class DropdownOptions(val items: List<String>)

@Composable
fun DropdownSelector(
    options: DropdownOptions,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    icon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(56.dp).clickable { expanded = true },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFBFBFE),
            border = BorderStroke(1.dp, Color(0xFFE9ECEF))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(selectedOption, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.9f)) {
            options.items.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontWeight = FontWeight.Medium) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

data class DataActivityData(
    val id: String,
    val userId: String,
    val activityType: String, // Export, Import
    val dataType: String,
    val fileName: String,
    val fileFormat: String,
    val dateFrom: String,
    val dateTo: String,
    val status: String, // Successful, Failed
    val activityDate: String
)



@Preview(showBackground = true)
@Composable
fun DataModuleEmployeeScreenPreview() {
    DataModuleEmployeeScreen()
}
