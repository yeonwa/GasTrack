package com.example.gastrack.ui.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.example.gastrack.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class ComplianceReport(
    val id: String,
    val name: String,
    val reportType: String,
    val period: String,
    val dueDate: String, // ISO date "YYYY-MM-DD"
    val status: String, // "Overdue", "Due Soon", "Upcoming", "Submitted"
    val fileName: String? = null
)

@Composable
fun ReportComplianceScreen(
    onBack: () -> Unit = {}
) {
    val isoDateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val displayDateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    val todayCal = Calendar.getInstance()
    val todayStr = isoDateFormat.format(todayCal.time)

    val getDateStr: (Int) -> String = { offsetDays ->
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offsetDays) }
        isoDateFormat.format(c.time)
    }

    val reports = remember {
        mutableStateListOf(
            ComplianceReport("REP-001", "Sales Summary", "Sales Summary", "Current Week", getDateStr(2), "Due Soon", "sales_summary_current.csv"),
            ComplianceReport("REP-002", "Inventory Audit", "Inventory Audit", "Previous Period", getDateStr(-3), "Overdue", null),
            ComplianceReport("REP-003", "Restocking Logs", "Restocking Logs", "Next Period", getDateStr(10), "Upcoming", null),
            ComplianceReport("REP-004", "Tax & Revenue Compliance", "Tax Compliance", "Last Period", getDateStr(-1), "Submitted", "tax_compliance_submitted.csv")
        )
    }

    var selectedDateStr by remember { mutableStateOf(todayStr) }
    var viewYear by remember { mutableIntStateOf(todayCal.get(Calendar.YEAR)) }
    var viewMonth by remember { mutableIntStateOf(todayCal.get(Calendar.MONTH)) } // 0-based
    var showNewReportModal by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Summary calculations
    val dueSoonCount = reports.count { it.status == "Due Soon" }
    val overdueCount = reports.count { it.status == "Overdue" }
    val submittedCount = reports.count { it.status == "Submitted" }
    val totalCount = reports.size

    val reportsForSelectedDate = reports.filter { it.dueDate == selectedDateStr }

    // Current month view label
    val calView = Calendar.getInstance().apply {
        set(Calendar.YEAR, viewYear)
        set(Calendar.MONTH, viewMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val monthLabel = monthYearFormat.format(calView.time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F8))
    ) {
        // Header
        EmployeePortalHeader(
            title = "Report & Compliance",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Action & Title Bar inside content
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Regulatory Portal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = StaffPortalButtonBlue,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Compliance Tracker",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDark
                        )
                    }
                    Button(
                        onClick = { showNewReportModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Schedule", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Stat Cards Grid (2x2 for mobile friendliness)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            StatCard(
                                label = "Due Soon",
                                value = dueSoonCount.toString(),
                                sub = "Within 7 days",
                                tone = StatTone.PURPLE,
                                icon = Icons.Default.CalendarMonth
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            StatCard(
                                label = "Overdue",
                                value = overdueCount.toString(),
                                sub = "Action needed",
                                tone = StatTone.RED,
                                icon = Icons.Default.Warning
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            StatCard(
                                label = "Submitted",
                                value = submittedCount.toString(),
                                sub = monthYearFormat.format(todayCal.time),
                                tone = StatTone.GREEN,
                                icon = Icons.Default.CheckCircle
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            StatCard(
                                label = "Total Reports",
                                value = totalCount.toString(),
                                sub = "All tracked",
                                tone = StatTone.BLUE,
                                icon = Icons.Default.Description
                            )
                        }
                    }
                }
            }

            // Calendar Widget Panel
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Month Header & Navigation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = monthLabel,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(
                                    onClick = {
                                        if (viewMonth == 0) {
                                            viewMonth = 11
                                            viewYear -= 1
                                        } else {
                                            viewMonth -= 1
                                        }
                                    },
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev", modifier = Modifier.size(18.dp))
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (viewMonth == 11) {
                                            viewMonth = 0
                                            viewYear += 1
                                        } else {
                                            viewMonth += 1
                                        }
                                    },
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", modifier = Modifier.size(18.dp))
                                }
                                OutlinedButton(
                                    onClick = {
                                        viewYear = todayCal.get(Calendar.YEAR)
                                        viewMonth = todayCal.get(Calendar.MONTH)
                                        selectedDateStr = todayStr
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Today", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Weekdays
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Grid calculation using Calendar
                        val tempCal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, viewYear)
                            set(Calendar.MONTH, viewMonth)
                            set(Calendar.DAY_OF_MONTH, 1)
                        }
                        val maxDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) // Sunday = 1, Monday = 2...
                        // Convert to Monday start (Monday = 0, Sunday = 6)
                        val dayOfWeekOffset = (firstDayOfWeek + 5) % 7

                        val totalCells = ((dayOfWeekOffset + maxDay + 6) / 7) * 7

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (row in 0 until (totalCells / 7)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    for (col in 0 until 7) {
                                        val dayIndex = row * 7 + col
                                        val dayNumber = dayIndex - dayOfWeekOffset + 1
                                        val isCurrentMonthDay = dayNumber in 1..maxDay

                                        val cellDateStr = if (isCurrentMonthDay) {
                                            val c = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, viewYear)
                                                set(Calendar.MONTH, viewMonth)
                                                set(Calendar.DAY_OF_MONTH, dayNumber)
                                            }
                                            isoDateFormat.format(c.time)
                                        } else ""

                                        val dayReports = if (cellDateStr.isNotEmpty()) reports.filter { it.dueDate == cellDateStr } else emptyList()
                                        val hasReports = dayReports.isNotEmpty()
                                        val primaryStatus = dayReports.firstOrNull()?.status
                                        val isSelected = cellDateStr.isNotEmpty() && cellDateStr == selectedDateStr

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isSelected -> StaffPortalButtonBlue
                                                        hasReports -> StaffPortalButtonBlue.copy(alpha = 0.08f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .clickable(enabled = cellDateStr.isNotEmpty()) {
                                                    if (cellDateStr.isNotEmpty()) selectedDateStr = cellDateStr
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (cellDateStr.isNotEmpty()) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = dayNumber.toString(),
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected || hasReports) FontWeight.Black else FontWeight.Normal,
                                                        color = when {
                                                            isSelected -> Color.White
                                                            !isCurrentMonthDay -> Color.LightGray
                                                            else -> TextDark
                                                        }
                                                    )
                                                    if (hasReports) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    when (primaryStatus) {
                                                                        "Overdue" -> GasTrackRed
                                                                        "Due Soon" -> Color(0xFFF59E0B)
                                                                        "Submitted" -> Color(0xFF16A34A)
                                                                        else -> StaffPortalButtonBlue
                                                                    }
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Selected Date Reports
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val formattedSelectedDateHeader = try {
                            val parsedDate = isoDateFormat.parse(selectedDateStr)
                            if (parsedDate != null) displayDateFormat.format(parsedDate) else selectedDateStr
                        } catch (_: Exception) {
                            selectedDateStr
                        }

                        Text(
                            text = "Reports Due on $formattedSelectedDateHeader",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (reportsForSelectedDate.isEmpty()) {
                            Text(
                                text = "No compliance reports scheduled for this date.",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                reportsForSelectedDate.forEach { report ->
                                    SelectedDateReportItem(report = report)
                                }
                            }
                        }
                    }
                }
            }

            // All Reports Section Panel
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ALL SCHEDULED REPORTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = TextGray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            reports.forEach { report ->
                                ReportItemRow(
                                    report = report,
                                    onSubmit = {
                                        val idx = reports.indexOf(report)
                                        if (idx >= 0) {
                                            reports[idx] = report.copy(status = "Submitted", fileName = "${report.name.lowercase().replace(" ", "_")}_submitted.csv")
                                            feedbackMessage = "Marked '${report.name}' as Submitted."
                                        }
                                    },
                                    onDelete = {
                                        reports.remove(report)
                                        feedbackMessage = "Deleted report '${report.name}'."
                                    },
                                    onGenerate = {
                                        feedbackMessage = "Generated CSV for '${report.name}'."
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Stay Compliant Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, StaffPortalButtonBlue.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = StaffPortalButtonBlue.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Stay Compliant", fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextDark)
                            Text("Submit regulatory and sales reports on time to avoid penalties and maintain audit readiness.", fontSize = 11.sp, color = TextGray)
                        }
                    }
                }
            }
        }
    }

    if (showNewReportModal) {
        NewComplianceReportDialog(
            onDismiss = { showNewReportModal = false },
            onCreated = { newRep ->
                reports.add(newRep)
                feedbackMessage = "Successfully scheduled '${newRep.name}'."
            }
        )
    }

    if (feedbackMessage != null) {
        ActionFeedbackDialog(
            message = feedbackMessage!!,
            onDismiss = { feedbackMessage = null }
        )
    }
}

enum class StatTone { PURPLE, RED, GREEN, BLUE }

@Composable
fun StatCard(label: String, value: String, sub: String, tone: StatTone, icon: ImageVector) {
    val (bgColor, contentColor) = when (tone) {
        StatTone.PURPLE -> Color(0xFFEDE9FE) to Color(0xFF7C3AED)
        StatTone.RED -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        StatTone.GREEN -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
        StatTone.BLUE -> Color(0xFFDBEAFE) to Color(0xFF2563EB)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(14.dp)),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = bgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(text = label, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = contentColor)
                Text(text = sub, fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReportItemRow(
    report: ComplianceReport,
    onSubmit: () -> Unit,
    onDelete: () -> Unit,
    onGenerate: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val statusColor = when (report.status) {
        "Overdue" -> GasTrackRed
        "Due Soon" -> Color(0xFFD97706)
        "Submitted" -> Color(0xFF16A34A)
        else -> StaffPortalButtonBlue
    }

    val statusBg = when (report.status) {
        "Overdue" -> Color(0xFFFEE2E2)
        "Due Soon" -> Color(0xFFFEF3C7)
        "Submitted" -> Color(0xFFDCFCE7)
        else -> Color(0xFFDBEAFE)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFBFBFE),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F3F4))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Text(text = "Period: ${report.period} • Due: ${report.dueDate}", fontSize = 11.sp, color = TextGray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = report.status,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextGray, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Generate CSV", fontSize = 13.sp) },
                            onClick = { menuExpanded = false; onGenerate() },
                            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        if (report.status != "Submitted") {
                            DropdownMenuItem(
                                text = { Text("Mark as Submitted", fontSize = 13.sp) },
                                onClick = { menuExpanded = false; onSubmit() },
                                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete", fontSize = 13.sp, color = GasTrackRed) },
                            onClick = { menuExpanded = false; onDelete() },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedDateReportItem(report: ComplianceReport) {
    val statusColor = when (report.status) {
        "Overdue" -> GasTrackRed
        "Due Soon" -> Color(0xFFD97706)
        "Submitted" -> Color(0xFF16A34A)
        else -> StaffPortalButtonBlue
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                Text(text = "Period: ${report.period}", fontSize = 11.sp, color = TextGray)
            }
            Text(
                text = report.status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    dateValue: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                onDateSelected(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCal.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = dateValue,
            onValueChange = { onDateSelected(it) },
            placeholder = { Text("YYYY-MM-DD", color = Color.LightGray, fontSize = 10.sp, maxLines = 1) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date", tint = StaffPortalButtonBlue, modifier = Modifier.size(16.dp))
                }
            }
        )
    }
}

@Composable
fun NewComplianceReportDialog(
    onDismiss: () -> Unit,
    onCreated: (ComplianceReport) -> Unit
) {
    var reportName by remember { mutableStateOf("") }
    var reportType by remember { mutableStateOf("Sales Summary") }
    var periodLabel by remember { mutableStateOf("") }
    var periodStart by remember { mutableStateOf("") }
    var periodEnd by remember { mutableStateOf("") }

    val defaultDueDate = remember {
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
    }
    var dueDate by remember { mutableStateOf(defaultDueDate) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var typeExpanded by remember { mutableStateOf(false) }

    val reportTypes = listOf("Sales Summary", "Inventory Audit", "Restocking Logs", "Tax & Revenue Compliance")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule Compliance Report", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMsg != null) {
                    Text(text = errorMsg!!, color = GasTrackRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Report Name
                Text("Report Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = reportName,
                    onValueChange = { reportName = it },
                    placeholder = { Text("e.g. July Sales Summary", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Report Type Dropdown
                Text("Report Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { typeExpanded = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(reportType, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        reportTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    reportType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Period Label
                Text("Period Label", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = periodLabel,
                    onValueChange = { periodLabel = it },
                    placeholder = { Text("e.g. July 2026", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Period Start & Period End using DatePickerField
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DatePickerField(
                            label = "Period Start",
                            dateValue = periodStart,
                            onDateSelected = { periodStart = it }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DatePickerField(
                            label = "Period End",
                            dateValue = periodEnd,
                            onDateSelected = { periodEnd = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Due Date using DatePickerField
                DatePickerField(
                    label = "Due Date",
                    dateValue = dueDate,
                    onDateSelected = { dueDate = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons: Cancel & Schedule
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color.LightGray)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
                    }
                    Button(
                        onClick = {
                            if (reportName.isBlank() || periodLabel.isBlank() || dueDate.isBlank()) {
                                errorMsg = "Please fill in all required fields."
                            } else {
                                val newReport = ComplianceReport(
                                    id = "REP-${(100..999).random()}",
                                    name = reportName,
                                    reportType = reportType,
                                    period = periodLabel,
                                    dueDate = dueDate,
                                    status = "Upcoming",
                                    fileName = null
                                )
                                onCreated(newReport)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Text("Schedule", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportComplianceScreenPreview() {
    ReportComplianceScreen()
}
