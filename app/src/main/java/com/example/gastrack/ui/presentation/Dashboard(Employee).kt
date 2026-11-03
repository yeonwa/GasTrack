package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
                        label = "Sales Today",
                        value = "₱48,250.00",
                        badgeText = "+14.5%",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Transactions",
                        value = "34",
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
                        label = "Stock Attention",
                        value = "3",
                        badgeText = "View",
                        onBadgeClick = { showLowStock = true },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Best Seller",
                        value = "Gasul LPG 11kg",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    val isPercentage = badgeText.contains("%") || badgeText.startsWith("+")
                    val badgeBg = if (badgeText.contains("-")) Color(0xFF263238) else if (isPercentage) Color(0xFFDCFCE7) else Color(0xFFF1F3F4)
                    val badgeTextCol = if (badgeText.contains("-")) Color.White else if (isPercentage) Color(0xFF166534) else TextDark
                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextCol,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Y-axis labels column
                Column(
                    modifier = Modifier
                        .width(42.dp)
                        .height(120.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("16000", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text("12000", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text("8000", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text("4000", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text("0", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Canvas with grid lines, smooth red trend curve, and green data points
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val points = listOf(0.28f, 0.96f, 0.02f, 0.02f, 0.02f, 0.06f, 0.02f)
                    val stepX = width / (points.size - 1)

                    // Draw vertical grid lines for each day point
                    for (i in points.indices) {
                        val x = stepX * i
                        drawLine(
                            color = Color(0xFFF1F3F4),
                            start = androidx.compose.ui.geometry.Offset(x, 0f),
                            end = androidx.compose.ui.geometry.Offset(x, height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw horizontal grid lines (4 intervals: 16k, 12k, 8k, 4k, 0 -> 4 lines)
                    val gridLinesCount = 4
                    for (i in 0..gridLinesCount) {
                        val y = (height / gridLinesCount) * i
                        drawLine(
                            color = Color(0xFFF1F3F4),
                            start = androidx.compose.ui.geometry.Offset(0f, y),
                            end = androidx.compose.ui.geometry.Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw smooth cubic bezier path (Red line)
                    val path = Path().apply {
                        moveTo(0f, height - (points[0] * height))
                        for (i in 0 until points.size - 1) {
                            val x1 = stepX * i
                            val y1 = height - (points[i] * height)
                            val x2 = stepX * (i + 1)
                            val y2 = height - (points[i + 1] * height)
                            val cx1 = x1 + (x2 - x1) / 2f
                            val cx2 = x1 + (x2 - x1) / 2f
                            cubicTo(cx1, y1, cx2, y2, x2, y2)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFFE53935),
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // Draw green data points
                    points.forEachIndexed { i, pt ->
                        val x = stepX * i
                        val y = height - (pt * height)
                        drawCircle(
                            color = Color(0xFF2E7D32),
                            radius = 4.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.width(42.dp))
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.ui.layout.Layout(
                    content = {
                        val days = listOf("Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue")
                        days.forEach { day ->
                            Text(text = day, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { measurables, constraints ->
                    val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
                    layout(constraints.maxWidth, placeables.maxOfOrNull { it.height } ?: 0) {
                        val totalPoints = placeables.size
                        if (totalPoints > 1) {
                            val availableWidth = constraints.maxWidth
                            placeables.forEachIndexed { index, placeable ->
                                val fraction = index.toFloat() / (totalPoints - 1)
                                val x = (availableWidth - placeable.width) * fraction
                                placeable.placeRelative(x.toInt(), 0)
                            }
                        }
                    }
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
                color = if (status == "Critical") Color(0xFFE53935) else Color(0xFFFF9800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(status, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onReorderClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Reorder $name", tint = Color(0xFF595959), modifier = Modifier.size(24.dp))
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = TextDark)) { append("$user ") }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, color = TextDark)) { append("— $text") }
        }, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(time, fontSize = 11.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.Normal)
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
            color = if (status == "Critical") Color(0xFFE53935) else Color(0xFFFF9800),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(status, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
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
        Text(time, fontSize = 11.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.Normal)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardEmployeeScreenPreview() {
    DashboardEmployeeScreen()
}
