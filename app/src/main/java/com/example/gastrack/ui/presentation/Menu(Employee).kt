package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

@Composable
fun MenuEmployeeScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogout: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToPOS: () -> Unit = {},
    onNavigateToSales: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToRestocking: () -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToSuppliers: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToReportCompliance: () -> Unit = {},
    onNavigateToSystemDataManagement: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmployeePortalHeader(
                title = "Admin Account",
                subtitle = "Hello,",
                onProfile = onNavigateToProfile,
                showProfile = true
            )

            // --- Menu Items List (Premium Cards) ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { EmployeeMenuItem(Icons.Default.GridView, "Dashboard", "System overview & stats", onNavigateToDashboard) }
                item { EmployeeMenuItem(Icons.Default.ShoppingCart, "POS Terminal", "Direct retail checkout", onNavigateToPOS) }
                item { EmployeeMenuItem(Icons.Default.Category, "Inventory", "Stock level monitoring", onNavigateToInventory) }
                item { EmployeeMenuItem(Icons.Default.Inventory, "Products", "Catalog management", onNavigateToProducts) }
                item { EmployeeMenuItem(Icons.Default.AttachMoney, "Sales", "Transaction analytics", onNavigateToSales) }
                item { EmployeeMenuItem(Icons.Default.AutoAwesome, "Restocking", "Smart replenishment", onNavigateToRestocking) }
                item { EmployeeMenuItem(Icons.Default.Groups, "Suppliers", "Vendor directory", onNavigateToSuppliers) }
                item { EmployeeMenuItem(Icons.Default.Analytics, "Data and Reports", "Exportable system data", onNavigateToReports) }
                item { EmployeeMenuItem(Icons.Default.Assessment, "Report & Compliance", "Regulatory compliance tracker", onNavigateToReportCompliance) }
                item { EmployeeMenuItem(Icons.Default.Person, "Users", "Staff access control", onNavigateToUsers) }
                item { EmployeeMenuItem(Icons.AutoMirrored.Filled.ListAlt, "Order and Delivery", "Fulfillment tracking", onNavigateToOrders) }
                item { EmployeeMenuItem(Icons.Default.Settings, "Company Profile & Branches", "Company, Branch, & Warehouse", onNavigateToSystemDataManagement) }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    Button(
                        onClick = onNavigateToLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GasTrackRed.copy(alpha = 0.08f),
                            contentColor = GasTrackRed
                        ),
                        border = BorderStroke(1.5.dp, GasTrackRed.copy(alpha = 0.3f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = GasTrackRed)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Logout from Portal", color = GasTrackRed, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun MenuSectionHeader(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun EmployeeMenuItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = StaffPortalButtonBlue.copy(alpha = 0.08f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = StaffPortalButtonBlue,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                )
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuEmployeeScreenPreview() {
    MenuEmployeeScreen(onNavigateToDashboard = {}, onNavigateToLogout = {})
}
