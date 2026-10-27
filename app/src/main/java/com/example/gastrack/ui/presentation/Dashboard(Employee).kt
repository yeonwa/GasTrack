package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gastrack.ui.theme.*

@Composable
fun DashboardEmployeeScreen(
    onNavigateToMenu: () -> Unit = {}
) {
    var showLowStock by remember { mutableStateOf(false) }
    var showTopProducts by remember { mutableStateOf(false) }
    var showActivityLog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmployeePortalHeader(
                title = "Dashboard",
                onBack = onNavigateToMenu
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // --- 4 Stat Cards Grid (React Aligned) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "SALES (TODAY)",
                        value = "₱48,250.00",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = StaffPortalButtonBlue,
                        badgeText = "+14.5%",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "TRANSACTIONS",
                        value = "34",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        accentColor = Color(0xFF4CAF50),
                        badgeText = "+2",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "STOCK ATTENTION",
                        value = "3",
                        icon = Icons.Default.Warning,
                        accentColor = GasTrackRed,
                        badgeText = "View",
                        onBadgeClick = { showLowStock = true },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "BEST SELLER (30d)",
                        value = "11kg Refill",
                        icon = Icons.Default.Star,
                        accentColor = ButtonOrange,
                        badgeText = "View",
                        onBadgeClick = { showTopProducts = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // --- Sales Trend Graph Section ---
                SectionHeader("SALES TREND (LAST 7 DAYS)")
                Spacer(modifier = Modifier.height(8.dp))
                SalesTrendGraphCard()

                Spacer(modifier = Modifier.height(18.dp))

                // --- Restock Suggestion Section ---
                SectionHeader("RESTOCK SUGGESTIONS")
                Spacer(modifier = Modifier.height(8.dp))
                RestockSuggestionListCard()
                
                Spacer(modifier = Modifier.height(18.dp))

                // --- Activity Timeline Section ---
                SectionHeader("ACTIVITY TIMELINE")
                Spacer(modifier = Modifier.height(8.dp))
                ActivityTimelineCard(onViewLogsClick = { showActivityLog = true })

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showLowStock) {
        LowStockModalDialog(onDismiss = { showLowStock = false })
    }
    if (showTopProducts) {
        TopProductsModalDialog(onDismiss = { showTopProducts = false })
    }
    if (showActivityLog) {
        ActivityLogModalDialog(onDismiss = { showActivityLog = false })
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = Color.Black,
        letterSpacing = 1.2.sp
    )
}

@Composable
fun StatCard(
    label: String, 
    value: String, 
    icon: ImageVector, 
    accentColor: Color, 
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    onBadgeClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(20.dp)).then(if (onBadgeClick != null) Modifier.clickable { onBadgeClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.padding(8.dp))
                }
                if (badgeText != null) {
                    Surface(
                        color = Color(0xFFF1F3F4),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(badgeText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 0.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun SalesTrendGraphCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val width = size.width
                val height = size.height
                val points = listOf(0.2f, 0.4f, 0.35f, 0.7f, 0.55f, 0.85f, 0.9f)
                val path = Path().apply {
                    moveTo(0f, height - (points[0] * height))
                    for (i in 1 until points.size) {
                        val x = (width / (points.size - 1)) * i
                        val y = height - (points[i] * height)
                        lineTo(x, y)
                    }
                }
                drawPath(
                    path = path,
                    color = StaffPortalButtonBlue,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                days.forEach { day ->
                    Text(text = day, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RestockSuggestionListCard() {
    var toastMessage by remember { mutableStateOf<String?>(null) }
    
    if (toastMessage != null) {
        PortalSuccessDialog(
            message = toastMessage!!,
            onDismiss = { toastMessage = null }
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            RestockRow("2.7kg Camping Refill", 8, 10, "Critical") {
                toastMessage = "Item successfully queued for restocking."
            }
            HorizontalDivider(color = Color(0xFFF1F3F4), modifier = Modifier.padding(vertical = 12.dp))
            RestockRow("11kg Standard Refill", 15, 20, "Low Stock") {
                toastMessage = "Item successfully queued for restocking."
            }
            HorizontalDivider(color = Color(0xFFF1F3F4), modifier = Modifier.padding(vertical = 12.dp))
            RestockRow("Gas Regulator (Auto)", 5, 30, "Critical") {
                toastMessage = "Item successfully queued for restocking."
            }
        }
    }
}

@Composable
fun RestockRow(name: String, stock: Int, reorderLevel: Int, status: String, onReorderClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Stock: $stock | Reorder At: $reorderLevel", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = if (status == "Critical") Color(0xFF990000) else Color(0xFFF59E0B),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(status, color = if (status == "Critical") Color.White else Color(0xFF451A03), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onReorderClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Reorder $name", tint = StaffPortalButtonBlue, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun ActivityTimelineCard(onViewLogsClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("RECENT ACTIVITY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.2.sp)
                Surface(
                    color = Color(0xFFF1F3F4),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable(onClickLabel = "View Activity Logs") { onViewLogsClick() }.semantics { contentDescription = "View Activity Logs" }
                ) {
                    Text("View Logs", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ActivityRow("Rei", "Update in Inventory: Processed stock out", "Just now")
            HorizontalDivider(color = Color(0xFFF1F3F4), modifier = Modifier.padding(vertical = 12.dp))
            ActivityRow("Patrick", "Create in POS: Processed new retail order", "2 hours ago")
            HorizontalDivider(color = Color(0xFFF1F3F4), modifier = Modifier.padding(vertical = 12.dp))
            ActivityRow("System", "Create in Restocking: Auto-generated Purchase Order", "5 hours ago")
        }
    }
}

@Composable
fun ActivityRow(user: String, text: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = TextDark)) { append("$user ") }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, color = TextDark)) { append("— $text") }
            }, fontSize = 13.sp, lineHeight = 18.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(time, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
    }
}

// --- Modals ---

@Composable
fun LowStockModalDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Products Needing Attention", fontSize = 16.sp, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                ModalLowStockRow("2.7kg Camping Refill", 0, 10, "Critical")
                HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                ModalLowStockRow("11kg Standard Refill", 15, 20, "Low Stock")
                HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                ModalLowStockRow("Gas Regulator (Auto)", 5, 30, "Critical")
            }
        }
    }
}

@Composable
fun ModalLowStockRow(name: String, stock: Int, limit: Int, status: String) {
    Row(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Stock: $stock / Reorder at: $limit", fontSize = 11.sp, color = TextGray)
        }
        Surface(
            color = if (status == "Critical") StatusInactive else Color(0xFFF59E0B),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(status, color = if (status == "Critical") Color.White else Color(0xFF451A03), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}

@Composable
fun TopProductsModalDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Best Sellers (Last 30 Days)", fontSize = 16.sp, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                TopProductRow("1. 11kg Standard Refill", "142 units sold", "₱134,900.00")
                HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                TopProductRow("2. 2.7kg Camping Refill", "85 units sold", "₱23,800.00")
                HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                TopProductRow("3. Gas Regulator (Auto)", "40 units sold", "₱18,000.00")
            }
        }
    }
}

@Composable
fun TopProductRow(name: String, sold: String, rev: String) {
    Row(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(sold, fontSize = 11.sp, color = TextGray)
        }
        Text(rev, fontWeight = FontWeight.Black, fontSize = 13.sp)
    }
}

@Composable
fun ActivityLogModalDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxHeight(0.6f)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Activity Log", fontSize = 18.sp, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    ModalActivityRow("Rei", "Update in Inventory: Processed stock out", "Just now")
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                    ModalActivityRow("Patrick", "Create in POS: Processed new retail order", "2 hours ago")
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                    ModalActivityRow("System", "Create in Restocking: Auto-generated Purchase Order", "5 hours ago")
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                    ModalActivityRow("Juana", "Update in Sales: Processed refund", "1 day ago")
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))
                    ModalActivityRow("Rei", "Create in Products: Added new inventory item", "2 days ago")
                }
            }
        }
    }
}

@Composable
fun ModalActivityRow(user: String, text: String, time: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = TextDark)) { append("$user ") }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, color = TextDark)) { append("— $text") }
        }, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(time, fontSize = 11.sp, color = TextGray)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardEmployeeScreenPreview() {
    DashboardEmployeeScreen()
}
