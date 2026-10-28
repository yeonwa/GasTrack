package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

data class MenuItemData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
)

@Composable
fun MenuEmployeeScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogout: () -> Unit,
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
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }

    val menuItems = listOf(
        MenuItemData(Icons.Default.GridView, "Dashboard", "System overview & stats", onNavigateToDashboard),
        MenuItemData(Icons.Default.ShoppingCart, "POS Terminal", "Direct retail checkout", onNavigateToPOS),
        MenuItemData(Icons.Default.Category, "Inventory", "Stock level monitoring", onNavigateToInventory),
        MenuItemData(Icons.Default.Inventory, "Products", "Catalog management", onNavigateToProducts),
        MenuItemData(Icons.Default.AttachMoney, "Sales", "Transaction analytics", onNavigateToSales),
        MenuItemData(Icons.Default.Sync, "Restocking", "Smart replenishment", onNavigateToRestocking),
        MenuItemData(Icons.Default.LocalShipping, "Order and Delivery", "Fulfillment tracking", onNavigateToOrders),
        MenuItemData(Icons.Default.Groups, "Suppliers", "Vendor directory", onNavigateToSuppliers),
        MenuItemData(Icons.Default.Assessment, "Report and Compliance", "Regulatory compliance tracker", onNavigateToReportCompliance),
        MenuItemData(Icons.Default.Storage, "Data", "Exportable system data", onNavigateToReports),
        MenuItemData(Icons.Default.Person, "Users", "Staff access control", onNavigateToUsers),
        MenuItemData(Icons.Default.Settings, "Settings", "System configuration", onNavigateToSettings),
    )

    val filteredMenuItems = remember(searchQuery, menuItems) {
        menuItems.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.subtitle.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmployeePortalHeader(
                title = "Admin Account",
                subtitle = "Hello,",
                showProfile = true,
                onProfile = onNavigateToProfile,
                rightContent = {
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )

            // --- Menu Items List (Premium Cards) ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .widthIn(max = 488.dp)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(20.dp)),
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .widthIn(max = 488.dp)
                                .fillMaxWidth(),
                            placeholder = { Text("Search for...", color = TextGray, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray) },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true
                        )
                    }
                }

                if (filteredMenuItems.isEmpty()) {
                    item {
                        Text(
                            text = "No menu items found.",
                            color = TextGray,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(filteredMenuItems.size) { index ->
                        val item = filteredMenuItems[index]
                        EmployeeMenuItem(item.icon, item.title, item.subtitle, item.onClick)
                    }
                }

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
fun EmployeeMenuItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .widthIn(max = 488.dp)
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .semantics(mergeDescendants = true) {
                contentDescription = "$title, $subtitle"
            }
            .clickable(
                onClickLabel = "$title: $subtitle",
                onClick = onClick
            ),
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
