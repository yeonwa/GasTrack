package com.example.gastrack.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

data class ComplianceNotificationItem(
    val id: String,
    val title: String,
    val period: String,
    val days: Int,
    val status: String, // "Overdue" or "Due Soon"
)

@Composable
fun NotificationEmployeeScreen(
    onBack: () -> Unit,
    onNavigateToReport: () -> Unit,
) {
    val overdueList = remember {
        listOf(
            ComplianceNotificationItem(
                id = "1",
                title = "Inventory Audit",
                period = "September 2026",
                days = 46,
                status = "Overdue"
            ),
            ComplianceNotificationItem(
                id = "2",
                title = "Monthly Sales Summary",
                period = "October 2026",
                days = 16,
                status = "Overdue"
            )
        )
    }

    val dueSoonList = remember {
        listOf(
            ComplianceNotificationItem(
                id = "3",
                title = "DEMO-JOSE-2026 Restocking Logs",
                period = "September 2026",
                days = 2,
                status = "Due Soon"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.54f))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onBack
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Prevents clicking inside card from closing modal
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications Icon",
                            tint = TextDark,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = "Report & Compliance Notifications",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Content list
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // OVERDUE SECTION
                    if (overdueList.isNotEmpty()) {
                        item {
                            Text(
                                text = "OVERDUE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GasTrackRed,
                                letterSpacing = 0.5.sp
                            )
                        }
                        items(overdueList) { item ->
                            NotificationRowItem(
                                item = item,
                                onClick = onNavigateToReport
                            )
                        }
                    }

                    // DUE SOON SECTION
                    if (dueSoonList.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "DUE SOON",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                letterSpacing = 0.5.sp
                            )
                        }
                        items(dueSoonList) { item ->
                            NotificationRowItem(
                                item = item,
                                onClick = onNavigateToReport
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Button
                Button(
                    onClick = onNavigateToReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GasTrackBlue
                    )
                ) {
                    Text(
                        text = "View All Reports",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRowItem(
    item: ComplianceNotificationItem,
    onClick: () -> Unit
) {
    val isOverdue = item.status == "Overdue"
    val icon = if (isOverdue) Icons.Outlined.Warning else Icons.Outlined.CalendarToday
    val iconTint = if (isOverdue) GasTrackRed else Color(0xFFD97706)

    val daysText = if (isOverdue) {
        pluralStringResource(R.plurals.days_overdue, item.days, item.days)
    } else {
        pluralStringResource(R.plurals.days_due, item.days, item.days)
    }

    val subtitleText = "${item.period} \u00B7 $daysText"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitleText,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationEmployeeScreenPreview() {
    NotificationEmployeeScreen(onBack = {}) {}
}
