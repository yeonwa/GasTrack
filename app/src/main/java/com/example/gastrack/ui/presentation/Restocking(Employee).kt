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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

// --- Data Models ---

data class RestockRecommendationData(
    val restockId: String,
    val productId: String,
    val productName: String,
    val supplierId: String,
    val stockOnHand: Int,
    val predictedDemand: Int,
    val recommendedQty: Int,
    val forecastDate: String,
    val status: String // Pending, Approved, Rejected, Cancelled
)

data class PurchaseOrderData(
    val purchaseOrderId: String,
    val supplierId: String,
    val restockId: String,
    val createdByUserId: String,
    val poNo: String,
    val orderDate: String,
    val expectedDeliveryDate: String,
    val status: String, // Draft, Submitted, Received, Cancelled
    val totalAmount: Double,
    val remarks: String = ""
)

data class PurchaseOrderItemData(
    val id: String,
    val purchaseOrderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitCost: Double,
    val subtotal: Double
)

@Composable
fun RestockingEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Recommendations") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    
    var showPOForm by remember { mutableStateOf(false) }
    var currentPO by remember { mutableStateOf<PurchaseOrderData?>(null) }
    
    var showPODetail by remember { mutableStateOf(false) }
    var selectedPODetail by remember { mutableStateOf<PurchaseOrderData?>(null) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Mock Data
    val recommendations = remember { mutableStateListOf(
        RestockRecommendationData("REC-001", "P-001", "11kg Standard Refill", "S-001", 15, 120, 85, "2026-01-10", "Pending"),
        RestockRecommendationData("REC-002", "P-002", "2.7kg Camping Refill", "S-002", 8, 50, 42, "2026-01-12", "Approved"),
        RestockRecommendationData("REC-003", "P-003", "Gas Regulator (Auto)", "S-003", 5, 30, 25, "2026-01-15", "Pending")
    ) }

    val purchaseOrders = remember { mutableStateListOf(
        PurchaseOrderData("PO-001", "S-001 (Petron)", "REC-001", "U-001", "PO-2026-001", "01/01/2026", "01/05/2026", "Submitted", 72250.0)
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "Restocking",
            subtitle = "Supply Chain Intelligence",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // --- Dashboard Metrics ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InventoryStatCard("Critical Urgencies", recommendations.count { it.status == "Pending" }.toString(), GasTrackRed)
                    InventoryStatCard("Restocking Cost", "₱ " + String.format(Locale.US, "%,.0f", purchaseOrders.sumOf { it.totalAmount }), StaffPortalButtonBlue)
                    InventoryStatCard("Stockout Risk", if(recommendations.any { it.stockOnHand < 10 }) "High" else "Normal", Color(0xFFFFA000))
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EmployeeTabSelector(
                        tabs = listOf("Recommendations", "Purchase Orders"),
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }

            if (selectedTab == "Recommendations") {
                item {
                    RecommendationFilterHeader(
                        currentFilter = selectedStatusFilter,
                        onFilterChange = { selectedStatusFilter = it }
                    )
                }
                
                val filteredRecs = recommendations.filter {
                    if (selectedStatusFilter == "All") true else it.status == selectedStatusFilter
                }
                
                items(filteredRecs) { rec ->
                    RestockRecommendationCard(
                        rec = rec,
                        onApprove = {
                            val index = recommendations.indexOfFirst { it.restockId == rec.restockId }
                            if (index != -1) recommendations[index] = rec.copy(status = "Approved")
                        },
                        onReject = {
                            val index = recommendations.indexOfFirst { it.restockId == rec.restockId }
                            if (index != -1) recommendations[index] = rec.copy(status = "Rejected")
                        },
                        onCancel = {
                            val index = recommendations.indexOfFirst { it.restockId == rec.restockId }
                            if (index != -1) recommendations[index] = rec.copy(status = "Cancelled")
                        },
                        onCreatePO = {
                            currentPO = null 
                            showPOForm = true
                        }
                    )
                }
            } else {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(purchaseOrders) { po ->
                    PurchaseOrderCard(
                        po = po,
                        onView = { 
                            selectedPODetail = po
                            showPODetail = true
                        },
                        onEdit = {
                            currentPO = po
                            showPOForm = true
                        }
                    )
                }
                item {
                    Button(
                        onClick = { currentPO = null; showPOForm = true },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp).shadow(4.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Manual Purchase Order", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }

    if (showPOForm) {
        PurchaseOrderFormDialog(
            po = currentPO,
            onDismiss = { showPOForm = false },
            onSave = { updatedPO ->
                val index = purchaseOrders.indexOfFirst { it.purchaseOrderId == updatedPO.purchaseOrderId }
                if (index != -1) purchaseOrders[index] = updatedPO else purchaseOrders.add(updatedPO)
                showPOForm = false
                successMessage = "Purchase Order successfully submitted."
                showSuccessDialog = true
            }
        )
    }

    if (showPODetail && selectedPODetail != null) {
        PurchaseOrderDetailDialog(po = selectedPODetail!!, onDismiss = { showPODetail = false })
    }

    if (showSuccessDialog) {
        PortalSuccessDialog(message = successMessage, onDismiss = { showSuccessDialog = false })
    }
}

@Composable
fun RecommendationFilterHeader(currentFilter: String, onFilterChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Filter By Status", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray)
        Box {
            Surface(
                modifier = Modifier.height(32.dp).clickable { expanded = true },
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                color = Color.White
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(currentFilter, fontSize = 11.sp, color = TextGray)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("All", "Pending", "Approved", "Rejected").forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status, fontSize = 12.sp) }, 
                        onClick = { 
                            onFilterChange(status)
                            expanded = false 
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RestockRecommendationCard(
    rec: RestockRecommendationData,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit,
    onCreatePO: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Restock ID: ${rec.restockId}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Text(text = rec.productName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = StaffPortalButtonBlue)
                }
                Surface(
                    color = when(rec.status) {
                        "Approved" -> Color(0xFFE8F5E9).copy(alpha = 0.5f)
                        "Rejected" -> Color(0xFFFFEBEE).copy(alpha = 0.5f)
                        else -> Color(0xFFFFF3E0).copy(alpha = 0.5f)
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, when(rec.status) {
                        "Approved" -> Color(0xFF2E7D32)
                        "Rejected" -> GasTrackRed
                        else -> Color(0xFFFFA000)
                    })
                ) {
                    Text(
                        text = rec.status.uppercase(),
                        color = when(rec.status) {
                            "Approved" -> Color(0xFF2E7D32)
                            "Rejected" -> GasTrackRed
                            else -> Color(0xFFFFA000)
                        },
                        fontSize = 9.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // --- Recommendation Data Grid ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DataPoint("Stock", rec.stockOnHand.toString())
                DataPoint("Demand", rec.predictedDemand.toString())
                DataPoint("Rec Qty", rec.recommendedQty.toString())
                DataPoint("Forecast", rec.forecastDate)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row {
                    IconButton(onClick = onApprove, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onReject, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, contentDescription = "Reject", tint = GasTrackRed, modifier = Modifier.size(18.dp)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Block, contentDescription = "Cancel", tint = TextGray, modifier = Modifier.size(18.dp)) }
                }
                
                Button(
                    onClick = onCreatePO,
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text("Create PO", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun PurchaseOrderCard(
    po: PurchaseOrderData,
    onView: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).shadow(2.dp, RoundedCornerShape(12.dp)),
        color = Color.White, shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = po.poNo, fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Text(text = "Vendor: ${po.supplierId}", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
                Text(text = String.format(Locale.US, "₱%,.2f", po.totalAmount), fontSize = 18.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Status: ${po.status}", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                Text(text = "Expected: ${po.expectedDeliveryDate}", fontSize = 12.sp, color = TextGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onView, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Visibility, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(18.dp)) }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp)) }
            }
        }
    }
}

@Composable
fun PurchaseOrderFormDialog(
    po: PurchaseOrderData? = null,
    onDismiss: () -> Unit,
    onSave: (PurchaseOrderData) -> Unit
) {
    var supplierId by remember { mutableStateOf(po?.supplierId ?: "Select Supplier") }
    var expectedDate by remember { mutableStateOf(po?.expectedDeliveryDate ?: "01/10/2026") }
    var remarks by remember { mutableStateOf(po?.remarks ?: "") }
    
    val items = remember { mutableStateListOf<PurchaseOrderItemData>().apply {
        if (po != null) {
            // Mock items for existing PO
            add(PurchaseOrderItemData("POI-001", po.purchaseOrderId, "P-001", "11kg Standard Refill", 50, 850.0, 42500.0))
        } else {
            // Start with one default item
            add(PurchaseOrderItemData("POI-NEW", "PO-NEW", "P-001", "11kg Standard Refill", 50, 850.0, 42500.0))
        }
    } }

    val totalAmount = items.sumOf { it.subtotal }
    
    var showSupplierDropdown by remember { mutableStateOf(false) }
    var showProductPicker by remember { mutableStateOf(false) }
    var itemToEditQty by remember { mutableStateOf<PurchaseOrderItemData?>(null) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), shape = RoundedCornerShape(20.dp), color = Color.White) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(if (po == null) "Create Purchase Order" else "Edit Purchase Order", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    StockFormField("PO Number", po?.poNo ?: "AUTO-GEN")
                    
                    // Supplier Selector
                    Box {
                        StockFormField("Supplier ID", supplierId, isDropdown = true, onClick = { showSupplierDropdown = true })
                        DropdownMenu(expanded = showSupplierDropdown, onDismissRequest = { showSupplierDropdown = false }) {
                            listOf("S-001 (Petron)", "S-002 (Shell)", "S-003 (Phoenix)").forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { supplierId = s; showSupplierDropdown = false })
                            }
                        }
                    }
                    
                    StockFormField("Restock Ref", po?.restockId ?: "Manual")
                    StockFormField("Expected Date", expectedDate)
                    StockFormField("Status", po?.status ?: "Draft")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Order Items", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
                        Button(onClick = { showProductPicker = true }, colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(32.dp)) {
                            Text("Add Product", fontSize = 11.sp)
                        }
                    }
                    
                    // --- Purchase Order Items List ---
                    items.forEach { item ->
                        OrderItemFormCard(
                            item = item,
                            onRemove = { items.remove(item) },
                            onEditQty = { itemToEditQty = item }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    EmployeeInputField(label = "REMARKS", value = remarks, onValueChange = { remarks = it }, placeholder = "Add notes...", icon = Icons.Default.Description)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailValueRow("Total Amount", String.format(Locale.US, "₱%,.2f", totalAmount), isBold = true, color = GasTrackRed)
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp)) { Text("Cancel", color = TextGray) }
                    Button(
                        enabled = supplierId != "Select Supplier" && items.isNotEmpty(),
                        onClick = { 
                            onSave(PurchaseOrderData(
                                po?.purchaseOrderId ?: "PO-${System.currentTimeMillis()}", supplierId, po?.restockId ?: "Manual", "U-001",
                                po?.poNo ?: "PO-${System.currentTimeMillis() % 10000}", "01/01/2026", expectedDate, "Submitted", totalAmount, remarks
                            ))
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) { 
                        Text("Submit", fontWeight = FontWeight.Black) 
                    }
                }
            }
        }
    }

    if (showProductPicker) {
        ProductPickerDialog(onDismiss = { showProductPicker = false }) { prod ->
            items.add(PurchaseOrderItemData("POI-${System.currentTimeMillis()}", po?.purchaseOrderId ?: "PO-NEW", prod.id, prod.name, 1, 850.0, 850.0))
            showProductPicker = false
        }
    }

    if (itemToEditQty != null) {
        QtyEditDialog(
            currentQty = itemToEditQty!!.quantity,
            onDismiss = { itemToEditQty = null },
            onSave = { newQty ->
                val index = items.indexOfFirst { it.id == itemToEditQty!!.id }
                if (index != -1) {
                    val item = items[index]
                    items[index] = item.copy(quantity = newQty, subtotal = newQty * item.unitCost)
                }
                itemToEditQty = null
            }
        )
    }
}

@Composable
fun ProductPickerDialog(onDismiss: () -> Unit, onProductSelected: (ProductData) -> Unit) {
    val products = listOf(
        ProductData("P-001", "11kg Standard Refill", "LPG", "Petron", "S-001", "Unit", 1150.0, 850.0, 10),
        ProductData("P-002", "2.7kg Camping Refill", "LPG", "Petron", "S-002", "Unit", 350.0, 250.0, 10),
        ProductData("P-003", "Gas Regulator", "Acc", "Standard", "S-003", "Unit", 450.0, 300.0, 10)
    )
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.9f)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Select Product", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(products) { p ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable { onProductSelected(p) },
                            color = Color(0xFFFBFBFE),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFF1F3F4))
                        ) {
                            Text(p.name, modifier = Modifier.padding(16.dp), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QtyEditDialog(currentQty: Int, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var qty by remember { mutableIntStateOf(currentQty) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.8f)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Update Quantity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if(qty > 1) qty-- }) { Icon(Icons.Default.Remove, contentDescription = null) }
                    Text(qty.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp))
                    IconButton(onClick = { qty++ }) { Icon(Icons.Default.Add, contentDescription = null) }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(onClick = { onSave(qty) }, modifier = Modifier.weight(1f)) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun PurchaseOrderDetailDialog(po: PurchaseOrderData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth(0.92f), shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("PO Details", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                DetailValueRow("PO Number", po.poNo, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Vendor", po.supplierId)
                DetailValueRow("Status", po.status, color = if(po.status == "Submitted") StaffPortalButtonBlue else Color(0xFF2E7D32))
                DetailValueRow("Expected At", po.expectedDeliveryDate)
                DetailValueRow("Created By", po.createdByUserId)
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("ITEMS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mock items for view
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(2) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFBFBFE),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFF1F3F4))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("50x 11kg Standard", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("₱ 42,500.00", fontSize = 13.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                DetailValueRow("TOTAL AMOUNT", String.format(Locale.US, "₱%,.2f", po.totalAmount), isBold = true, color = GasTrackRed)
                
                if (po.remarks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("REMARKS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray)
                    Text(po.remarks, fontSize = 13.sp, color = TextDark)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close Details", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun OrderItemFormCard(item: PurchaseOrderItemData, onRemove: () -> Unit, onEditQty: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFBFBFE),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F3F4))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.productName, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark, modifier = Modifier.weight(1f))
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(16.dp)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Qty:", fontSize = 12.sp, color = TextGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.clickable { onEditQty() },
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                        color = Color.White
                    ) {
                        Text(text = item.quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = String.format(Locale.US, "Unit: ₱%,.2f", item.unitCost), fontSize = 11.sp, color = TextGray)
                    Text(text = String.format(Locale.US, "₱%,.2f", item.subtotal), fontSize = 13.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                }
            }
        }
    }
}

@Composable
fun DataPoint(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextDark)
    }
}

@Preview(showBackground = true)
@Composable
fun RestockingEmployeeScreenPreview() {
    RestockingEmployeeScreen()
}
