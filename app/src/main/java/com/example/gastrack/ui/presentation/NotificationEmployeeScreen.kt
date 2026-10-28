package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

data class ComplianceNotificationItem(
    val id: String,
    val title: String,
    val period: String,
    val dueDate: String,
    val status: String, // "Overdue" or "Due Soon"
)

@Composable
fun NotificationEmployeeScreen(
    onBack: () -> Unit,
    onNavigateToReport: () -> Unit,
) {
    // Mock compliance notifications matching React web app logic
    val overdue = listOf(
        ComplianceNotificationItem("1", "Monthly Stock Audit & Reconciliation", "January 2026", "2026-02-05", "Overdue"),
    )
    val dueSoon = listOf(
        ComplianceNotificationItem("2", "DOE Quarterly Inventory Report", "Q1 2026", "2026-04-15", "Due Soon"),
    )

    val hasNotifications = overdue.isNotEmpty() || dueSoon.isNotEmpty()

    Scaffold(
        containerColor = EmployeePortalBackground,
        topBar = {
            EmployeePortalHeader(
                title = "Notifications",
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        if (!hasNotifications) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "You're all caught up!", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "No due or overdue reports right now.", fontSize = 13.sp, color = TextGray, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (overdue.isNotEmpty()) {
                    item {
                        Text(
                            text = "OVERDUE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = GasTrackRed,
                            letterSpacing = 1.sp
                        )
                    }
                    items(overdue) { item ->
                        ComplianceNotificationCard(item, onNavigateToReport)
                    }
                }

                if (dueSoon.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DUE SOON",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E),
                            letterSpacing = 1.sp
                        )
                    }
                    items(dueSoon) { item ->
                        ComplianceNotificationCard(item, onNavigateToReport)
                    }
                }
            }
        }
    }
}

@Composable
fun ComplianceNotificationCard(item: ComplianceNotificationItem, onClick: () -> Unit) {
    val isDanger = item.status == "Overdue"
    val accentColor = if (isDanger) GasTrackRed else Color(0xFF92400E)
    val icon = if (isDanger) Icons.Default.Warning else Icons.Default.Schedule

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.period} · Due: ${item.dueDate}",
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationEmployeeScreenPreview() {
    NotificationEmployeeScreen(onBack = {}) {}
}
