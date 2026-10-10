package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gastrack.ui.theme.*
import java.util.Locale

data class OrderData(
    val id: String,
    val customerId: String,
    val orderNo: String,
    val date: String,
    val type: String, // Walk-in, Pickup, Delivery
    val status: String, // Pending, Confirmed, Processing, Completed, Cancelled
    val totalAmount: Double,
    val remarks: String = ""
)

data class OrderDetailData(
    val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

data class DeliveryData(
    val id: String,
    val salesId: String,
    val drNo: String,
    val date: String,
    val deliveredBy: String, // UserID
    val deliveryCharge: Double,
    val address: String,
    val status: String, // Pending, In Transit, Delivered, Cancelled
    val remarks: String = ""
)

@Composable
fun OrdersEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var mainTab by remember { mutableStateOf("Deliveries") }
    var orderStatusTab by remember { mutableStateOf("All") }
    var deliveryStatusTab by remember { mutableStateOf("All") }

    // Modal States
    var showOrderDetailDialog by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<OrderData?>(null) }
    
    var showDeliveryDetailDialog by remember { mutableStateOf(false) }
    var selectedDelivery by remember { mutableStateOf<DeliveryData?>(null) }

    var showStatusUpdateDialog by remember { mutableStateOf(false) }
    var showAssignDriverDialog by remember { mutableStateOf(false) }
    var statusToUpdate by remember { mutableStateOf<String?>(null) } // "Order" or "Delivery"

    var showEditDialog by remember { mutableStateOf(false) }
    var editingEntity by remember { mutableStateOf<Any?>(null) }

    var showCancelConfirm by remember { mutableStateOf(false) }
    var entityToCancel by remember { mutableStateOf<Any?>(null) }
    
    var showPrintProgress by remember { mutableStateOf(false) }
    var printMessage by remember { mutableStateOf("") }

    // Mock Data
    val orders = remember { mutableStateListOf(
        OrderData("O-001", "C-001", "ORD-2026-001", "2026-01-01", "Delivery", "Pending", 950.0, "Fragile"),
        OrderData("O-002", "C-002", "ORD-2026-002", "2026-01-01", "Pickup", "Confirmed", 280.0),
        OrderData("O-003", "C-003", "ORD-2026-003", "2026-01-02", "Walk-in", "Completed", 4500.0)
    ) }

    val deliveries = remember { mutableStateListOf(
        DeliveryData("D-001", "S-001", "DR-1001", "2026-01-01", "EMP-005", 50.0, "45 Mabini St, Navotas", "Pending"),
        DeliveryData("D-002", "S-002", "DR-1002", "2026-01-01", "EMP-006", 0.0, "12 Rizal Ave, Malabon", "In Transit")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)),
    ) {
        EmployeePortalHeader(
            title = if (mainTab == "Orders") "Orders Log" else "Logistics Queue",
            subtitle = "Operations Management",
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            EmployeeTabSelector(
                tabs = listOf("Orders", "Deliveries"),
                selectedTab = mainTab,
                onTabSelected = { mainTab = it }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (mainTab == "Orders") {
                OrdersTabView(
                    orders = orders,
                    selectedStatusTab = orderStatusTab,
                    onStatusTabSelected = { orderStatusTab = it },
                    onViewOrder = {
                        selectedOrder = it
                        showOrderDetailDialog = true
                    },
                    onUpdateStatus = {
                        selectedOrder = it
                        statusToUpdate = "Order"
                        showStatusUpdateDialog = true
                    },
                    onEditOrder = {
                        editingEntity = it
                        showEditDialog = true
                    },
                    onPrintOrder = {
                        printMessage = "Generating exportable invoice for ${it.orderNo}..."
                        showPrintProgress = true
                    },
                    onCancelOrder = {
                        entityToCancel = it
                        showCancelConfirm = true
                    }
                )
            } else {
                DeliveriesTabView(
                    deliveries = deliveries,
                    selectedStatusTab = deliveryStatusTab,
                    onStatusTabSelected = { deliveryStatusTab = it },
                    onViewDelivery = {
                        selectedDelivery = it
                        showDeliveryDetailDialog = true
                    },
                    onUpdateStatus = {
                        selectedDelivery = it
                        statusToUpdate = "Delivery"
                        showStatusUpdateDialog = true
                    },
                    onAssignDriver = {
                        selectedDelivery = it
                        showAssignDriverDialog = true
                    },
                    onEditDelivery = {
                        editingEntity = it
                        showEditDialog = true
                    },
                    onCancelDelivery = {
                        entityToCancel = it
                        showCancelConfirm = true
                    }
                )
            }
        }
    }

    if (showOrderDetailDialog && selectedOrder != null) {
        OrderDetailDialog(order = selectedOrder!!, onDismiss = { showOrderDetailDialog = false })
    }

    if (showDeliveryDetailDialog && selectedDelivery != null) {
        DeliveryDetailDialog(delivery = selectedDelivery!!, onDismiss = { showDeliveryDetailDialog = false })
    }

    if (showEditDialog && editingEntity != null) {
        OrderEditDialog(
            entity = editingEntity!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedEntity ->
                if (updatedEntity is OrderData) {
                    val index = orders.indexOfFirst { it.id == updatedEntity.id }
                    if (index != -1) orders[index] = updatedEntity
                } else if (updatedEntity is DeliveryData) {
                    val index = deliveries.indexOfFirst { it.id == updatedEntity.id }
                    if (index != -1) deliveries[index] = updatedEntity
                }
                showEditDialog = false
            }
        )
    }

    if (showStatusUpdateDialog) {
        UpdateStatusDialog(
            currentStatus = if (statusToUpdate == "Order") selectedOrder?.status ?: "" else selectedDelivery?.status ?: "",
            onDismiss = { showStatusUpdateDialog = false },
            onUpdate = { newStatus ->
                if (statusToUpdate == "Order" && selectedOrder != null) {
                    val index = orders.indexOfFirst { it.id == selectedOrder!!.id }
                    if (index != -1) orders[index] = selectedOrder!!.copy(status = newStatus)
                } else if (statusToUpdate == "Delivery" && selectedDelivery != null) {
                    val index = deliveries.indexOfFirst { it.id == selectedDelivery!!.id }
                    if (index != -1) deliveries[index] = selectedDelivery!!.copy(status = newStatus)
                }
                showStatusUpdateDialog = false
            }
        )
    }

    if (showAssignDriverDialog && selectedDelivery != null) {
        AssignDriverDialog(
            onDismiss = { showAssignDriverDialog = false },
            onAssign = { driverId ->
                val index = deliveries.indexOfFirst { it.id == selectedDelivery!!.id }
                if (index != -1) deliveries[index] = selectedDelivery!!.copy(deliveredBy = driverId)
                showAssignDriverDialog = false
            }
        )
    }

    if (showCancelConfirm && entityToCancel != null) {
        AlertDialog(
            onDismissRequest = { showCancelConfirm = false },
            title = { Text("Confirm Cancellation", fontWeight = FontWeight.Black) },
            text = { 
                val label = if (entityToCancel is OrderData) (entityToCancel as OrderData).orderNo else (entityToCancel as DeliveryData).drNo
                Text("Are you sure you want to cancel $label? This action will mark the record as void.") 
            },
            confirmButton = {
                TextButton(onClick = {
                    if (entityToCancel is OrderData) {
                        val order = entityToCancel as OrderData
                        val index = orders.indexOfFirst { it.id == order.id }
                        if (index != -1) orders[index] = order.copy(status = "Cancelled")
                    } else if (entityToCancel is DeliveryData) {
                        val delivery = entityToCancel as DeliveryData
                        val index = deliveries.indexOfFirst { it.id == delivery.id }
                        if (index != -1) deliveries[index] = delivery.copy(status = "Cancelled")
                    }
                    showCancelConfirm = false
                }) { Text("Confirm", color = GasTrackRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirm = false }) { Text("Keep Record") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showPrintProgress) {
        ActionFeedbackDialog(
            message = printMessage,
            onDismiss = { showPrintProgress = false }
        )
    }
}

@Composable
fun OrderEditDialog(entity: Any, onDismiss: () -> Unit, onSave: (Any) -> Unit) {
    var remarks by remember { mutableStateOf(if (entity is OrderData) entity.remarks else (entity as DeliveryData).remarks) }
    var amount by remember { mutableStateOf(if (entity is OrderData) entity.totalAmount.toString() else (entity as DeliveryData).deliveryCharge.toString()) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.92f)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (entity is OrderData) "Edit Order: ${entity.orderNo}" else "Edit Delivery: ${(entity as DeliveryData).drNo}",
                    fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                EmployeeInputField(
                    label = if (entity is OrderData) "TOTAL AMOUNT (₱)" else "DELIVERY CHARGE (₱)",
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = "0.00",
                    icon = Icons.Default.Payments
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                EmployeeInputField(
                    label = "REMARKS / NOTES",
                    value = remarks,
                    onValueChange = { remarks = it },
                    placeholder = "Add additional info...",
                    icon = Icons.AutoMirrored.Filled.Notes
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancel", color = TextGray)
                    }
                    Button(
                        onClick = {
                            if (entity is OrderData) {
                                onSave(entity.copy(remarks = remarks, totalAmount = amount.toDoubleOrNull() ?: entity.totalAmount))
                            } else if (entity is DeliveryData) {
                                onSave(entity.copy(remarks = remarks, deliveryCharge = amount.toDoubleOrNull() ?: entity.deliveryCharge))
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateStatusDialog(currentStatus: String, onDismiss: () -> Unit, onUpdate: (String) -> Unit) {
    val statuses = listOf("Pending", "Confirmed", "Processing", "In Transit", "Delivered", "Completed", "Cancelled")
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.9f)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Update Status", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                statuses.forEach { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onUpdate(status) }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = currentStatus == status, onClick = { onUpdate(status) }, colors = RadioButtonDefaults.colors(selectedColor = StaffPortalButtonBlue))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(status, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel", color = TextGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AssignDriverDialog(onDismiss: () -> Unit, onAssign: (String) -> Unit) {
    val drivers = listOf("EMP-005 (Juan)", "EMP-006 (Maria)", "EMP-007 (Andres)", "EMP-008 (Patrick)")
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.9f)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Assign Personnel", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                drivers.forEach { driver ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onAssign(driver) }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = StaffPortalButtonBlue)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(driver, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel", color = TextGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OrdersTabView(
    orders: List<OrderData>,
    selectedStatusTab: String,
    onStatusTabSelected: (String) -> Unit,
    onViewOrder: (OrderData) -> Unit,
    onUpdateStatus: (OrderData) -> Unit,
    onEditOrder: (OrderData) -> Unit,
    onPrintOrder: (OrderData) -> Unit,
    onCancelOrder: (OrderData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("All") }

    val filteredOrders = orders.filter {
        val matchesSearch = it.orderNo.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatusTab == "All") true else it.status == selectedStatusTab
        val matchesType = if (selectedTypeFilter == "All") true else it.type == selectedTypeFilter
        matchesSearch && matchesStatus && matchesType
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().height(56.dp), 
            placeholder = { Text("Search by Order No...", color = Color.LightGray, fontSize = 14.sp) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selectedTypeFilter != "All") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(if (selectedTypeFilter == "All") "All Types" else selectedTypeFilter, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (selectedTypeFilter != "All") StaffPortalButtonBlue else TextGray)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("All", "Walk-in", "Pickup", "Delivery").forEach { type ->
                        DropdownMenuItem(text = { Text(type, fontSize = 12.sp, fontWeight = FontWeight.Bold) }, onClick = { selectedTypeFilter = type; expanded = false })
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selectedStatusTab != "All") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(if (selectedStatusTab == "All") "All Status" else selectedStatusTab, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (selectedStatusTab != "All") StaffPortalButtonBlue else TextGray)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("All", "Pending", "Confirmed", "Processing", "Completed", "Cancelled").forEach { status ->
                        DropdownMenuItem(text = { Text(status, fontSize = 12.sp, fontWeight = FontWeight.Bold) }, onClick = { onStatusTabSelected(status); expanded = false })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredOrders) { order ->
                OrderDataCard(
                    order = order, 
                    onView = { onViewOrder(order) }, 
                    onSync = { onUpdateStatus(order) },
                    onEdit = { onEditOrder(order) },
                    onPrint = { onPrintOrder(order) },
                    onCancel = { onCancelOrder(order) }
                )
            }
        }
    }
}

@Composable
fun OrderDataCard(
    order: OrderData, 
    onView: () -> Unit, 
    onSync: () -> Unit,
    onEdit: () -> Unit,
    onPrint: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = order.orderNo, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                    Text(text = "Date: ${order.date}", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = when(order.status) {
                        "Completed" -> Color(0xFFE8F5E9)
                        "Cancelled" -> Color(0xFFFFEBEE)
                        "Pending" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status.uppercase(), 
                        color = when(order.status) {
                            "Completed" -> Color(0xFF2E7D32)
                            "Cancelled" -> GasTrackRed
                            "Pending" -> Color(0xFFEF6C00)
                            else -> StaffPortalButtonBlue
                        }, 
                        fontSize = 8.sp, 
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Type: ${order.type}", fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Bold)
                Text(text = String.format(Locale.US, "₱%,.2f", order.totalAmount), fontSize = 17.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
            }
            
            if (order.remarks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Remarks: ${order.remarks}", fontSize = 11.sp, color = TextGray, fontStyle = FontStyle.Italic)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                EmployeeActionItem(Icons.Default.Visibility, "View", StaffPortalButtonBlue, modifier = Modifier.weight(1f), onClick = onView)
                EmployeeActionItem(Icons.Default.Edit, "Edit", Color(0xFF4CAF50), modifier = Modifier.weight(1f), onClick = onEdit)
                
                Row(modifier = Modifier.weight(1.3f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSync, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Sync, contentDescription = "Update Status", tint = TextDark, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onPrint, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Print, contentDescription = "Print", tint = TextDark, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = GasTrackRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveriesTabView(
    deliveries: List<DeliveryData>,
    selectedStatusTab: String,
    onStatusTabSelected: (String) -> Unit,
    onViewDelivery: (DeliveryData) -> Unit,
    onUpdateStatus: (DeliveryData) -> Unit,
    onAssignDriver: (DeliveryData) -> Unit,
    onEditDelivery: (DeliveryData) -> Unit,
    onCancelDelivery: (DeliveryData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredDeliveries = deliveries.filter {
        val matchesSearch = it.drNo.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatusTab == "All") true else it.status == selectedStatusTab
        matchesSearch && matchesStatus
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().height(56.dp), 
            placeholder = { Text("Search by DR No...", color = Color.LightGray, fontSize = 14.sp) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Pending", "In Transit", "Delivered").forEach { status ->
                val isSelected = selectedStatusTab == status
                Surface(
                    modifier = Modifier.weight(1f).height(40.dp).clickable { onStatusTabSelected(status) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) StaffPortalButtonBlue else Color(0xFFFBFBFE),
                    border = BorderStroke(1.dp, if (isSelected) StaffPortalButtonBlue else Color(0xFFE9ECEF))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.White else TextGray, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredDeliveries) { delivery ->
                DeliveryDataCard(
                    delivery = delivery, 
                    onView = { onViewDelivery(delivery) }, 
                    onSync = { onUpdateStatus(delivery) }, 
                    onAssign = { onAssignDriver(delivery) },
                    onEdit = { onEditDelivery(delivery) },
                    onCancel = { onCancelDelivery(delivery) }
                )
            }
        }
    }
}

@Composable
fun DeliveryDataCard(
    delivery: DeliveryData, 
    onView: () -> Unit, 
    onSync: () -> Unit, 
    onAssign: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = delivery.drNo, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                    Text(text = "Date: ${delivery.date}", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = when(delivery.status) {
                        "Delivered" -> Color(0xFFE8F5E9)
                        "In Transit" -> Color(0xFFE3F2FD)
                        else -> Color(0xFFFFF3E0)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = delivery.status.uppercase(), 
                        color = when(delivery.status) {
                            "Delivered" -> Color(0xFF2E7D32)
                            "In Transit" -> StaffPortalButtonBlue
                            else -> Color(0xFFEF6C00)
                        }, 
                        fontSize = 8.sp, 
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = delivery.address, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Driver: ${delivery.deliveredBy}", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                EmployeeActionItem(Icons.Default.Visibility, "View", StaffPortalButtonBlue, modifier = Modifier.weight(1f), onClick = onView)
                EmployeeActionItem(Icons.Default.Edit, "Edit", Color(0xFF4CAF50), modifier = Modifier.weight(1f), onClick = onEdit)
                
                Row(modifier = Modifier.weight(1.3f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSync, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Sync, contentDescription = "Update Status", tint = TextDark, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = GasTrackRed, modifier = Modifier.size(18.dp))
                    }
                }
                
                Button(
                    onClick = onAssign,
                    modifier = Modifier.weight(1.5f).height(38.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text("Assign Driver", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun OrderDetailDialog(order: OrderData, onDismiss: () -> Unit) {
    val items = listOf(
        OrderDetailData("OD-001", order.id, "P-001", "11kg Standard Refill", 1, 950.0, 950.0)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Order Details", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                DetailValueRow("Order No", order.orderNo, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Date", order.date)
                DetailValueRow("Order Type", order.type)
                DetailValueRow("Status", order.status, color = if(order.status == "Completed") Color(0xFF2E7D32) else TextDark)
                
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))

                Text("ORDER ITEMS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF8F9FA), RoundedCornerShape(12.dp)).background(Color(0xFFFBFBFE)).padding(12.dp)) {
                    items.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${item.quantity}x ${item.productName}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(String.format(Locale.US, "₱%,.2f", item.subtotal), fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                DetailValueRow("TOTAL AMOUNT", String.format(Locale.US, "₱%,.2f", order.totalAmount), isBold = true, color = GasTrackRed)
                
                if (order.remarks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("REMARKS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray)
                    Text(order.remarks, fontSize = 12.sp, color = TextDark)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun DeliveryDetailDialog(delivery: DeliveryData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Delivery Tracker", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                DetailValueRow("DR Number", delivery.drNo, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Sales Ref", delivery.salesId)
                DetailValueRow("Status", delivery.status, color = if(delivery.status == "Delivered") Color(0xFF2E7D32) else StaffPortalButtonBlue, isBold = true)
                DetailValueRow("Assigned Driver", delivery.deliveredBy)
                
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))

                Text("DELIVERY ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFBFBFE),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = delivery.address,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close Tracker", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdersEmployeeScreenPreview() {
    OrdersEmployeeScreen()
}
