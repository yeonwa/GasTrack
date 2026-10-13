package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
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
            .background(Color(0xFFE2E8F0))
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
                    "Import" -> ImportTab(
                        onAction = { msg ->
                            feedbackMessage = msg
                            isProcessing = true
                        }
                    )
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
            modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, StaffPortalButtonBlue.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("EXPORT OPTIONS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

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
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
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
fun ImportTab(
    onAction: (String) -> Unit = {}
) {
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
            modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, StaffPortalButtonBlue.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "For importing Sales data specifically, use the 'Import Sales Data' button on the Sales page. General bulk import for other data types isn't wired up yet.",
                    fontSize = 13.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text("IMPORT OPTIONS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

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
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onAction("Importing $importDataType...") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Proceed Import", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LogsTab(activityType: String) {
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf<DataActivityData?>(null) }

    val logs = remember {
        mutableStateListOf(
            DataActivityData("ACT-552", "EMP-001", "Export", "Sales Data", "sales_may_2026.pdf", "PDF", "2026-05-01", "2026-05-15", "Successful", "2026-05-15 10:30 AM"),
            DataActivityData("ACT-551", "EMP-001", "Import", "Inventory Data", "inv_update.csv", "CSV", "N/A", "N/A", "Failed", "2026-05-15 09:15 AM"),
            DataActivityData("ACT-550", "EMP-002", "Export", "Inventory Data", "stock_audit.xlsx", "Excel", "2026-01-01", "2026-05-14", "Successful", "2026-05-14 04:45 PM")
        )
    }

    val filteredLogs = logs.filter { it.activityType.equals(activityType, ignoreCase = true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                Text(
                    text = if (activityType == "Export") "Data Export History" else "Data Import History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No $activityType history yet.", color = TextGray, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            items(filteredLogs) { log ->
                DataActivityCard(
                    log = log,
                    onView = {
                        selectedActivity = log
                        showDetailDialog = true
                    }
                )
            }
        }
    }

    if (showDetailDialog && selectedActivity != null) {
        DataActivityDetailDialog(activity = selectedActivity!!, onDismiss = { showDetailDialog = false })
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

@Composable
fun DataActivityCard(log: DataActivityData, onView: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = log.id, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                    Text(text = log.activityDate, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = if (log.status == "Successful") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = log.status.uppercase(), 
                        color = if (log.status == "Successful") Color(0xFF2E7D32) else GasTrackRed, 
                        fontSize = 8.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(color = Color(0xFFFBFBFE), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFF1F3F4)), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ACTIVITY", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black)
                        Text(log.activityType, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FILE NAME", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black)
                        Text(log.fileName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StaffPortalButtonBlue, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "By: ${log.userId}", fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
                EmployeeActionItem(Icons.Default.Visibility, "View", StaffPortalButtonBlue, modifier = Modifier.width(100.dp), onClick = onView)
            }
        }
    }
}

@Composable
fun DataActivityDetailDialog(activity: DataActivityData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.92f)) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Activity Insight", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                DetailValueRow("Activity ID", activity.id)
                DetailValueRow("Processed By", activity.userId)
                DetailValueRow("Activity Type", activity.activityType, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Data Source", activity.dataType)
                DetailValueRow("File Name", activity.fileName)
                DetailValueRow("File Format", activity.fileFormat, isBold = true)
                DetailValueRow("Coverage From", activity.dateFrom)
                DetailValueRow("Coverage To", activity.dateTo)
                DetailValueRow("Status", activity.status, color = if(activity.status == "Successful") Color(0xFF2E7D32) else GasTrackRed)
                DetailValueRow("Timestamp", activity.activityDate)

                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)) {
                    Text("Close Audit", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DataModuleEmployeeScreenPreview() {
    DataModuleEmployeeScreen()
}
