@file:Suppress("UnstableCollections", "ComposeUnstableCollections", "ComposeModifierWithoutDefault", "Stability")

package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

// --- Data Models ---
@Immutable
data class PickupOrderData(
    val id: String,
    val orderId: String,
    val customerName: String,
    val pickupStatus: String,
    val paymentStatus: String,
    val totalAmount: Double,
    val pickupScheduledAt: String,
    val canSettle: Boolean,
    val paymentMethod: String = "Cash",
    val type: String = "Pickup"
)

@Immutable
data class DeliveryOrderData(
    val id: String,
    val orderId: String,
    val deliveryId: String,
    val drNo: String,
    val customerName: String,
    val deliveryRiderId: String,
    val deliveryRiderName: String,
    val deliveryStatus: String,
    val attempts: Int,
    val maxAttempts: Int,
    val nextAttemptAt: String,
    val paymentStatus: String,
    val deliveryAddress: String,
    val canSettle: Boolean,
    val paymentMethod: String = "Cash on Delivery",
    val type: String = "Delivery",
    val deliveryFee: Double = 50.0,
    val totalAmount: Double = 0.0
)

@Immutable
data class ODCustomerData(
    val id: String,
    val type: String,
    val name: String,
    val phone: String,
    val address: String,
    val companyName: String = "",
    val firstName: String = "",
    val lastName: String = ""
)

@Immutable
data class CustomerHistoryOrder(
    val orderId: String,
    val id: String,
    val type: String,
    val status: String,
    val date: String,
    val totalAmount: Double,
    val settledAt: String?,
    val netRefundAmount: Double
)

@Composable
fun OrdersAndDeliveryEmployeeScreen(
    isDriver: Boolean = false, // If true, hides Pickup and Customer tabs
    onBack: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf(if (isDriver) "delivery" else "pickup") }

    // --- Mock States ---
    val pickups = remember { mutableStateListOf(
        PickupOrderData("ORD-101", "O-101", "Juan Dela Cruz", "Preparing", "Paid", 950.0, "2026-01-10 10:00 AM", false),
        PickupOrderData("ORD-102", "O-102", "Maria Clara", "Ready for Pickup", "Unpaid", 1000.0, "2026-01-10 11:00 AM", false),
        PickupOrderData("ORD-103", "O-103", "Andres Bonifacio", "Unclaimed", "Paid", 2850.0, "2026-01-09 09:00 AM", true)
    ) }

    val deliveries = remember { mutableStateListOf(
        DeliveryOrderData("ORD-201", "O-201", "D-201", "DR-5001", "Pedro Penduko", "EMP-005", "Juan Driver", "Preparing", 0, 3, "", "Paid", "45 Mabini St.", false, totalAmount = 950.0),
        DeliveryOrderData("ORD-202", "O-202", "D-202", "DR-5002", "Jose Rizal", "EMP-005", "Juan Driver", "Out for Delivery", 1, 3, "", "Unpaid", "12 Rizal Ave.", false, totalAmount = 2850.0),
        DeliveryOrderData("ORD-203", "O-203", "D-203", "DR-5003", "Emilio Aguinaldo", "", "Unassigned", "Failed Attempt", 2, 3, "2026-01-11", "Paid", "Kawit, Cavite", false, totalAmount = 1000.0)
    ) }

    val customers = remember { mutableStateListOf(
        ODCustomerData("CUST-001", "Residential", "Juan Dela Cruz", "09171234567", "45 Mabini St.", "", "Juan", "Dela Cruz"),
        ODCustomerData("CUST-002", "Commercial", "ABC Corp", "09181234567", "Makati City", "ABC Corp", "Maria", "Clara")
    ) }

    val driversList = listOf("EMP-005" to "Juan Driver", "EMP-006" to "Maria Driver")

    // --- Filters ---
    var deliverySearch by remember { mutableStateOf("") }
    var deliveryStatusFilter by remember { mutableStateOf("Delivery Status") }
    var customerSearch by remember { mutableStateOf("") }
    var customerTypeFilter by remember { mutableStateOf("All Customer Types") }

    // --- Modals ---
    var pickupModalKind by remember { mutableStateOf("") } // "complete", "settle", "cancel"
    var selectedOrder by remember { mutableStateOf<Any?>(null) } // PickupOrderData or DeliveryOrderData
    
    var showLogFailedModal by remember { mutableStateOf(false) }
    
    var showCustomerModal by remember { mutableStateOf(false) }
    var customerIsNew by remember { mutableStateOf(true) }
    var selectedCustomer by remember { mutableStateOf<ODCustomerData?>(null) }
    
    var showConfirmDelete by remember { mutableStateOf(false) }
    var deleteKind by remember { mutableStateOf("") } // "order", "customer"
    var confirmDeleteOrder by remember { mutableStateOf<Any?>(null) }
    
    var showViewOrderModal by remember { mutableStateOf(false) }
    var viewOrderType by remember { mutableStateOf("") } // "Pickup", "Delivery"
    
    var showCustomerHistory by remember { mutableStateOf(false) }
    
    var toastMessage by remember { mutableStateOf<String?>(null) }

    fun showToast(msg: String) {
        toastMessage = msg
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground)
    ) {
        EmployeePortalHeader(
            title = "Order and Delivery",
            onBack = onBack
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            val tabs = mutableListOf<String>()
            if (!isDriver) tabs.add("Pickup (${pickups.size})")
            tabs.add("Delivery (${deliveries.size})")
            if (!isDriver) tabs.add("Customers")

            EmployeeTabSelector(
                tabs = tabs,
                selectedTab = when(activeTab) {
                    "pickup" -> "Pickup (${pickups.size})"
                    "delivery" -> "Delivery (${deliveries.size})"
                    else -> "Customers"
                },
                onTabSelected = {
                    activeTab = when {
                        it.startsWith("Pickup") -> "pickup"
                        it.startsWith("Delivery") -> "delivery"
                        else -> "customers"
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ==========================================
            // TAB: PICKUP
            // ==========================================
            if (activeTab == "pickup" && !isDriver) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { showToast("List refreshed.") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                                border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = StaffPortalButtonBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Refresh", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        if (pickups.isEmpty()) {
                            Text("No active paid pickup orders.", modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center, color = TextGray)
                        } else {
                            pickups.forEach { order ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).shadow(3.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                            Column {
                                                Text(order.id, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = TextDark)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(order.customerName, fontSize = 14.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                            }
                                            Text(String.format(Locale.US, "₱%,.2f", order.totalAmount), fontWeight = FontWeight.Black, fontSize = 18.sp, color = StaffPortalButtonBlue)
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            ODStatusPill(if (order.pickupStatus == "Unclaimed") "Expired (Unclaimed)" else order.pickupStatus)
                                            ODStatusPill(order.paymentStatus)
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Scheduled: ${order.pickupScheduledAt}", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                        }
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        HorizontalDivider(color = Color(0xFFF1F3F4))
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                IconButton(onClick = { selectedOrder = order; viewOrderType = "Pickup"; showViewOrderModal = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Visibility, contentDescription = "View", tint = Color(0xFFD4A017), modifier = Modifier.size(20.dp)) }
                                                IconButton(onClick = { showToast("Printing receipt for ${order.id}") }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Print, contentDescription = "Print", tint = Color(0xFF1F2937), modifier = Modifier.size(20.dp)) }
                                                IconButton(
                                                    onClick = { selectedOrder = order; pickupModalKind = "cancel" }, 
                                                    modifier = Modifier.size(32.dp),
                                                    enabled = order.pickupStatus in listOf("Preparing", "Ready for Pickup")
                                                ) { Icon(Icons.Default.Block, contentDescription = "Cancel", tint = if (order.pickupStatus in listOf("Preparing", "Ready for Pickup")) GasTrackRed else Color.LightGray, modifier = Modifier.size(20.dp)) }
                                            }

                                            when {
                                                order.pickupStatus == "Preparing" -> {
                                                    Button(
                                                        onClick = { 
                                                            val idx = pickups.indexOfFirst { it.id == order.id }
                                                            if(idx != -1) pickups[idx] = order.copy(pickupStatus = "Ready for Pickup")
                                                            showToast("Pickup updated.")
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.height(36.dp)
                                                    ) { Text("Mark Ready", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                                }
                                                order.pickupStatus == "Ready for Pickup" -> {
                                                    Button(
                                                        onClick = { selectedOrder = order; pickupModalKind = "complete" },
                                                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.height(36.dp)
                                                    ) { Text("Complete Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                                }
                                                order.canSettle -> {
                                                    OutlinedButton(
                                                        onClick = { selectedOrder = order; pickupModalKind = "settle" },
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.height(36.dp)
                                                    ) { Text("Restock & Settle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // TAB: DELIVERY
            // ==========================================
            if (activeTab == "delivery") {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        EmployeeSearchBar(query = deliverySearch, onQueryChange = { deliverySearch = it }, placeholder = "Search delivery, order, customer...")
                        Spacer(modifier = Modifier.height(8.dp))
                        FilterDropdown(
                            options = listOf("Delivery Status", "Preparing", "Out for Delivery", "Failed Attempt", "Cancelled", "Delivery Failed"),
                            selectedOption = deliveryStatusFilter,
                            onOptionSelected = { deliveryStatusFilter = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val filteredDeliveries = deliveries.filter {
                            val q = deliverySearch.trim().lowercase()
                            val matchesSearch = q.isEmpty() || it.drNo.lowercase().contains(q) || it.id.lowercase().contains(q) || it.customerName.lowercase().contains(q) || it.deliveryRiderName.lowercase().contains(q)
                            val matchesStatus = deliveryStatusFilter == "Delivery Status" || it.deliveryStatus == deliveryStatusFilter
                            matchesSearch && matchesStatus
                        }

                        if (filteredDeliveries.isEmpty()) {
                            Text("No delivery orders match your search.", modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center, color = TextGray)
                        } else {
                            filteredDeliveries.forEach { order ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).shadow(3.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(order.drNo.ifEmpty { order.id }, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = TextDark)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(order.customerName, fontSize = 14.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                ODStatusPill(order.deliveryStatus)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                ODStatusPill(order.paymentStatus)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Driver: ${order.deliveryRiderName.ifEmpty { "Unassigned" }}", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(order.deliveryAddress, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Attempt ${order.attempts}/${order.maxAttempts} ${if (order.nextAttemptAt.isNotEmpty()) "(Next: ${order.nextAttemptAt})" else ""}", fontSize = 11.sp, color = TextDark, fontWeight = FontWeight.Bold)
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        HorizontalDivider(color = Color(0xFFF1F3F4))
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            if (!isDriver) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    IconButton(onClick = { selectedOrder = order; viewOrderType = "Delivery"; showViewOrderModal = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Visibility, contentDescription = "View", tint = Color(0xFFD4A017), modifier = Modifier.size(20.dp)) }
                                                    IconButton(onClick = { showToast("Printing receipt for ${order.id}") }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Print, contentDescription = "Print", tint = Color(0xFF1F2937), modifier = Modifier.size(20.dp)) }
                                                    IconButton(
                                                        onClick = { confirmDeleteOrder = order; deleteKind = "order"; showConfirmDelete = true }, 
                                                        modifier = Modifier.size(32.dp),
                                                        enabled = order.deliveryStatus in listOf("Preparing", "Failed Attempt") && order.paymentStatus != "Paid"
                                                    ) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = if (order.deliveryStatus in listOf("Preparing", "Failed Attempt") && order.paymentStatus != "Paid") GasTrackRed else Color.LightGray, modifier = Modifier.size(20.dp)) }
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.width(1.dp))
                                            }

                                            // Action Buttons Flow
                                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (!isDriver && order.deliveryStatus in listOf("Preparing", "Failed Attempt") && order.attempts < order.maxAttempts) {
                                                    Button(
                                                        onClick = { 
                                                            if (order.deliveryRiderId.isEmpty()) {
                                                                showToast("Assign a driver using the view icon before dispatching.")
                                                            } else {
                                                                val idx = deliveries.indexOfFirst { it.id == order.id }
                                                                if(idx != -1) deliveries[idx] = order.copy(deliveryStatus = "Out for Delivery")
                                                                showToast("Delivery dispatched.")
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.height(34.dp),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                                                    ) { Text("Dispatch", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                                    if (order.deliveryRiderId.isEmpty()) {
                                                        Text("Driver required", fontSize = 9.sp, color = TextGray)
                                                    }
                                                }
                                                if (order.deliveryStatus in listOf("Out for Delivery", "Failed Attempt")) {
                                                    Button(
                                                        onClick = { selectedOrder = order; pickupModalKind = "complete" },
                                                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.height(34.dp),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                                                    ) { Text("Confirm Delivery", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                                }
                                                if (order.deliveryStatus == "Out for Delivery") {
                                                    OutlinedButton(
                                                        onClick = { selectedOrder = order; showLogFailedModal = true },
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.height(34.dp),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                                                    ) { Text("Log Failed Attempt", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark) }
                                                }
                                                if (!isDriver && order.canSettle) {
                                                    OutlinedButton(
                                                        onClick = { selectedOrder = order; pickupModalKind = "settle" },
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.height(34.dp),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                                                    ) { Text("Restock & Settle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // TAB: CUSTOMERS
            // ==========================================
            if (activeTab == "customers" && !isDriver) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        EmployeeSearchBar(query = customerSearch, onQueryChange = { customerSearch = it }, placeholder = "Search Customer")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterDropdown(
                                options = listOf("All Customer Types", "Residential", "Commercial"),
                                selectedOption = customerTypeFilter,
                                onOptionSelected = { customerTypeFilter = it },
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { customerIsNew = true; selectedCustomer = null; showCustomerModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text("+ Add Customer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        val filteredCustomers = customers.filter {
                            val q = customerSearch.trim().lowercase()
                            val matchesSearch = q.isEmpty() || it.name.lowercase().contains(q) || it.id.lowercase().contains(q) || it.phone.contains(q)
                            val matchesType = customerTypeFilter == "All Customer Types" || it.type == customerTypeFilter
                            matchesSearch && matchesType
                        }

                        if (filteredCustomers.isEmpty()) {
                            Text("No customers match your search.", modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center, color = TextGray)
                        } else {
                            filteredCustomers.forEach { cust ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).shadow(3.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Surface(
                                                    color = StaffPortalButtonBlue.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.size(40.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.Person, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(22.dp))
                                                    }
                                                }
                                                Column {
                                                    Text(cust.id, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                                                    Text(cust.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                                                }
                                            }
                                            ODStatusPill(cust.type)
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(cust.phone.ifEmpty { "—" }, fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(cust.address.ifEmpty { "—" }, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                        }
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        HorizontalDivider(color = Color(0xFFF1F3F4))
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { selectedCustomer = cust; showCustomerHistory = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Visibility, contentDescription = "View Profile", tint = Color(0xFFD4A017), modifier = Modifier.size(20.dp)) }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(onClick = { selectedCustomer = cust; customerIsNew = false; showCustomerModal = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Edit, contentDescription = "Edit Customer", tint = Color(0xFF22C55E), modifier = Modifier.size(20.dp)) }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(onClick = { confirmDeleteOrder = cust; deleteKind = "customer"; showConfirmDelete = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, contentDescription = "Delete Customer", tint = GasTrackRed, modifier = Modifier.size(20.dp)) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODALS
    // ==========================================

    if (toastMessage != null) {
        PortalSuccessDialog(message = toastMessage!!, onDismiss = { toastMessage = null })
    }

    if (pickupModalKind == "complete" && selectedOrder != null) {
        val isDelivery = selectedOrder is DeliveryOrderData
        val orderAmount = if (isDelivery) (selectedOrder as DeliveryOrderData).totalAmount else (selectedOrder as PickupOrderData).totalAmount
        val paymentMethod = if (isDelivery) (selectedOrder as DeliveryOrderData).paymentMethod else (selectedOrder as PickupOrderData).paymentMethod
        val isCod = isDelivery && paymentMethod == "Cash on Delivery"
        
        var amountCollected by remember { mutableStateOf("") }
        var emptyReturned by remember { mutableStateOf("no") } // "yes", "no"
        var error by remember { mutableStateOf("") }
        
        Dialog(onDismissRequest = { pickupModalKind = ""; selectedOrder = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(if (isDelivery) "Confirm Delivery" else "Complete Claim", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Order ID:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Text(if (isDelivery) (selectedOrder as DeliveryOrderData).id else (selectedOrder as PickupOrderData).id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    
                    if (isCod) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Amount to Collect:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Text(formatPeso(orderAmount), fontSize = 16.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = amountCollected, onValueChange = { amountCollected = it },
                            label = { Text("Amount Collected") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Empty Cylinder Returned?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = emptyReturned == "yes", onClick = { emptyReturned = "yes" }, colors = RadioButtonDefaults.colors(selectedColor = StaffPortalButtonBlue))
                        Text("Yes", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = emptyReturned == "no", onClick = { emptyReturned = "no" }, colors = RadioButtonDefaults.colors(selectedColor = StaffPortalButtonBlue))
                        Text("No", fontSize = 14.sp)
                    }
                    
                    if (error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error, color = GasTrackRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { pickupModalKind = ""; selectedOrder = null }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp)) { Text("Cancel", color = TextDark) }
                        Button(
                            onClick = { 
                                if (isCod && (amountCollected.toDoubleOrNull() ?: 0.0) < orderAmount) {
                                    error = "Amount collected must cover the amount to collect."
                                } else {
                                    if (isDelivery) {
                                        val idx = deliveries.indexOfFirst { it.id == (selectedOrder as DeliveryOrderData).id }
                                        if(idx != -1) deliveries[idx] = (selectedOrder as DeliveryOrderData).copy(deliveryStatus = "Delivered", paymentStatus = "Paid")
                                        showToast("Delivery confirmed.")
                                    } else {
                                        val idx = pickups.indexOfFirst { it.id == (selectedOrder as PickupOrderData).id }
                                        if(idx != -1) pickups[idx] = (selectedOrder as PickupOrderData).copy(pickupStatus = "Completed")
                                        showToast("Pickup claim completed.")
                                    }
                                    pickupModalKind = ""
                                    selectedOrder = null
                                }
                            }, 
                            modifier = Modifier.weight(1f).height(48.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text(if (isDelivery) "Confirm" else "Confirm Claim", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }

    if (pickupModalKind == "settle" && selectedOrder != null) {
        var refundMethod by remember { mutableStateOf("GCash") }
        var accountName by remember { mutableStateOf("") }
        var accountNumber by remember { mutableStateOf("") }
        var refNo by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { pickupModalKind = ""; selectedOrder = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                    Text("Restock & Settle Order", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Text(if (selectedOrder is DeliveryOrderData) (selectedOrder as DeliveryOrderData).id else (selectedOrder as PickupOrderData).id, fontSize = 14.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upon confirmation, ordered items will be restored to inventory.", fontSize = 12.sp, color = TextGray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Original Paid Amount", fontSize = 13.sp); Text("₱ 1,000.00", fontSize = 13.sp) }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Cancellation Fee", fontSize = 13.sp); Text("₱ 50.00", fontSize = 13.sp) }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Net Refund Amount", fontSize = 14.sp, fontWeight = FontWeight.Bold); Text("₱ 950.00", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GasTrackRed) }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Refund Method", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("GCash", "Card", "Bank").forEach { method ->
                            Surface(
                                modifier = Modifier.weight(1f).height(40.dp).clickable { refundMethod = method },
                                shape = RoundedCornerShape(8.dp),
                                color = if (refundMethod == method) StaffPortalButtonBlue else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (refundMethod == method) StaffPortalButtonBlue else Color(0xFFE2E8F0))
                            ) { Box(contentAlignment = Alignment.Center) { Text(method, color = if (refundMethod == method) Color.White else TextDark, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = accountName, onValueChange = { accountName = it }, label = { Text("Account Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("Account/Mobile Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = refNo, onValueChange = { refNo = it }, label = { Text("Payout Reference No. (Optional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { pickupModalKind = ""; selectedOrder = null }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp)) { Text("Close", color = TextDark) }
                        Button(
                            onClick = { 
                                val isDelivery = selectedOrder is DeliveryOrderData
                                if (isDelivery) {
                                    val idx = deliveries.indexOfFirst { it.id == (selectedOrder as DeliveryOrderData).id }
                                    if(idx != -1) deliveries[idx] = (selectedOrder as DeliveryOrderData).copy(canSettle = false)
                                    showToast("Delivery settled and moved to customer order history.")
                                } else {
                                    val idx = pickups.indexOfFirst { it.id == (selectedOrder as PickupOrderData).id }
                                    if(idx != -1) pickups[idx] = (selectedOrder as PickupOrderData).copy(canSettle = false)
                                    showToast("Pickup settled and inventory restored.")
                                }
                                pickupModalKind = ""
                                selectedOrder = null
                            }, 
                            modifier = Modifier.weight(1f).height(48.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Confirm Settlement", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }

    if (pickupModalKind == "cancel" && selectedOrder != null) {
        Dialog(onDismissRequest = { pickupModalKind = ""; selectedOrder = null }) {
            Surface(modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Cancel Pickup", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cancel this pickup within its expiration threshold. Stock and payment will remain pending settlement.", fontSize = 14.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { pickupModalKind = ""; selectedOrder = null }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp)) { Text("Close", color = TextDark) }
                        Button(
                            onClick = { 
                                val idx = pickups.indexOfFirst { it.id == (selectedOrder as PickupOrderData).id }
                                if(idx != -1) pickups[idx] = (selectedOrder as PickupOrderData).copy(pickupStatus = "Cancelled", canSettle = true)
                                showToast("Pickup cancelled.")
                                pickupModalKind = ""
                                selectedOrder = null
                            }, 
                            modifier = Modifier.weight(1f).height(48.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = GasTrackRed),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Confirm", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }

    if (showLogFailedModal && selectedOrder != null) {
        var reason by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        val reasons = listOf("Recipient Unavailable / No One to Receive", "Customer Unreachable", "Insufficient Cash on Hand (COD Unpaid)", "Premises Inaccessible", "Severe Weather / Flooding", "Item Damaged in Transit", "Other (Specify in Notes)")
        var expanded by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showLogFailedModal = false; selectedOrder = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Log Failed Attempt", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Text((selectedOrder as DeliveryOrderData).id, fontSize = 14.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (error.isNotEmpty()) {
                        Text(error, color = GasTrackRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text("Reason for Failed Attempt", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box {
                        OutlinedTextField(
                            value = reason.ifEmpty { "Select a reason" }, onValueChange = {}, readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = TextDark, disabledBorderColor = Color(0xFFD1D5DB))
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            reasons.forEach { r -> DropdownMenuItem(text = { Text(r) }, onClick = { reason = r; expanded = false }) }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Attempt details / notes ${if (reason == "Other (Specify in Notes)") "(required)" else "(optional)"}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notes, onValueChange = { notes = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showLogFailedModal = false; selectedOrder = null }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp)) { Text("Cancel", color = TextDark) }
                        Button(
                            onClick = { 
                                if (reason.isEmpty()) { error = "Select a reason for the failed attempt." }
                                else if (reason == "Other (Specify in Notes)" && notes.isBlank()) { error = "Specify the reason in notes." }
                                else {
                                    val idx = deliveries.indexOfFirst { it.id == (selectedOrder as DeliveryOrderData).id }
                                    if(idx != -1) deliveries[idx] = (selectedOrder as DeliveryOrderData).copy(deliveryStatus = "Failed Attempt", attempts = deliveries[idx].attempts + 1)
                                    showToast("Failed attempt logged.")
                                    showLogFailedModal = false
                                    selectedOrder = null
                                }
                            }, 
                            modifier = Modifier.weight(1f).height(48.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Log Attempt", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }

    if (showCustomerModal) {
        var companyName by remember { mutableStateOf(selectedCustomer?.companyName ?: "") }
        var firstName by remember { mutableStateOf(selectedCustomer?.firstName ?: "") }
        var lastName by remember { mutableStateOf(selectedCustomer?.lastName ?: "") }
        var phone by remember { mutableStateOf(selectedCustomer?.phone ?: "") }
        var address by remember { mutableStateOf(selectedCustomer?.address ?: "") }
        var custType by remember { mutableStateOf(selectedCustomer?.type ?: "Residential") }

        Dialog(onDismissRequest = { showCustomerModal = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())) {
                    Text(if (customerIsNew) "Add Customer" else "Edit Customer", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Customer ID", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = selectedCustomer?.id ?: "Auto-generated (CUST-NEW)", onValueChange = {}, readOnly = true, 
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFD1D5DB)),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = TextDark)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Customer Type", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        listOf("Residential", "Commercial").forEach { t ->
                            Surface(
                                modifier = Modifier.weight(1f).height(48.dp).clickable { custType = t },
                                shape = RoundedCornerShape(8.dp),
                                color = if (custType == t) StaffPortalButtonBlue else Color.White,
                                border = BorderStroke(1.dp, if (custType == t) StaffPortalButtonBlue else Color(0xFFD1D5DB))
                            ) { Box(contentAlignment = Alignment.Center) { Text(t, color = if (custType == t) Color.White else TextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold) } }
                        }
                    }

                    if (custType == "Commercial") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Company Name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = companyName, onValueChange = { companyName = it }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedBorderColor = StaffPortalButtonBlue, unfocusedBorderColor = Color(0xFFD1D5DB)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = TextDark)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Contact Person", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("First Name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = firstName, onValueChange = { firstName = it }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedBorderColor = StaffPortalButtonBlue, unfocusedBorderColor = Color(0xFFD1D5DB)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = TextDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Last Name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = lastName, onValueChange = { lastName = it }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedBorderColor = StaffPortalButtonBlue, unfocusedBorderColor = Color(0xFFD1D5DB)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = TextDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Contact Number", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it }, 
                        modifier = Modifier.fillMaxWidth().height(48.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedBorderColor = StaffPortalButtonBlue, unfocusedBorderColor = Color(0xFFD1D5DB)),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = TextDark)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Address", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = address, onValueChange = { address = it }, 
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedBorderColor = StaffPortalButtonBlue, unfocusedBorderColor = Color(0xFFD1D5DB)),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = TextDark)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { showCustomerModal = false }, 
                            modifier = Modifier.weight(1f).height(48.dp), 
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0), contentColor = Color.Black),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                        ) { Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                        Button(
                            onClick = { 
                                if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() || address.isBlank() || (custType == "Commercial" && companyName.isBlank())) {
                                    showToast("Please fill in all required fields.")
                                } else {
                                    val nameStr = if (custType == "Commercial") companyName else "$firstName $lastName"
                                    if (customerIsNew) {
                                        customers.add(ODCustomerData("CUST-${System.currentTimeMillis() % 10000}", custType, nameStr, phone, address, companyName, firstName, lastName))
                                        showToast("Customer added.")
                                    } else {
                                        val idx = customers.indexOfFirst { it.id == selectedCustomer!!.id }
                                        if (idx != -1) customers[idx] = selectedCustomer!!.copy(type = custType, name = nameStr, phone = phone, address = address, companyName = companyName, firstName = firstName, lastName = lastName)
                                        showToast("Customer updated.")
                                    }
                                    showCustomerModal = false 
                                }
                            }, 
                            modifier = Modifier.weight(1f).height(48.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) { Text("Save", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                    }
                }
            }
        }
    }

    if (showConfirmDelete && confirmDeleteOrder != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false; confirmDeleteOrder = null },
            title = { Text(if (deleteKind == "order") "Cancel order?" else "Permanently delete customer?", fontWeight = FontWeight.Black) },
            text = { 
                if (deleteKind == "order") {
                    val label = if (confirmDeleteOrder is PickupOrderData) (confirmDeleteOrder as PickupOrderData).id else (confirmDeleteOrder as DeliveryOrderData).id
                    Text("This will cancel order $label and restore its stock. This cannot be undone.")
                } else {
                    val label = (confirmDeleteOrder as ODCustomerData).name
                    Text("This will permanently delete $label. Transaction history will be preserved. This cannot be undone.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deleteKind == "order") {
                            if (confirmDeleteOrder is PickupOrderData) {
                                val idx = pickups.indexOfFirst { it.id == (confirmDeleteOrder as PickupOrderData).id }
                                if(idx != -1) pickups[idx] = (confirmDeleteOrder as PickupOrderData).copy(pickupStatus = "Cancelled", canSettle = true)
                            } else {
                                val idx = deliveries.indexOfFirst { it.id == (confirmDeleteOrder as DeliveryOrderData).id }
                                if(idx != -1) deliveries[idx] = (confirmDeleteOrder as DeliveryOrderData).copy(deliveryStatus = "Cancelled", canSettle = true)
                            }
                            showToast("Order cancelled and stock restored.")
                        } else {
                            customers.removeAll { it.id == (confirmDeleteOrder as ODCustomerData).id }
                            showToast("Customer permanently deleted.")
                        }
                        showConfirmDelete = false
                        confirmDeleteOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GasTrackRed)
                ) { Text(if (deleteKind == "order") "Cancel Order" else "Delete", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmDelete = false; confirmDeleteOrder = null }) { Text("Back", color = TextDark) }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showViewOrderModal && selectedOrder != null) {
        val isDelivery = viewOrderType == "Delivery"
        val delOrd = if (isDelivery) selectedOrder as DeliveryOrderData else null
        val pickOrd = if (!isDelivery) selectedOrder as PickupOrderData else null

        var dNotes by remember { mutableStateOf("") }
        var dDriver by remember { mutableStateOf(delOrd?.deliveryRiderId ?: "") }

        Dialog(onDismissRequest = { showViewOrderModal = false; selectedOrder = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), 
                shape = RoundedCornerShape(16.dp), 
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                    // Title Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.SpaceBetween, 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Order Details", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isDelivery) "DR: ${delOrd?.drNo?.ifEmpty { delOrd.id } ?: ""}" else "Order ID: ${pickOrd?.id ?: ""}", 
                                fontSize = 13.sp, 
                                color = StaffPortalButtonBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { showViewOrderModal = false; selectedOrder = null }) { 
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDark) 
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section 1: Order Information Card
                    Surface(
                        color = Color(0xFFF8FAFC), 
                        shape = RoundedCornerShape(12.dp), 
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Order Type", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(if (isDelivery) "Delivery" else "Pickup", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Status", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    ODStatusPill(if (isDelivery) delOrd?.deliveryStatus ?: "" else pickOrd?.pickupStatus ?: "")
                                }
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Order Date", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("2026-01-10", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Payment Status", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    ODStatusPill(if (isDelivery) delOrd?.paymentStatus ?: "" else pickOrd?.paymentStatus ?: "")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section 2: Items Summary
                    Text("ITEMS & PRICING SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Product Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.weight(2f))
                                Text("Qty", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("UnitPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("Subtotal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.weight(1f), textAlign = TextAlign.Right)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("11kg Standard Refill", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark, modifier = Modifier.weight(2f))
                                Text("1", fontSize = 13.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = TextDark)
                                Text("₱850.00", fontSize = 13.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = TextDark)
                                Text("₱850.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Right, color = TextDark)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (isDelivery) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Delivery Fee:", fontSize = 13.sp, color = TextGray)
                                    Text(formatPeso(delOrd?.deliveryFee ?: 0.0), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Amount:", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextDark)
                                Text(formatPeso(if (isDelivery) delOrd?.totalAmount ?: 0.0 else pickOrd?.totalAmount ?: 0.0), fontSize = 16.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section 3: Customer & Fulfillment Details
                    Text("CUSTOMER & FULFILLMENT", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Customer Name", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(if (isDelivery) delOrd?.customerName ?: "" else pickOrd?.customerName ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Payment Method", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(if (isDelivery) delOrd?.paymentMethod ?: "" else pickOrd?.paymentMethod ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                            }
                            if (isDelivery && delOrd != null) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Delivery Address", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(delOrd.deliveryAddress, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                }
                            }
                            if (!isDelivery && pickOrd != null) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Scheduled Pickup", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(pickOrd.pickupScheduledAt, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                }
                            }
                        }
                    }

                    // Section 4: Assignment (If Delivery)
                    if (isDelivery && delOrd != null) {
                        val isLocked = delOrd.deliveryStatus in listOf("Delivered", "Cancelled", "Delivery Failed")
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("DRIVER ASSIGNMENT & NOTES", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Assigned Delivery Driver", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedTextField(
                                value = driversList.find { it.first == dDriver }?.second ?: "Unassigned", onValueChange = {}, readOnly = true,
                                modifier = Modifier.fillMaxWidth().clickable(enabled = !isLocked) { expanded = true },
                                enabled = !isLocked,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = TextDark, 
                                    disabledBorderColor = Color(0xFFD1D5DB),
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedBorderColor = StaffPortalButtonBlue
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            )
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("Unassigned") }, onClick = { dDriver = ""; expanded = false })
                                driversList.forEach { dr -> DropdownMenuItem(text = { Text(dr.second) }, onClick = { dDriver = dr.first; expanded = false }) }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Text("Notes / Special Instructions", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = dNotes, onValueChange = { dNotes = it },
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            enabled = !isLocked,
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("Enter special notes...", color = Color.LightGray, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = TextDark, 
                                disabledBorderColor = Color(0xFFD1D5DB),
                                unfocusedBorderColor = Color(0xFFD1D5DB),
                                focusedBorderColor = StaffPortalButtonBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (!isLocked) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { showViewOrderModal = false; selectedOrder = null }, 
                                    modifier = Modifier.weight(1f).height(48.dp), 
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0), contentColor = Color.Black),
                                    border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                                ) { 
                                    Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 15.sp) 
                                }
                                Button(
                                    onClick = { 
                                        val idx = deliveries.indexOfFirst { it.id == delOrd.id }
                                        if (idx != -1) {
                                            deliveries[idx] = delOrd.copy(deliveryRiderId = dDriver, deliveryRiderName = driversList.find { it.first == dDriver }?.second ?: "")
                                        }
                                        showToast("Delivery updated.")
                                        showViewOrderModal = false
                                        selectedOrder = null
                                    }, 
                                    modifier = Modifier.weight(1f).height(48.dp), 
                                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) { 
                                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 15.sp) 
                                }
                            }
                        } else {
                            Button(
                                onClick = { showViewOrderModal = false; selectedOrder = null }, 
                                modifier = Modifier.fillMaxWidth().height(48.dp), 
                                shape = RoundedCornerShape(8.dp), 
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = TextDark)
                            ) { 
                                Text("Close", fontWeight = FontWeight.Bold, fontSize = 15.sp) 
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showViewOrderModal = false; selectedOrder = null }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), 
                            shape = RoundedCornerShape(8.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = TextDark)
                        ) { 
                            Text("Close", fontWeight = FontWeight.Bold, fontSize = 15.sp) 
                        }
                    }
                }
            }
        }
    }

    if (showCustomerHistory && selectedCustomer != null) {
        var custTab by remember { mutableStateOf("profile") }
        val cust = selectedCustomer!!
        
        val mockHistory = listOf(
            CustomerHistoryOrder("O-101", "ORD-101", "Pickup", "Completed", "2026-01-01", 950.0, null, 0.0),
            CustomerHistoryOrder("O-201", "ORD-201", "Delivery", "Cancelled", "2025-12-25", 2850.0, "2025-12-26", 2800.0)
        )

        Dialog(onDismissRequest = { showCustomerHistory = false; selectedCustomer = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Header
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            Text("View Customer", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(cust.name, fontSize = 14.sp, color = TextGray)
                        }
                        IconButton(onClick = { showCustomerHistory = false; selectedCustomer = null }, modifier = Modifier.size(24.dp)) { 
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDark) 
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Sub-tabs
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(
                            modifier = Modifier.clickable { custTab = "profile" }
                        ) {
                            Text(
                                "Customer Profile", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = if (custTab == "profile") StaffPortalButtonBlue else TextGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(110.dp)
                                    .background(if (custTab == "profile") StaffPortalButtonBlue else Color.Transparent)
                            )
                        }

                        Column(
                            modifier = Modifier.clickable { custTab = "history" }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "Order History", 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = if (custTab == "history") StaffPortalButtonBlue else TextGray
                                )
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "${mockHistory.size}", 
                                        color = StaffPortalButtonBlue, 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(120.dp)
                                    .background(if (custTab == "history") StaffPortalButtonBlue else Color.Transparent)
                            )
                        }
                    }
                    
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    if (custTab == "profile") {
                        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                            Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(modifier = Modifier.fillMaxWidth()) { 
                                        Text("Customer ID", fontSize = 13.sp, color = TextGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(cust.id, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                    }
                                    Column(modifier = Modifier.fillMaxWidth()) { 
                                        Text("Customer Type", fontSize = 13.sp, color = TextGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(cust.type, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                    } 
                                    if (cust.type == "Commercial") {
                                        Column(modifier = Modifier.fillMaxWidth()) { 
                                            Text("Company Name", fontSize = 13.sp, color = TextGray)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(cust.companyName.ifEmpty { "—" }, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                        }
                                        Column(modifier = Modifier.fillMaxWidth()) { 
                                            Text("Contact Person", fontSize = 13.sp, color = TextGray)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("${cust.firstName} ${cust.lastName}".trim().ifEmpty { "—" }, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                        } 
                                    } else {
                                        Column(modifier = Modifier.fillMaxWidth()) { 
                                            Text("First Name", fontSize = 13.sp, color = TextGray)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(cust.firstName.ifEmpty { "—" }, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                        }
                                        Column(modifier = Modifier.fillMaxWidth()) { 
                                            Text("Last Name", fontSize = 13.sp, color = TextGray)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(cust.lastName.ifEmpty { "—" }, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                        } 
                                    }
                                    Column(modifier = Modifier.fillMaxWidth()) { 
                                        Text("Contact Number", fontSize = 13.sp, color = TextGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(cust.phone.ifEmpty { "—" }, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                    }
                                    Column(modifier = Modifier.fillMaxWidth()) { 
                                        Text("Address", fontSize = 13.sp, color = TextGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(cust.address.ifEmpty { "—" }, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark) 
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(mockHistory) { hist ->
                                Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text(hist.id, fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextDark)
                                            ODStatusPill(hist.status)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("${hist.type} · ${hist.date} · ${formatPeso(hist.totalAmount)}", fontSize = 12.sp, color = TextGray)
                                        if (hist.settledAt != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Settled. Net Refund: ${formatPeso(hist.netRefundAmount)}", fontSize = 12.sp, color = GasTrackRed, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            IconButton(onClick = { showToast("Viewing history order...") }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFD4A017)) }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            IconButton(onClick = { showToast("Printing receipt...") }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF1F2937)) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ODStatusPill(status: String) {
    val (bgColor, textColor) = when (status) {
        "Preparing" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        "Ready", "Ready for Pickup", "Out for Delivery", "Residential" -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        "Completed", "Delivered", "Paid", "Commercial" -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        "Expired (Unclaimed)", "Unclaimed", "Cancelled", "Unpaid", "Failed Attempt", "Delivery Failed" -> Color(0xFFFFE4E6) to Color(0xFF9F1239)
        else -> Color(0xFFE2E8F0) to Color(0xFF475569)
    }
    Surface(color = bgColor, shape = RoundedCornerShape(999.dp)) {
        Text(status, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun OrdersAndDeliveryEmployeeScreenPreview() {
    OrdersAndDeliveryEmployeeScreen()
}
