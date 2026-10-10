package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardEmployeeScreen(
    onNavigateToMenu: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToRestocking: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmployeePortalHeader(
                title = "Hello Rei",
                subtitle = "Operations Overview",
                onBack = onNavigateToMenu,
                rightContent = {
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.size(24.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = GasTrackRed) { Text("3", color = Color.White, fontSize = 6.sp) }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = StaffPortalButtonBlue, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // --- Sales Performance & No. of Transactions Matrix ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "SALES PERFORMANCE",
                        value = "₱48,250.00",
                        icon = Icons.Default.TrendingUp,
                        accentColor = StaffPortalButtonBlue,
                        modifier = Modifier.weight(1.2f)
                    )
                    StatCard(
                        label = "TRANSACTIONS",
                        value = "34",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // --- Sales Trend Graph Section ---
                SectionHeader("SALES TREND GRAPH")
                Spacer(modifier = Modifier.height(8.dp))
                SalesTrendGraphCard()

                Spacer(modifier = Modifier.height(18.dp))

                // --- Best Seller Section ---
                SectionHeader("BEST SELLER")
                Spacer(modifier = Modifier.height(8.dp))
                BestSellerCard()

                Spacer(modifier = Modifier.height(18.dp))

                // --- Stock Attention Section ---
                SectionHeader("STOCK ATTENTION")
                Spacer(modifier = Modifier.height(8.dp))
                InventoryAlertCard(onRestockClick = onNavigateToInventory)

                Spacer(modifier = Modifier.height(18.dp))

                // --- Restock Suggestion Section ---
                SectionHeader("RESTOCK SUGGESTION")
                Spacer(modifier = Modifier.height(8.dp))
                RestockSuggestionCard(onClick = onNavigateToRestocking)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = TextGray,
        letterSpacing = 1.2.sp
    )
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, accentColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 0.5.sp)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Weekly Revenue Trend", fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextDark)
                Text(text = "+14.5% vs Last Week", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
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
fun BestSellerCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = StaffPortalButtonBlue.copy(alpha = 0.05f)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = ButtonOrange,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "11kg Standard Refill", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                Text(text = "Highest volume this month • 142 units sold", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
            }
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Top 1",
                    color = Color(0xFF4CAF50),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InventoryAlertCard(onRestockClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, GasTrackRed.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(14.dp),
                color = GasTrackRed.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = GasTrackRed,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Critical: 2.7kg Camping Cylinder", 
                    fontWeight = FontWeight.Black, 
                    fontSize = 15.sp,
                    color = TextDark
                )
                Text(
                    text = "0 cylinders remaining (Out of Stock)", 
                    fontSize = 12.sp, 
                    color = GasTrackRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onRestockClick,
                colors = ButtonDefaults.buttonColors(containerColor = GasTrackRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(text = "Manage", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun RestockSuggestionCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(14.dp),
                color = StaffPortalButtonBlue.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = null,
                    tint = StaffPortalButtonBlue,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Auto-generated Purchase Order", fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextDark)
                Text(text = "Recommended: 50x 2.7kg Camping Refill based on velocity", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TaskCard(
    title: String, 
    subTitle: String, 
    time: String, 
    status: String, 
    statusColor: Color, 
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = title, fontWeight = FontWeight.Black, fontSize = 17.sp, color = TextDark)
                Text(text = subTitle, fontSize = 13.sp, color = TextGray, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = time, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Bold)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardEmployeeScreenPreview() {
    DashboardEmployeeScreen()
}
