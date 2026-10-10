package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gastrack.ui.theme.*
import kotlinx.coroutines.delay


@Composable
fun DataModuleEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Report Generator") }
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
            title = "Data & Reports",
            subtitle = "System Intelligence",
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            EmployeeTabSelector(
                tabs = listOf("Report Generator", "Data Logs"),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = selectedTab, label = "DataTabTransition") { tab ->
                when (tab) {
                    "Report Generator" -> ReportGeneratorTab(
                        onAction = { msg ->
                            feedbackMessage = msg
                            isProcessing = true
                        }
                    )
                    "Data Logs" -> DataLogsTab()
                }
            }
        }
    }
}

@Composable
fun ReportGeneratorTab(
    onAction: (String) -> Unit = {}
) {
    var selectedDataType by remember { mutableStateOf("Sales Reports") }
    var selectedDateRange by remember { mutableStateOf("Last 7 Days") }
    var selectedFormat by remember { mutableStateOf("PDF") }

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
                // Datatype Selection
                Text("DATATYPE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    options = listOf("Sales Reports", "Inventory Status", "Supplier Performance", "User Activity"),
                    selectedOption = selectedDataType,
                    onOptionSelected = { selectedDataType = it },
                    icon = Icons.Default.Analytics
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Date Range Selection
                Text("DATE RANGE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    options = listOf("Today", "Yesterday", "Last 7 Days", "Last 30 Days", "Custom Range"),
                    selectedOption = selectedDateRange,
                    onOptionSelected = { selectedDateRange = it },
                    icon = Icons.Default.DateRange
                )

                Spacer(modifier = Modifier.height(24.dp))

                // File Format Selection
                Text("FILE FORMAT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("CSV", "EXCEL", "PDF").forEach { format ->
                        val isSelected = selectedFormat == format
                        Surface(
                            modifier = Modifier.weight(1f).height(44.dp).clickable { selectedFormat = format },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) StaffPortalButtonBlue else Color(0xFFFBFBFE),
                            border = BorderStroke(1.dp, if (isSelected) StaffPortalButtonBlue else Color(0xFFE9ECEF))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(format, fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.White else TextDark)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onAction("Generating ${selectedDataType}...") },
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.SettingsSuggest, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Generate Report", fontWeight = FontWeight.Black, fontSize = 15.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onAction("Importing external data...") },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE9ECEF))
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import", color = TextDark, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onAction("Exporting to $selectedFormat...") },
                    modifier = Modifier.weight(1f).height(56.dp).shadow(8.dp, RoundedCornerShape(28.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DropdownSelector(
    options: List<String>,
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
            options.forEach { option ->
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

@Composable
fun DataLogsTab() {
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf<DataActivityData?>(null) }

    val logs = remember { mutableStateListOf(
        DataActivityData("ACT-552", "EMP-001", "Export", "Sales Reports", "sales_may_2026.pdf", "PDF", "2026-05-01", "2026-05-15", "Success", "2026-05-15 10:30 AM"),
        DataActivityData("ACT-551", "EMP-001", "Import", "Inventory Status", "inv_update.csv", "CSV", "N/A", "N/A", "Failed", "2026-05-15 09:15 AM"),
        DataActivityData("ACT-550", "EMP-002", "Export", "Inventory Status", "stock_audit.xlsx", "EXCEL", "2026-01-01", "2026-05-14", "Success", "2026-05-14 04:45 PM")
    ) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(logs) { log ->
            DataActivityCard(
                log = log,
                onView = {
                    selectedActivity = log
                    showDetailDialog = true
                }
            )
        }
    }

    if (showDetailDialog && selectedActivity != null) {
        DataActivityDetailDialog(activity = selectedActivity!!, onDismiss = { showDetailDialog = false })
    }
}

data class DataActivityData(
    val id: String,
    val userId: String,
    val activityType: String, // Export, Import, Generate
    val dataType: String,
    val fileName: String,
    val fileFormat: String,
    val dateFrom: String,
    val dateTo: String,
    val status: String, // Success, Failed
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
                    color = if (log.status == "Success") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = log.status.uppercase(), 
                        color = if (log.status == "Success") Color(0xFF2E7D32) else GasTrackRed, 
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
                DetailValueRow("Status", activity.status, color = if(activity.status == "Success") Color(0xFF2E7D32) else GasTrackRed)
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
