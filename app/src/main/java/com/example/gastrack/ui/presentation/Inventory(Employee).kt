package com.example.gastrack.ui.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

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

    // Inventory Filter States
    var searchQuery by remember { mutableStateOf("") }
    var selectedWarehouse by remember { mutableStateOf("All Warehouse") }
    var selectedStatus by remember { mutableStateOf("Inventory Status") }

    // Transactions Filter States
    var selectedTrxType by remember { mutableStateOf("All Types") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground)
    ) {
        EmployeePortalHeader(
            title = "Inventory",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // --- Stats Dashboard 2x2 Grid ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InventoryStatCard(
                            label = "Out of Stock",
                            value = inventoryItems.count { it.status == "Out of Stock" }.toString(),
                            color = StatusInactive,
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.RemoveShoppingCart,
                            sub = "Items"
                        )
                        InventoryStatCard(
                            label = "Critical Items",
                            value = inventoryItems.count { it.status == "Critical" }.toString(),
                            color = StatusInactive,
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Warning,
                            sub = "Action needed"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InventoryStatCard(
                            label = "Low Stock",
                            value = inventoryItems.count { it.status == "Low Stock" }.toString(),
                            color = Color(0xFFFFA000),
                            modifier = Modifier.weight(1f),
                            icon = Icons.AutoMirrored.Filled.TrendingDown,
                            sub = "Reorder limit"
                        )
                        InventoryStatCard(
                            label = "Normal Items",
                            value = inventoryItems.count { it.status == "Normal" }.toString(),
                            color = StatusActive,
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CheckCircle,
                            sub = "In stock"
                        )
                    }
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
                        InventoryQuickAction("Stock Out", Icons.Default.Remove, modifier = Modifier.weight(1f)) { showStockOutForm = true }
                        InventoryQuickAction("Transfer", Icons.Default.SwapHoriz, modifier = Modifier.weight(1f)) { showAdjustmentForm = true }
                    }
                }
                
                val filteredInventory = inventoryItems.filter {
                    val matchesSearch = it.productName.contains(searchQuery, ignoreCase = true) || 
                                        it.productID.contains(searchQuery, ignoreCase = true)
                    val matchesWarehouse = selectedWarehouse == "All Warehouse" || it.warehouseName == selectedWarehouse
                    val matchesStatus = selectedStatus == "Inventory Status" || it.status == selectedStatus
                    matchesSearch && matchesWarehouse && matchesStatus
                }

                item {
                    // --- Filter Controls ---
                    InventoryFiltersSection(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedWarehouse = selectedWarehouse,
                        onWarehouseChange = { selectedWarehouse = it },
                        selectedStatus = selectedStatus,
                        onStatusChange = { selectedStatus = it }
                    )
                }

                items(filteredInventory) { item ->
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
                val filteredTransactions = transactions.filter {
                    selectedTrxType == "All Types" || it.transactionType == selectedTrxType
                }

                item {
                    // --- Filter Controls for Transactions ---
                    TransactionsFiltersSection(
                        selectedType = selectedTrxType,
                        onTypeChange = { selectedTrxType = it }
                    )
                }
                
                items(filteredTransactions) { trx ->
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
        TransferDialog(onDismiss = { showAdjustmentForm = false }) {
            showAdjustmentForm = false
            successMessage = "Stock Transfer processed successfully."
            showSuccessDialog = true
        }
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
        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
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
fun InventoryFiltersSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedWarehouse: String,
    onWarehouseChange: (String) -> Unit,
    selectedStatus: String,
    onStatusChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        EmployeeSearchBar(
            query = searchQuery,
            onQueryChange = onSearchChange,
            placeholder = "Search Inventory by Product"
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown(
                options = listOf("All Warehouse", "Pasig", "San Juan"),
                selectedOption = selectedWarehouse,
                onOptionSelected = onWarehouseChange,
                modifier = Modifier.weight(1f)
            )
            FilterDropdown(
                options = listOf("Inventory Status", "Normal", "Low Stock", "Critical"),
                selectedOption = selectedStatus,
                onOptionSelected = onStatusChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TransactionsFiltersSection(
    selectedType: String,
    onTypeChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown(
                options = listOf("All Types", "In", "Out", "Transfer", "Adjust"),
                selectedOption = selectedType,
                onOptionSelected = onTypeChange,
                modifier = Modifier.weight(1f)
            )
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
                        "Critical" -> StatusInactive.copy(alpha = 0.1f)
                        "Low Stock" -> Color(0xFFFFF3E0)
                        else -> StatusActive.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, when(item.status) {
                        "Critical" -> StatusInactive
                        "Low Stock" -> Color(0xFFFFA000)
                        else -> StatusActive
                    })
                ) {
                    Text(
                        text = item.status, 
                        color = when(item.status) {
                            "Critical" -> StatusInactive
                            "Low Stock" -> Color(0xFFFFA000)
                            else -> StatusActive
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
            modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(), 
            shape = RoundedCornerShape(12.dp), 
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Inventory Details", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF111827))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                @Composable
                fun DetailRow(label: String, value: String) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(text = "$label: ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                        Text(text = value, fontSize = 13.sp, color = Color(0xFF4B5563))
                    }
                }
                
                DetailRow("Product ID", item.productID)
                DetailRow("Product Name", item.productName)
                DetailRow("Warehouse", item.warehouseName)
                DetailRow("Current Stock", item.stockOnHand.toString())
                if (item.isTank) {
                    DetailRow("Empty Tanks", item.emptyStock.toString())
                }
                DetailRow("Reorder Limit", item.reorderLimit.toString())
                DetailRow("Status", item.status)
                DetailRow("Last Updated", "10/9/2026, 12:02:40 PM") // Mocked as per screenshot
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss, 
                    modifier = Modifier.fillMaxWidth().height(44.dp), 
                    shape = RoundedCornerShape(8.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF111827))
                ) {
                    Text("Close", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InventoryEditDialog(item: InventoryItemData, onDismiss: () -> Unit, onSave: (InventoryItemData) -> Unit) {
    var stock by remember { mutableStateOf(item.stockOnHand.toString()) }
    var remarks by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight(), 
            shape = RoundedCornerShape(12.dp), 
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Edit Stock", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF111827))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${item.productName} — ${item.warehouseName}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("New Stock Quantity", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = stock,
                    onValueChange = { if (it.all { char -> char.isDigit() }) stock = it },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF4B5563)),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text("Remarks (optional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    placeholder = { Text("e.g. Physical count correction", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color(0xFF4B5563)),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(28.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss, 
                        modifier = Modifier.weight(1f).height(44.dp), 
                        shape = RoundedCornerShape(8.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF111827))
                    ) { 
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold) 
                    }
                    Button(
                        onClick = { 
                            // Note: Remarks is not currently part of InventoryItemData, 
                            // but is available for backend submission in a real app.
                            onSave(item.copy(stockOnHand = stock.toIntOrNull() ?: 0)) 
                        },
                        modifier = Modifier.weight(1f).height(44.dp), 
                        shape = RoundedCornerShape(8.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue, contentColor = Color.White)
                    ) { 
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
            // Header Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Trx ID: ${trx.transactionId}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextDark)
                
                val typeLabel = when (trx.transactionType) {
                    "In" -> "Stock In"
                    "Out" -> "Stock Out"
                    else -> trx.transactionType
                }
                
                Surface(
                    color = when(trx.transactionType) {
                        "In" -> StatusActive.copy(alpha = 0.1f)
                        "Out" -> StatusInactive.copy(alpha = 0.1f)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, when(trx.transactionType) {
                        "In" -> StatusActive
                        "Out" -> StatusInactive
                        else -> StaffPortalButtonBlue
                    })
                ) {
                    Text(
                        text = typeLabel, 
                        color = when(trx.transactionType) {
                            "In" -> StatusActive
                            "Out" -> StatusInactive
                            else -> StaffPortalButtonBlue
                        }, 
                        fontSize = 9.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Product & Warehouse Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Product", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = "${trx.productId} • ${trx.productName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Warehouse", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = trx.warehouseName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(10.dp))
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Quantity", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = trx.quantity.toString(), fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Reference", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = trx.reference, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Date", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text(text = trx.transactionDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(text = "By: ${trx.processedBy}", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var stockType by remember { mutableStateOf("filled") }
    var warehouseId by remember { mutableStateOf("") }
    var referenceNo by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Supplier Delivery") }
    var otherReason by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    
    data class ItemRow(var productId: String = "", var quantity: String = "")
    val items = remember { mutableStateListOf(ItemRow()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val stockInReasons = listOf("Supplier Delivery", "Customer Return", "Inventory Adjustments", "Other")
    val warehouses = listOf("Pasig", "San Juan", "Main Warehouse")
    
    data class Product(val id: String, val name: String, val costPrice: Double, val isTank: Boolean)
    val products = listOf(
        Product("P-001", "Gasul LPG 2.7 kg", 4860.0, true),
        Product("P-002", "Gasul LPG 7 kg", 12500.0, true),
        Product("P-003", "LPG Hose Clamp", 150.0, false),
        Product("P-004", "Gasul LPG 11 kg", 13815.0, true),
        Product("P-005", "POL Regulator", 350.0, false)
    )
    val productMap = products.associateBy { it.id }

    val validItems = items.filter { it.productId.isNotBlank() && (it.quantity.toIntOrNull() ?: 0) > 0 }
    val totalItems = validItems.sumOf { it.quantity.toIntOrNull() ?: 0 }
    val estimatedCost = validItems.sumOf { 
        val p = productMap[it.productId]
        (p?.costPrice ?: 0.0) * (it.quantity.toIntOrNull() ?: 0)
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(28.dp)) {
                Text("Add Stock In", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                Spacer(modifier = Modifier.height(20.dp))
                
                if (error.isNotBlank()) {
                    Text(error, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    @Composable
                    fun FormRow(label: String, content: @Composable () -> Unit) {
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) { content() }
                        }
                    }

                    FormRow("Stock Type") {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = if (stockType == "filled") "Filled / Sellable Stock" else "Empty Tanks",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("Filled / Sellable Stock") }, onClick = { stockType = "filled"; items.clear(); items.add(ItemRow()); expanded = false })
                                DropdownMenuItem(text = { Text("Empty Tanks") }, onClick = { stockType = "empty"; items.clear(); items.add(ItemRow()); expanded = false })
                            }
                        }
                    }

                    FormRow("Warehouse") {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = warehouseId.ifBlank { "Select warehouse" },
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                warehouses.forEach { w ->
                                    DropdownMenuItem(text = { Text(w) }, onClick = { warehouseId = w; expanded = false })
                                }
                            }
                        }
                    }

                    FormRow("Reference ID") {
                        OutlinedTextField(
                            value = referenceNo,
                            onValueChange = { referenceNo = it },
                            placeholder = { Text("Reference ID") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                        )
                    }

                    FormRow("Reason") {
                        Column {
                            stockInReasons.forEach { r ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp).clickable { reason = r }) {
                                    RadioButton(selected = (reason == r), onClick = { reason = r }, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(r, fontSize = 14.sp, color = Color(0xFF374151))
                                }
                            }
                            if (reason == "Other") {
                                OutlinedTextField(
                                    value = otherReason,
                                    onValueChange = { otherReason = it },
                                    placeholder = { Text("Describe the reason or conditions...") },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                                )
                            }
                        }
                    }

                    FormRow("Remarks") {
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            placeholder = { Text("Optional notes") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                        )
                    }

                    // Items Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Items", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                        Button(
                            onClick = { items.add(ItemRow()) },
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Add Item", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(bottom = 12.dp))

                    // Items Table using Cards for Mobile
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEachIndexed { index, item ->
                            val product = productMap[item.productId]
                            val cost = product?.costPrice ?: 0.0
                            val qty = item.quantity.toIntOrNull() ?: 0
                            val subtotal = cost * qty

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Item ${index + 1}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextDark
                                        )
                                        IconButton(
                                            onClick = {
                                                val newList = items.toMutableList()
                                                newList.removeAt(index)
                                                items.clear()
                                                items.addAll(newList)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(text = "Product", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Box {
                                                var expanded by remember { mutableStateOf(false) }
                                                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                                                    OutlinedTextField(
                                                        value = productMap[item.productId]?.name ?: "Select product",
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                                        modifier = Modifier.fillMaxWidth().height(48.dp).menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                                        shape = RoundedCornerShape(8.dp),
                                                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark),
                                                        colors = OutlinedTextFieldDefaults.colors(
                                                            unfocusedBorderColor = Color(0xFFE9ECEF),
                                                            unfocusedContainerColor = Color.White,
                                                            focusedContainerColor = Color.White
                                                        )
                                                    )
                                                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                                        products.filter { stockType != "empty" || it.isTank }.forEach { p ->
                                                            DropdownMenuItem(text = { Text(p.name, fontSize = 13.sp) }, onClick = {
                                                                val newList = items.toMutableList()
                                                                newList[index] = item.copy(productId = p.id)
                                                                items.clear()
                                                                items.addAll(newList)
                                                                expanded = false
                                                            })
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Column(modifier = Modifier.width(70.dp)) {
                                            Text(text = "Qty", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            OutlinedTextField(
                                                value = item.quantity,
                                                onValueChange = { q: String ->
                                                    if (q.all { it.isDigit() }) {
                                                        val newList = items.toMutableList()
                                                        newList[index] = item.copy(quantity = q)
                                                        items.clear()
                                                        items.addAll(newList)
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue, textAlign = TextAlign.Center),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedBorderColor = Color(0xFFE9ECEF),
                                                    unfocusedContainerColor = Color.White,
                                                    focusedContainerColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFFF1F3F4))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text(text = "Cost Price", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Text(text = if(product != null) "₱ ${String.format(Locale.US, "%.2f", cost)}" else "—", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = "Subtotal", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Text(text = "₱ ${String.format(Locale.US, "%.2f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Total Items: $totalItems", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Estimated Cost: ₱ ${String.format(Locale.US, "%.2f", estimatedCost)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                    Spacer(modifier = Modifier.height(28.dp))
                }
                
                // Modal Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss, 
                        modifier = Modifier.weight(1f).height(48.dp), 
                        shape = RoundedCornerShape(6.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF111827))
                    ) { Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    
                    Button(
                        onClick = {
                            if (warehouseId.isBlank()) { error = "Please select a warehouse."; return@Button }
                            if (referenceNo.isBlank()) { error = "Reference ID is required."; return@Button }
                            if (reason == "Other" && otherReason.isBlank()) { error = "Please provide details when Other reason is selected."; return@Button }
                            if (validItems.isEmpty()) { error = "Add at least one item with a product and quantity."; return@Button }
                            
                            isSubmitting = true
                            error = ""
                            onConfirm()
                        }, 
                        modifier = Modifier.weight(1f).height(48.dp), 
                        shape = RoundedCornerShape(6.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue, contentColor = Color.White),
                        enabled = !isSubmitting
                    ) { Text(if (isSubmitting) "Saving…" else "Received", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockOutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var stockType by remember { mutableStateOf("filled") }
    var warehouseId by remember { mutableStateOf("") }
    var referenceNo by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Damaged Product") }
    var otherReason by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    
    data class ItemRow(var productId: String = "", var quantity: String = "")
    val items = remember { mutableStateListOf(ItemRow()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val stockOutReasons = listOf("Damaged Product", "Defective Cylinder", "Return to Supplier", "Expired/Unusable", "Inventory Adjustment", "Other")
    val warehouses = listOf("Pasig", "San Juan", "Main Warehouse")
    
    data class Product(val id: String, val name: String, val availableStock: Int, val isTank: Boolean)
    val products = listOf(
        Product("P-001", "Gasul LPG 2.7 kg", 50, true),
        Product("P-002", "Gasul LPG 7 kg", 100, true),
        Product("P-003", "LPG Hose Clamp", 20, false),
        Product("P-004", "Gasul LPG 11 kg", 34, true),
        Product("P-005", "POL Regulator", 60, false)
    )
    val productMap = products.associateBy { it.id }

    val validItems = items.filter { it.productId.isNotBlank() && (it.quantity.toIntOrNull() ?: 0) > 0 }
    val totalQty = validItems.sumOf { it.quantity.toIntOrNull() ?: 0 }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(28.dp)) {
                Text("Add Stock Out", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                Spacer(modifier = Modifier.height(20.dp))
                
                if (error.isNotBlank()) {
                    Text(error, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    @Composable
                    fun FormRow(label: String, content: @Composable () -> Unit) {
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) { content() }
                        }
                    }

                    FormRow("Stock Type") {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = if (stockType == "filled") "Filled / Sellable Stock" else "Empty Tanks",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("Filled / Sellable Stock") }, onClick = { stockType = "filled"; items.clear(); items.add(ItemRow()); expanded = false })
                                DropdownMenuItem(text = { Text("Empty Tanks") }, onClick = { stockType = "empty"; items.clear(); items.add(ItemRow()); expanded = false })
                            }
                        }
                    }

                    FormRow("Warehouse") {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = warehouseId.ifBlank { "Select warehouse" },
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                warehouses.forEach { w ->
                                    DropdownMenuItem(text = { Text(w) }, onClick = { warehouseId = w; expanded = false })
                                }
                            }
                        }
                    }

                    FormRow("Reference ID") {
                        OutlinedTextField(
                            value = referenceNo,
                            onValueChange = { referenceNo = it },
                            placeholder = { Text("Reference ID") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                        )
                    }

                    FormRow("Reason") {
                        Column {
                            stockOutReasons.forEach { r ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp).clickable { reason = r }) {
                                    RadioButton(selected = (reason == r), onClick = { reason = r }, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(r, fontSize = 14.sp, color = Color(0xFF374151))
                                }
                            }
                            if (reason == "Other") {
                                OutlinedTextField(
                                    value = otherReason,
                                    onValueChange = { otherReason = it },
                                    placeholder = { Text("Describe the reason or conditions...") },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                                )
                            }
                        }
                    }

                    FormRow("Remarks") {
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            placeholder = { Text("Optional notes") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                        )
                    }

                    // Items Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Items", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                        Button(
                            onClick = { items.add(ItemRow()) },
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Add Item", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(bottom = 12.dp))

                    // Items Table using Cards for Mobile
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEachIndexed { index, item ->
                            val product = productMap[item.productId]
                            val availableStock = product?.availableStock ?: 0

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Item ${index + 1}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextDark
                                        )
                                        IconButton(
                                            onClick = {
                                                val newList = items.toMutableList()
                                                newList.removeAt(index)
                                                items.clear()
                                                items.addAll(newList)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(text = "Product", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Box {
                                                var expanded by remember { mutableStateOf(false) }
                                                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                                                    OutlinedTextField(
                                                        value = product?.name ?: "Select product",
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                                        modifier = Modifier.fillMaxWidth().height(48.dp).menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                                        shape = RoundedCornerShape(8.dp),
                                                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark),
                                                        colors = OutlinedTextFieldDefaults.colors(
                                                            unfocusedBorderColor = Color(0xFFE9ECEF),
                                                            unfocusedContainerColor = Color.White,
                                                            focusedContainerColor = Color.White
                                                        )
                                                    )
                                                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                                        products.filter { stockType != "empty" || it.isTank }.forEach { p ->
                                                            DropdownMenuItem(text = { Text(p.name, fontSize = 13.sp) }, onClick = {
                                                                val newList = items.toMutableList()
                                                                newList[index] = item.copy(productId = p.id)
                                                                items.clear()
                                                                items.addAll(newList)
                                                                expanded = false
                                                            })
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Column(modifier = Modifier.width(70.dp)) {
                                            Text(text = "Qty", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            OutlinedTextField(
                                                value = item.quantity,
                                                onValueChange = { q: String ->
                                                    if (q.all { it.isDigit() }) {
                                                        val newList = items.toMutableList()
                                                        newList[index] = item.copy(quantity = q)
                                                        items.clear()
                                                        items.addAll(newList)
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue, textAlign = TextAlign.Center),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedBorderColor = Color(0xFFE9ECEF),
                                                    unfocusedContainerColor = Color.White,
                                                    focusedContainerColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFFF1F3F4))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text(text = "In Stock", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Text(text = "$availableStock", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Total Stock Out Items: $totalQty", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                    Spacer(modifier = Modifier.height(28.dp))
                }
                
                // Modal Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss, 
                        modifier = Modifier.weight(1f).height(48.dp), 
                        shape = RoundedCornerShape(6.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF111827))
                    ) { Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    
                    Button(
                        onClick = {
                            if (warehouseId.isBlank()) { error = "Please select a warehouse."; return@Button }
                            if (referenceNo.isBlank()) { error = "Reference ID is required."; return@Button }
                            if (reason == "Other" && otherReason.isBlank()) { error = "Please provide details when Other reason is selected."; return@Button }
                            if (validItems.isEmpty()) { error = "Add at least one item with a product and quantity."; return@Button }
                            
                            validItems.forEach { item ->
                                val p = productMap[item.productId]
                                val available = p?.availableStock ?: 0
                                if ((item.quantity.toIntOrNull() ?: 0) > available) {
                                    error = "Cannot remove ${item.quantity} of \"${p?.name ?: item.productId}\" — only $available in stock."
                                    return@Button
                                }
                            }
                            
                            isSubmitting = true
                            error = ""
                            onConfirm()
                        }, 
                        modifier = Modifier.weight(1f).height(48.dp), 
                        shape = RoundedCornerShape(6.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue, contentColor = Color.White),
                        enabled = !isSubmitting
                    ) { Text(if (isSubmitting) "Saving…" else "Approved", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var fromWarehouseId by remember { mutableStateOf("") }
    var toWarehouseId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Completed") }
    var remarks by remember { mutableStateOf("") }
    
    data class ItemRow(var productId: String = "", var quantity: String = "")
    val items = remember { mutableStateListOf(ItemRow()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val transferStatusList = listOf("Completed", "In Transit", "Pending")
    val warehouses = listOf("Pasig Warehouse", "San Juan Warehouse", "Main Warehouse")
    
    data class Product(val id: String, val name: String, val currentStock: Int, val isTank: Boolean)
    val products = listOf(
        Product("P-001", "Gasul LPG 2.7 kg", 50, true),
        Product("P-002", "Gasul LPG 7 kg", 100, true),
        Product("P-003", "LPG Hose Clamp", 20, false),
        Product("P-004", "Gasul LPG 11 kg", 34, true),
        Product("P-005", "POL Regulator", 60, false)
    )
    val productMap = products.associateBy { it.id }

    val validItems = items.filter { it.productId.isNotBlank() && (it.quantity.toIntOrNull() ?: 0) > 0 }
    val totalQty = validItems.sumOf { it.quantity.toIntOrNull() ?: 0 }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(28.dp)) {
                Text("Warehouse Transfer", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                Spacer(modifier = Modifier.height(20.dp))
                
                if (error.isNotBlank()) {
                    Text(error, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    @Composable
                    fun FormRow(label: String, content: @Composable () -> Unit) {
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) { content() }
                        }
                    }

                    FormRow("From Warehouse") {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = fromWarehouseId.ifBlank { "Select warehouse" },
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                warehouses.forEach { w ->
                                    DropdownMenuItem(text = { Text(w) }, onClick = { fromWarehouseId = w; expanded = false })
                                }
                            }
                        }
                    }

                    FormRow("To Warehouse") {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = toWarehouseId.ifBlank { "Select warehouse" },
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                warehouses.forEach { w ->
                                    DropdownMenuItem(text = { Text(w) }, onClick = { toWarehouseId = w; expanded = false })
                                }
                            }
                        }
                    }

                    FormRow("Status") {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = status,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                transferStatusList.forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { status = s; expanded = false })
                                }
                            }
                        }
                    }

                    FormRow("Remarks") {
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            placeholder = { Text("Optional notes") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFD1D5DB))
                        )
                    }

                    // Items Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Transfer Items", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                        Button(
                            onClick = { items.add(ItemRow()) },
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Add Item", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(bottom = 12.dp))

                    // Items Table using Cards for Mobile
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEachIndexed { index, item ->
                            val product = productMap[item.productId]
                            val availableStock = product?.currentStock ?: 0

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Item ${index + 1}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextDark
                                        )
                                        IconButton(
                                            onClick = {
                                                val newList = items.toMutableList()
                                                newList.removeAt(index)
                                                items.clear()
                                                items.addAll(newList)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(text = "Product", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Box {
                                                var expanded by remember { mutableStateOf(false) }
                                                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                                                    OutlinedTextField(
                                                        value = product?.name ?: "Select product",
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                                        modifier = Modifier.fillMaxWidth().height(48.dp).menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                                        shape = RoundedCornerShape(8.dp),
                                                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark),
                                                        colors = OutlinedTextFieldDefaults.colors(
                                                            unfocusedBorderColor = Color(0xFFE9ECEF),
                                                            unfocusedContainerColor = Color.White,
                                                            focusedContainerColor = Color.White
                                                        )
                                                    )
                                                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                                        products.forEach { p ->
                                                            DropdownMenuItem(text = { Text(p.name, fontSize = 13.sp) }, onClick = {
                                                                val newList = items.toMutableList()
                                                                newList[index] = item.copy(productId = p.id)
                                                                items.clear()
                                                                items.addAll(newList)
                                                                expanded = false
                                                            })
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Column(modifier = Modifier.width(70.dp)) {
                                            Text(text = "Qty", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            OutlinedTextField(
                                                value = item.quantity,
                                                onValueChange = { q: String ->
                                                    if (q.all { it.isDigit() }) {
                                                        val newList = items.toMutableList()
                                                        newList[index] = item.copy(quantity = q)
                                                        items.clear()
                                                        items.addAll(newList)
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue, textAlign = TextAlign.Center),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedBorderColor = Color(0xFFE9ECEF),
                                                    unfocusedContainerColor = Color.White,
                                                    focusedContainerColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFFF1F3F4))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text(text = "Available", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                            Text(text = "$availableStock", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Total Transfer Items: $totalQty", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                    Spacer(modifier = Modifier.height(28.dp))
                }
                
                // Modal Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss, 
                        modifier = Modifier.weight(1f).height(48.dp), 
                        shape = RoundedCornerShape(6.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF111827))
                    ) { Text("Close", fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) }
                    
                    Button(
                        onClick = {
                            if (fromWarehouseId.isBlank() || toWarehouseId.isBlank()) { error = "Please select source and destination warehouse."; return@Button }
                            if (fromWarehouseId == toWarehouseId) { error = "Source and destination warehouses must be different."; return@Button }
                            if (validItems.isEmpty()) { error = "Add at least one product with quantity."; return@Button }
                            
                            validItems.forEach { item ->
                                val p = productMap[item.productId]
                                val available = p?.currentStock ?: 0
                                if ((item.quantity.toIntOrNull() ?: 0) > available) {
                                    error = "Cannot transfer ${item.quantity} of \"${p?.name ?: item.productId}\" — only $available in stock."
                                    return@Button
                                }
                            }
                            
                            isSubmitting = true
                            error = ""
                            onConfirm()
                        }, 
                        modifier = Modifier.weight(1f).height(48.dp), 
                        shape = RoundedCornerShape(6.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue, contentColor = Color.White),
                        enabled = !isSubmitting
                    ) { Text(if (isSubmitting) "Saving…" else "Create Transfer", fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InventoryEmployeeScreenPreview() {
    InventoryEmployeeScreen()
}
