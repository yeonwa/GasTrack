package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*

// Data Models based on Dashboard Screenshots
data class InventoryItemData(
    val id: String,
    val productID: String,
    val productName: String,
    val warehouseName: String,
    val stockOnHand: Int,
    val emptyStock: Int,
    val reorderLimit: Int,
    val isTank: Boolean,
    val status: String // Normal, Critical, Low Stock
)

data class InventoryTransactionData(
    val transactionId: String,
    val productId: String,
    val productName: String,
    val warehouseName: String,
    val transactionType: String, // In, Out, Adjust
    val quantity: Int,
    val reference: String, // Supplier, POS, etc.
    val transactionDate: String,
    val processedBy: String
)

@Composable
fun InventoryEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Inventory") }
    var showStockInForm by remember { mutableStateOf(false) }
    var showStockOutForm by remember { mutableStateOf(false) }
    var showAdjustmentForm by remember { mutableStateOf(false) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    var selectedItemForDetail by remember { mutableStateOf<InventoryItemData?>(null) }
    var selectedItemForEdit by remember { mutableStateOf<InventoryItemData?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<InventoryItemData?>(null) }

    val inventoryItems = remember { mutableStateListOf(
        InventoryItemData("INV-001", "P-001", "Gasul LPG 2.7 kg", "Pasig", 50, 10, 10, true, "Normal"),
        InventoryItemData("INV-002", "P-002", "Gasul LPG 7 kg", "San Juan", 100, 20, 10, true, "Normal"),
        InventoryItemData("INV-003", "P-003", "LPG Hose Clamp", "Pasig", 20, 0, 20, false, "Critical"),
        InventoryItemData("INV-004", "P-004", "Gasul LPG 11 kg", "San Juan", 34, 15, 10, true, "Normal"),
        InventoryItemData("INV-005", "P-005", "POL Regulator", "San Juan", 60, 0, 20, false, "Low Stock")
    ) }

    val transactions = remember { mutableStateListOf(
        InventoryTransactionData("T-001", "P-001", "Gas LPG 2.7kg", "Pasig", "In", 10, "Supplier", "01/15/2026", "U-001"),
        InventoryTransactionData("T-002", "P-002", "Gas LPG 7kg", "San Juan", "Out", 2, "POS", "01/06/2026", "U-003"),
        InventoryTransactionData("T-003", "P-003", "Gas LPG 11kg", "Pasig", "Adjust", 30, "Supplier", "01/03/2026", "U-002"),
        InventoryTransactionData("T-004", "P-004", "Cylinder 2.7kg", "San Juan", "Out", 5, "POS", "01/02/2026", "U-003"),
        InventoryTransactionData("T-005", "P-005", "Cylinder 7kg", "San Juan", "Out", 20, "POS", "01/01/2026", "U-003")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "Inventory",
            subtitle = "Operations Management",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // --- Stats Dashboard Row ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InventoryStatCard("Out of Stock Items", inventoryItems.count { it.status == "Out of Stock" }.toString(), Color(0xFF991B1B), modifier = Modifier.weight(1f))
                    InventoryStatCard("Critical Items", inventoryItems.count { it.status == "Critical" }.toString(), GasTrackRed, modifier = Modifier.weight(1f))
                    InventoryStatCard("Low Stock Items", inventoryItems.count { it.status == "Low Stock" }.toString(), Color(0xFFFFA000), modifier = Modifier.weight(1f))
                    InventoryStatCard("Normal Items", inventoryItems.count { it.status == "Normal" }.toString(), Color(0xFF2E7D32), modifier = Modifier.weight(1f))
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EmployeeTabSelector(
                        tabs = listOf("Inventory", "Inventory Transactions"),
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }

            if (selectedTab == "Inventory") {
                item {
                    // --- Quick Action Buttons (Blue Theme) ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InventoryQuickAction("CSV", Icons.Default.FileDownload, modifier = Modifier.weight(1f)) { /* Download Template */ }
                        InventoryQuickAction("Import", Icons.Default.FileUpload, modifier = Modifier.weight(1f)) { /* Handle Import */ }
                        InventoryQuickAction("Stock In", Icons.Default.Add, modifier = Modifier.weight(1f)) { showStockInForm = true }
                        InventoryQuickAction("Stock Out", Icons.Default.Add, modifier = Modifier.weight(1f)) { showStockOutForm = true }
                        InventoryQuickAction("Transfer", Icons.Default.Add, modifier = Modifier.weight(1f)) { showAdjustmentForm = true }
                    }
                }

                item {
                    // --- Filter Controls ---
                    InventoryFiltersSection()
                }

                items(inventoryItems) { item ->
                    InventoryCardMobile(
                        item = item,
                        onView = { selectedItemForDetail = it },
                        onEdit = { selectedItemForEdit = it },
                        onDelete = {
                            itemToDelete = it
                            showDeleteConfirm = true
                        }
                    )
                }
            } else {
                item {
                    // --- Filter Controls for Transactions ---
                    TransactionsFiltersSection()
                }
                
                items(transactions) { trx ->
                    TransactionCardMobile(trx)
                }
            }
        }
    }

    // Modal Forms implementation
    if (showStockInForm) {
        StockInDialog(onDismiss = { showStockInForm = false }) {
            showStockInForm = false
            successMessage = "Stock In successfully processed."
            showSuccessDialog = true
        }
    }

    if (showStockOutForm) {
        StockOutDialog(onDismiss = { showStockOutForm = false }) {
            showStockOutForm = false
            successMessage = "Stock Out successfully processed."
            showSuccessDialog = true
        }
    }
    
    if (showAdjustmentForm) {
        // Shared logic with other forms or specific adjustment UI
        showAdjustmentForm = false
        successMessage = "Stock Adjustment applied successfully."
        showSuccessDialog = true
    }

    if (selectedItemForDetail != null) {
        InventoryDetailDialog(item = selectedItemForDetail!!, onDismiss = { selectedItemForDetail = null })
    }

    if (selectedItemForEdit != null) {
        InventoryEditDialog(
            item = selectedItemForEdit!!, 
            onDismiss = { selectedItemForEdit = null },
            onSave = { updated ->
                val index = inventoryItems.indexOfFirst { it.id == updated.id }
                if (index != -1) inventoryItems[index] = updated
                selectedItemForEdit = null
                successMessage = "Inventory record updated."
                showSuccessDialog = true
            }
        )
    }

    if (showDeleteConfirm && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Inventory Item", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to remove ${itemToDelete!!.productID} from ${itemToDelete!!.warehouseName}?") },
            confirmButton = {
                TextButton(onClick = {
                    inventoryItems.remove(itemToDelete)
                    showDeleteConfirm = false
                }) { Text("Delete", color = GasTrackRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (showSuccessDialog) {
        PortalSuccessDialog(message = successMessage, onDismiss = { showSuccessDialog = false })
    }
}

@Composable
fun InventoryQuickAction(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0082FB)),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
        modifier = modifier.height(42.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = text, fontSize = 8.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}

@Composable
fun InventoryFiltersSection() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = "", onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(52.dp),
            placeholder = { Text("Search Inventory by Product", color = Color.LightGray, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White, 
                focusedContainerColor = Color.White, 
                unfocusedBorderColor = Color(0xFFE9ECEF), 
                focusedBorderColor = GasTrackBlue
            ),
            singleLine = true
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown("All Warehouse", Modifier.weight(1f))
            FilterDropdown("Inventory Status", Modifier.weight(1f))
        }
    }
}

@Composable
fun TransactionsFiltersSection() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown("All Types", Modifier.weight(1f))
        }
    }
}

@Composable
fun FilterDropdown(text: String, modifier: Modifier) {
    Surface(
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium, maxLines = 1)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun InventoryCardMobile(
    item: InventoryItemData,
    onView: (InventoryItemData) -> Unit,
    onEdit: (InventoryItemData) -> Unit,
    onDelete: (InventoryItemData) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Inv ID: ${item.id}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextDark)
                Surface(
                    color = when(item.status) {
                        "Critical" -> GasTrackRed.copy(alpha = 0.1f)
                        "Low Stock" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E9)
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, when(item.status) {
                        "Critical" -> GasTrackRed
                        "Low Stock" -> Color(0xFFFFA000)
                        else -> Color(0xFF2E7D32)
                    })
                ) {
                    Text(
                        text = item.status, 
                        color = when(item.status) {
                            "Critical" -> GasTrackRed
                            "Low Stock" -> Color(0xFFFFA000)
                            else -> Color(0xFF2E7D32)
                        }, 
                        fontSize = 9.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Product ID", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = item.productID, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Warehouse", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = item.warehouseName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Filled / Limit", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = "${item.stockOnHand} / ${item.reorderLimit}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { onView(item) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Visibility, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(18.dp)) }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp)) }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(16.dp)) }
            }
        }
    }
}

@Composable
fun InventoryDetailDialog(item: InventoryItemData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), 
            shape = RoundedCornerShape(16.dp), 
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Inventory Item Details", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                
                DetailValueRow("Inventory ID", item.id)
                DetailValueRow("Product ID", item.productID, isBold = true)
                DetailValueRow("Product Name", item.productName)
                DetailValueRow("Warehouse", item.warehouseName, isBold = true)
                DetailValueRow("Filled / Sellable Stock", item.stockOnHand.toString(), isBold = true, color = StaffPortalButtonBlue)
                if (item.isTank) {
                    DetailValueRow("Empty Tanks", item.emptyStock.toString(), isBold = true, color = TextGray)
                }
                DetailValueRow("Reorder Limit", item.reorderLimit.toString(), color = GasTrackRed)
                DetailValueRow("Current Status", item.status, color = if(item.status == "Normal") Color(0xFF2E7D32) else GasTrackRed)
                
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss, 
                    modifier = Modifier.fillMaxWidth().height(48.dp), 
                    shape = RoundedCornerShape(10.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close Overview", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun InventoryEditDialog(item: InventoryItemData, onDismiss: () -> Unit, onSave: (InventoryItemData) -> Unit) {
    var stock by remember { mutableStateOf(item.stockOnHand.toString()) }
    var limit by remember { mutableStateOf(item.reorderLimit.toString()) }
    var status by remember { mutableStateOf(item.status) }
    var statusExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(), 
            shape = RoundedCornerShape(16.dp), 
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Edit Inventory Item", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                var action by remember { mutableStateOf("filled") }
                
                if (item.isTank) {
                    Text("STOCK ACTION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    var actionExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { actionExpanded = true }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), 
                            shape = RoundedCornerShape(12.dp), 
                            border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    when(action) {
                                        "filled" -> "Edit Filled Stock"
                                        "set-empty" -> "Edit Empty Stock"
                                        "refill" -> "Refill Empty Tanks"
                                        else -> ""
                                    }, 
                                    color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                            }
                        }
                        DropdownMenu(expanded = actionExpanded, onDismissRequest = { actionExpanded = false }) {
                            DropdownMenuItem(text = { Text("Edit Filled Stock", fontSize = 14.sp) }, onClick = { action = "filled"; stock = item.stockOnHand.toString(); actionExpanded = false })
                            DropdownMenuItem(text = { Text("Edit Empty Stock", fontSize = 14.sp) }, onClick = { action = "set-empty"; stock = item.emptyStock.toString(); actionExpanded = false })
                            if (item.emptyStock > 0) {
                                DropdownMenuItem(text = { Text("Refill Empty Tanks", fontSize = 14.sp) }, onClick = { action = "refill"; stock = ""; actionExpanded = false })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val label = when(action) {
                    "refill" -> "TANKS TO REFILL"
                    "set-empty" -> "COUNTED EMPTY TANKS"
                    else -> "NEW FILLED / SELLABLE STOCK QUANTITY"
                }

                EmployeeInputField(label = label, value = stock, onValueChange = { if(it.all { char -> char.isDigit() }) stock = it }, placeholder = "0", icon = Icons.Default.Inventory)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (action != "set-empty" && action != "refill") {
                    EmployeeInputField(label = "REORDER LIMIT", value = limit, onValueChange = { if(it.all { char -> char.isDigit() }) limit = it }, placeholder = "0", icon = Icons.Default.NotificationsActive)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("STATUS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedButton(
                            onClick = { statusExpanded = true }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), 
                            shape = RoundedCornerShape(12.dp), 
                            border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(status, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                            }
                        }
                        DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                            listOf("Normal", "Critical", "Low Stock").forEach { s ->
                                DropdownMenuItem(text = { Text(s, fontSize = 14.sp) }, onClick = { status = s; statusExpanded = false })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss, 
                        modifier = Modifier.weight(1f).height(44.dp), 
                        shape = RoundedCornerShape(10.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)
                    ) { 
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold) 
                    }
                    Button(
                        onClick = { onSave(item.copy(stockOnHand = stock.toIntOrNull() ?: 0, reorderLimit = limit.toIntOrNull() ?: 0, status = status)) },
                        modifier = Modifier.weight(1f).height(44.dp), 
                        shape = RoundedCornerShape(10.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) { 
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}


@Composable
fun TransactionCardMobile(trx: InventoryTransactionData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = trx.transactionId, fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextDark)
                Surface(
                    color = when(trx.transactionType) {
                        "In" -> Color(0xFFE8F5E9)
                        "Out" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = trx.transactionType.uppercase(), 
                        color = when(trx.transactionType) {
                            "In" -> Color(0xFF2E7D32)
                            "Out" -> GasTrackRed
                            else -> StaffPortalButtonBlue
                        }, 
                        fontSize = 9.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = trx.productName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = "PID: ${trx.productId} • WH: ${trx.warehouseName}", fontSize = 11.sp, color = TextGray)
            
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text(text = "Qty: ${trx.quantity} • ${trx.reference}", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                    Text(text = "By: ${trx.processedBy}", fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
                }
                Text(text = trx.transactionDate, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StockInDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), shape = RoundedCornerShape(12.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add Stock In", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StockFormField("Stock In ID", "SI-001")
                    
                    var supplierId by remember { mutableStateOf("Select Supplier") }
                    var showSupplierDropdown by remember { mutableStateOf(false) }
                    Box {
                        StockFormField("Supplier", supplierId, isDropdown = true, onClick = { showSupplierDropdown = true })
                        DropdownMenu(expanded = showSupplierDropdown, onDismissRequest = { showSupplierDropdown = false }, modifier = Modifier.fillMaxWidth(0.6f).background(Color.White)) {
                            listOf("S-001 (Petron)", "S-002 (Shell)", "S-003 (Phoenix)").forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { supplierId = s; showSupplierDropdown = false })
                            }
                        }
                    }
                    
                    StockFormField("Invoice No", "INV-001")
                    StockFormField("Received by", "User Name", isDropdown = true)
                    StockFormField("Date", "01/01/2026 2:14:05 PM")
                    StockFormField("Status", "Pending", isDropdown = true)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Items", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)) {
                            Text("Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // --- Stock In Table ---
                    Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        Column(modifier = Modifier.width(500.dp).border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(4.dp))) {
                            // Header
                            Row(modifier = Modifier.fillMaxWidth().background(StaffPortalBlue).padding(vertical = 8.dp, horizontal = 4.dp)) {
                                TableText("#", Modifier.width(30.dp), isHeader = true)
                                TableText("Product ID", Modifier.weight(1f), isHeader = true)
                                TableText("Product Name", Modifier.weight(2f), isHeader = true)
                                TableText("Qty", Modifier.weight(0.8f), isHeader = true)
                                TableText("Cost Price", Modifier.weight(1.2f), isHeader = true)
                                TableText("Expiry Date", Modifier.weight(1.2f), isHeader = true)
                            }
                            // Rows
                            val mockItems = listOf(
                                listOf("1", "P-001", "Gasul LPG 2.7 kg", "20", "₱ 4,860.00", "01/01/2027"),
                                listOf("2", "P-003", "Gasul LPG 11kg", "15", "₱ 13,815.00", "01/01/2027"),
                                listOf("3", "P-005", "Cylinder 7kg", "15", "₱ 27,000.00", "01/01/2027")
                            )
                            mockItems.forEachIndexed { index, row ->
                                Row(modifier = Modifier.fillMaxWidth().background(if(index % 2 == 0) Color.White else Color(0xFFF8F9FA)).padding(vertical = 8.dp, horizontal = 4.dp)) {
                                    TableText(row[0], Modifier.width(30.dp))
                                    TableText(row[1], Modifier.weight(1f))
                                    TableText(row[2], Modifier.weight(2f))
                                    TableText(row[3], Modifier.weight(0.8f))
                                    TableText(row[4], Modifier.weight(1.2f))
                                    TableText(row[5], Modifier.weight(1.2f))
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Total Items: 50", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Text(text = "Estimated Cost: ₱ 45,675.00", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)) { Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                    Button(onClick = {}, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)) { Text("Save Draft", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                    Button(onClick = onConfirm, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)) { Text("Received", fontSize = 14.sp, fontWeight = FontWeight.Black) }
                }
            }
        }
    }
}

@Composable
fun StockOutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), shape = RoundedCornerShape(12.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add Stock Out", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StockFormField("Stock Out ID", "SO-001")
                    
                    var outType by remember { mutableStateOf("Supplier Return") }
                    var showTypeDropdown by remember { mutableStateOf(false) }
                    Box {
                        StockFormField("Stock Out Type", outType, isDropdown = true, onClick = { showTypeDropdown = true })
                        DropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }, modifier = Modifier.fillMaxWidth(0.6f).background(Color.White)) {
                            listOf("Supplier Return", "Damaged", "Expired", "Internal Use").forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = { outType = type; showTypeDropdown = false })
                            }
                        }
                    }
                    
                    StockFormField("Reference ID", "REF-001")
                    
                    var reason by remember { mutableStateOf("Damaged") }
                    var showReasonDropdown by remember { mutableStateOf(false) }
                    Box {
                        StockFormField("Reason", reason, isDropdown = true, onClick = { showReasonDropdown = true })
                        DropdownMenu(expanded = showReasonDropdown, onDismissRequest = { showReasonDropdown = false }, modifier = Modifier.fillMaxWidth(0.6f).background(Color.White)) {
                            listOf("Damaged", "Defective", "Expired", "Other").forEach { r ->
                                DropdownMenuItem(text = { Text(r) }, onClick = { reason = r; showReasonDropdown = false })
                            }
                        }
                    }
                    
                    StockFormField("Processed by", "User Name", isDropdown = true)
                    StockFormField("Date", "01/01/2026 2:14:05 PM")
                    StockFormField("Status", "Pending", isDropdown = true)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Items", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)) {
                            Text("Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // --- Stock Out Table ---
                    Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        Column(modifier = Modifier.width(500.dp).border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(4.dp))) {
                            // Header
                            Row(modifier = Modifier.fillMaxWidth().background(StaffPortalBlue).padding(vertical = 8.dp, horizontal = 4.dp)) {
                                TableText("#", Modifier.width(30.dp), isHeader = true)
                                TableText("Product ID", Modifier.weight(1f), isHeader = true)
                                TableText("Product Name", Modifier.weight(2f), isHeader = true)
                                TableText("Qty", Modifier.weight(0.8f), isHeader = true)
                                TableText("Condition", Modifier.weight(1.2f), isHeader = true)
                                TableText("Action", Modifier.weight(1.2f), isHeader = true)
                            }
                            // Rows
                            val mockItems = listOf(
                                listOf("1", "P-001", "Gasul LPG 2.7 kg", "20", "Damaged", "Send Back"),
                                listOf("2", "P-003", "Gasul LPG 11kg", "15", "Damaged", "Send Back"),
                                listOf("3", "P-005", "Cylinder 7kg", "15", "Damaged", "Send Back")
                            )
                            mockItems.forEachIndexed { index, row ->
                                Row(modifier = Modifier.fillMaxWidth().background(if(index % 2 == 0) Color.White else Color(0xFFF8F9FA)).padding(vertical = 8.dp, horizontal = 4.dp)) {
                                    TableText(row[0], Modifier.width(30.dp))
                                    TableText(row[1], Modifier.weight(1f))
                                    TableText(row[2], Modifier.weight(2f))
                                    TableText(row[3], Modifier.weight(0.8f))
                                    TableText(row[4], Modifier.weight(1.2f))
                                    TableText(row[5], Modifier.weight(1.2f))
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Total Stock Out Items: 50", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Text(text = "Stock Deduction: 50", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Text(text = "Note: Items will be returned to supplier warehouse", fontSize = 12.sp, color = TextGray, fontStyle = FontStyle.Italic)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)) { Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                    Button(onClick = {}, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)) { Text("Save Draft", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                    Button(onClick = onConfirm, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)) { Text("Approved", fontSize = 14.sp, fontWeight = FontWeight.Black) }
                }
            }
        }
    }
}

@Composable
fun TableText(text: String, modifier: Modifier = Modifier, isHeader: Boolean = false) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 2.dp),
        fontSize = if(isHeader) 10.sp else 11.sp,
        fontWeight = if(isHeader) FontWeight.Black else FontWeight.Bold,
        color = if(isHeader) Color.White else TextDark,
        textAlign = TextAlign.Start,
        maxLines = 1
    )
}


@Preview(showBackground = true)
@Composable
fun InventoryEmployeeScreenPreview() {
    InventoryEmployeeScreen()
}
