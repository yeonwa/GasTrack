package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Stable
data class POSCustomerData(
    val id: Int, 
    val name: String, 
    val customerType: String, 
    val phone: String, 
    val address: String, 
    val status: String, 
    val registeredBy: String, 
    val lastOrder: String, 
    val initials: String,
)

@Stable
data class HeldCartData(
    val id: String,
    val timestamp: String,
    val items: Map<Int, Int>,
    val customerType: String,
    val discountRate: Double,
    val totalAmount: Double,
    val pickupDate: String = "",
    val pickupTime: String = "",
    val deliveryAddress: String = "",
    val deliveryInstructions: String = "",
    val vehicleType: String = "",
    val assignedRider: String = "",
    val paymentMethod: String = "",
    val selectedCustomer: POSCustomerData? = null,
)

@Composable
fun POSEmployeeScreen(
    onBack: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf("Products") }
    var showPayPopup by remember { mutableStateOf(false) }
    var showOrderDetails by remember { mutableStateOf(false) }

    // Modal States for Customer Directory
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showEditCustomerDialog by remember { mutableStateOf(false) }
    var showViewCustomerDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    var customerToEdit by remember { mutableStateOf<POSCustomerData?>(null) }
    var customerToView by remember { mutableStateOf<POSCustomerData?>(null) }
    var customerToDelete by remember { mutableStateOf<POSCustomerData?>(null) }

    // Held Transactions State
    val heldCarts = remember { mutableStateListOf<HeldCartData>() }
    var showHeldModal by remember { mutableStateOf(false) }
    
    // Cart & Order Fulfillment States
    val cart = remember { mutableStateMapOf<Int, Int>() }
    var selectedCustomer by remember { mutableStateOf<POSCustomerData?>(null) }
    var customerType by remember { mutableStateOf("Walk-in") }
    var discountRate by remember { mutableDoubleStateOf(0.0) } // 0.0, 0.05, 0.10

    // Fulfillment Modal States
    var showPickupModal by remember { mutableStateOf(false) }
    var pickupDate by remember { mutableStateOf("2026-01-20") }
    var pickupTime by remember { mutableStateOf("10:00 AM") }

    var showDeliveryModal by remember { mutableStateOf(false) }
    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryInstructions by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Motor") }
    var assignedRider by remember { mutableStateOf("EMP-005 (Juan)") }

    // Customers Directory State
    val customers = remember { mutableStateListOf(
        POSCustomerData(1, "Juan Dela Cruz", "Residential", "0917-123-4567", "45 Mabini St, Navotas City", "Active", "EMP-001", "2 DAYS AGO", "JD"),
        POSCustomerData(2, "Maria Clara", "Commercial", "0918-987-6543", "12 Rizal Ave, Malabon City", "Active", "EMP-002", "1 WEEK AGO", "MC"),
        POSCustomerData(3, "Andres Bonifacio", "Residential", "0917-123-4567", "8 Bonifacio St, Caloocan City", "Inactive", "EMP-001", "1 MONTH AGO", "AB")
    ) }
    
    // Master Product List (using POSProductData from OrderComponents)
    val products = remember { mutableStateListOf(
        POSProductData(1, "Gasul LPG 2.7 kg", 250.0, R.drawable.tank_2_7kg, 15, "Petron", "Gasul LPG", "Pasig Warehouse"),
        POSProductData(2, "Gasul LPG 7 kg", 650.0, R.drawable.tank_11kg, 20, "Petron", "Gasul LPG", "Pasig Warehouse"),
        POSProductData(3, "Gasul LPG 11 kg Elite", 1050.0, R.drawable.tank_11kg, 30, "Petron", "Gasul LPG", "San Juan Warehouse"),
        POSProductData(4, "Gasul LPG 11 kg", 1000.0, R.drawable.tank_11kg, 50, "Petron", "Gasul LPG", "Pasig Warehouse"),
        POSProductData(5, "Gasul LPG 22 kg", 2000.0, R.drawable.tank_50kg, 10, "Petron", "Gasul LPG", "Pasig Warehouse"),
        POSProductData(6, "Gasul LPG 50 kg", 4500.0, R.drawable.tank_50kg, 8, "Petron", "Gasul LPG", "San Juan Warehouse"),
        POSProductData(7, "Cylinder 2.7 kg", 1500.0, R.drawable.tank_2_7kg, 5, "Generic", "Cylinder", "Pasig Warehouse"),
        POSProductData(8, "Cylinder LPG 7 kg", 2000.0, R.drawable.tank_11kg, 12, "Generic", "Cylinder", "San Juan Warehouse"),
        POSProductData(9, "Cylinder 11 kg Elite", 2800.0, R.drawable.tank_11kg, 10, "Generic", "Cylinder", "Pasig Warehouse"),
        POSProductData(10, "Cylinder 11 kg", 2500.0, R.drawable.tank_11kg, 18, "Generic", "Cylinder", "Pasig Warehouse"),
        POSProductData(11, "Cylinder 22 kg", 4500.0, R.drawable.tank_50kg, 6, "Generic", "Cylinder", "San Juan Warehouse"),
        POSProductData(12, "Cylinder 50 kg", 8000.0, R.drawable.tank_50kg, 4, "Generic", "Cylinder", "Pasig Warehouse"),
        POSProductData(13, "LPG Hose Clamp", 50.0, R.drawable.tank_2_7kg, 100, "Generic", "Accessories", "Pasig Warehouse"),
        POSProductData(14, "LPG Hose Clamp with 1.5m Hose", 250.0, R.drawable.tank_11kg, 45, "Generic", "Accessories", "San Juan Warehouse"),
        POSProductData(15, "LPG Hose (Per Meter)", 100.0, R.drawable.tank_11kg, 200, "Generic", "Accessories", "Pasig Warehouse"),
        POSProductData(16, "POL Regulator", 450.0, R.drawable.tank_11kg, 25, "Generic", "Accessories", "Pasig Warehouse"),
        POSProductData(17, "TPA Regulator", 550.0, R.drawable.tank_11kg, 30, "Generic", "Accessories", "San Juan Warehouse"),
        POSProductData(18, "Reyna Gas Stove", 1200.0, R.drawable.tank_50kg, 15, "Reyna", "Accessories", "Pasig Warehouse")
    ) }

    // Financial Calculation Breakdown
    val subtotalInclusive = products.sumOf { (id, _, price) -> (cart[id] ?: 0) * price }
    val discountAmount = subtotalInclusive * discountRate
    val totalSalesInclusive = subtotalInclusive - discountAmount
    val vatAmount = totalSalesInclusive * (0.12 / 1.12)
    val amountNetOfVat = totalSalesInclusive - vatAmount
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground) // Darker background to make white cards pop
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmployeePortalHeader(
                title = "Checkout Station",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                EmployeeTabSelector(
                    tabs = listOf("Products", "Cart (${cart.values.sum()})"),
                    selectedTab = if (selectedTab.startsWith("Cart")) "Cart (${cart.values.sum()})" else selectedTab
                ) { tab ->
                    selectedTab = if (tab.startsWith("Cart")) "Cart" else tab
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (selectedTab == "Products") {
                    POSProductsTab(
                        products = products,
                        cart = cart
                    ) { id, qty ->
                        val product = products.find { it.id == id }
                        if (product != null) {
                            val clampedQty = qty.coerceIn(0, product.stock)
                            if (clampedQty <= 0) cart.remove(id) else cart[id] = clampedQty
                        }
                    }
                } else {
                    POSCartTabView(
                        cart = cart,
                        products = products,
                        discountRate = discountRate,
                        customerType = customerType,
                        selectedCustomer = selectedCustomer,
                        pickupDate = pickupDate,
                        pickupTime = pickupTime,
                        deliveryAddress = deliveryAddress,
                        subtotalInclusive = subtotalInclusive,
                        discountAmount = discountAmount,
                        vatAmount = vatAmount,
                        amountNetOfVat = amountNetOfVat,
                        totalAmountDue = totalSalesInclusive,
                        heldCount = heldCarts.size,
                        onDiscountChange = { discountRate = it },
                        onCustomerTypeChange = { newType ->
                            customerType = newType
                            if (newType == "Pickup") showPickupModal = true
                            else if (newType == "Delivery") showDeliveryModal = true
                        },
                        onConfigureFulfillment = {
                            if (customerType == "Pickup") showPickupModal = true
                            else if (customerType == "Delivery") showDeliveryModal = true
                        },
                        onClearCart = { 
                            cart.clear() 
                            selectedCustomer = null
                            pickupDate = ""
                            pickupTime = ""
                            deliveryAddress = ""
                            deliveryInstructions = ""
                        }
                    ) { showHeldModal = true }
                }
            }
            
            POSBottomBar(
                totalAmount = totalSalesInclusive,
                onCheckoutClick = { 
                    if (totalSalesInclusive > 0) {
                        when (customerType) {
                            "Pickup" -> if (selectedCustomer == null) showPickupModal = true else showPayPopup = true
                            "Delivery" -> if ((selectedCustomer == null) || deliveryAddress.isEmpty()) showDeliveryModal = true else showPayPopup = true
                            else -> showPayPopup = true
                        }
                    } 
                },
                onHoldClick = {
                    if (cart.isNotEmpty()) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)
                        val timestamp = sdf.format(Date())
                        heldCarts.add(
                            HeldCartData(
                                id = "HOLD-${System.currentTimeMillis() % 10000}",
                                timestamp = timestamp,
                                items = HashMap(cart),
                                customerType = customerType,
                                discountRate = discountRate,
                                totalAmount = totalSalesInclusive,
                                pickupDate = pickupDate,
                                pickupTime = pickupTime,
                                deliveryAddress = deliveryAddress,
                                deliveryInstructions = deliveryInstructions,
                                vehicleType = vehicleType,
                                assignedRider = assignedRider,
                                paymentMethod = "",
                                selectedCustomer = selectedCustomer
                            )
                        )
                        cart.clear()
                        selectedCustomer = null
                        customerType = "Walk-in"
                        pickupDate = ""
                        pickupTime = ""
                        deliveryAddress = ""
                        deliveryInstructions = ""
                        vehicleType = "Motor"
                        assignedRider = ""
                    }
                }
            )
        }
    }

    // Checkout Flow
    var selectedPaymentMethod by remember { mutableStateOf("Cash") }

    if (showPayPopup) {
        PayPopup(
            totalAmount = totalSalesInclusive,
            subtotal = subtotalInclusive,
            discount = discountAmount,
            vat = vatAmount,
            netOfVat = amountNetOfVat,
            onDismiss = { showPayPopup = false },
            onConfirm = { type, method ->
                customerType = type
                selectedPaymentMethod = method
                showPayPopup = false
                showOrderDetails = true
            }
        )
    }

    if (showOrderDetails) {
        OrderDetailsPopup(
            cart = cart,
            products = products,
            customer = selectedCustomer,
            orderType = customerType,
            paymentMethod = selectedPaymentMethod,
            subtotal = subtotalInclusive,
            discount = discountAmount,
            vat = vatAmount,
            netOfVat = amountNetOfVat,
            totalAmount = totalSalesInclusive,
            onBackClick = {
                showOrderDetails = false
                showPayPopup = true
            },
            onDismiss = {
                showOrderDetails = false
                cart.clear()
            }
        )
    }

    if (showHeldModal) {
        HeldCartsModalDialog(
            heldCarts = heldCarts,
            onDismiss = { showHeldModal = false },
            onRestore = { held ->
                cart.clear()
                held.items.forEach { (k, v) -> cart[k] = v }
                customerType = held.customerType
                discountRate = held.discountRate
                pickupDate = held.pickupDate
                pickupTime = held.pickupTime
                deliveryAddress = held.deliveryAddress
                deliveryInstructions = held.deliveryInstructions
                vehicleType = held.vehicleType
                assignedRider = held.assignedRider
                selectedCustomer = held.selectedCustomer
                heldCarts.remove(held)
                showHeldModal = false
                selectedTab = "Cart (${cart.values.sum()})"
            },
            onDiscard = { held ->
                heldCarts.remove(held)
            }
        )
    }

    if (showPickupModal) {
        PickupDetailsModalDialog(
            pickupDate = pickupDate,
            pickupTime = pickupTime,
            selectedCustomer = selectedCustomer,
            customers = customers,
            onCustomerSelect = { selectedCustomer = it },
            onAddNewCustomer = { showAddCustomerDialog = true },
            onDismiss = { showPickupModal = false },
            onSave = { date, time ->
                pickupDate = date
                pickupTime = time
                showPickupModal = false
            }
        )
    }

    if (showDeliveryModal) {
        DeliveryDetailsModalDialog(
            deliveryAddress = deliveryAddress,
            instructions = deliveryInstructions,
            vehicleType = vehicleType,
            assignedRider = assignedRider,
            selectedCustomer = selectedCustomer,
            customers = customers,
            onCustomerSelect = { selectedCustomer = it },
            onAddNewCustomer = { showAddCustomerDialog = true },
            onDismiss = { showDeliveryModal = false },
            onSave = { addr, inst, vehicle, rider ->
                deliveryAddress = addr
                deliveryInstructions = inst
                vehicleType = vehicle
                assignedRider = rider
                showDeliveryModal = false
            }
        )
    }

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onAdd = { name, phone, addr, type, status ->
                val newId = (customers.maxOfOrNull { it.id } ?: 0) + 1
                val newCustomer = POSCustomerData(newId, name, type, phone, addr, status, "EMP-001", "JUST NOW", name.take(2).uppercase())
                customers.add(newCustomer)
                
                // Automatically assign the newly created customer
                selectedCustomer = newCustomer
                
                showAddCustomerDialog = false
            }
        )
    }

    if (showEditCustomerDialog && (customerToEdit != null)) {
        AddCustomerDialog(
            customer = customerToEdit,
            onDismiss = { showEditCustomerDialog = false },
            onAdd = { name, phone, addr, type, status ->
                val index = customers.indexOfFirst { it.id == customerToEdit!!.id }
                if (index != -1) {
                    customers[index] = customerToEdit!!.copy(name = name, phone = phone, address = addr, customerType = type, status = status)
                }
                showEditCustomerDialog = false
            }
        )
    }

    if (showViewCustomerDialog && (customerToView != null)) {
        ViewCustomerDialog(customer = customerToView!!, onDismiss = { showViewCustomerDialog = false })
    }

    if (showDeleteConfirm && customerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Customer", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to delete ${customerToDelete!!.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        customers.removeAll { it.id == customerToDelete!!.id }
                        showDeleteConfirm = false
                    }
                ) { Text("Delete", color = GasTrackRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun POSProductsTab(
    products: SnapshotStateList<POSProductData>,
    cart: SnapshotStateMap<Int, Int>,
    onUpdateQuantity: (Int, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedBrand by remember { mutableStateOf("All") }
    
    
    val categories = listOf("All Categories", "Cylinder", "Accessories", "Gasul LPG")
    val brands = listOf("All", "Petron", "Solane", "M-Gas", "Phoenix")

    val filteredProducts = products.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All Categories" || it.category == selectedCategory
        val matchesBrand = selectedBrand == "All" || it.brand == selectedBrand
        matchesSearch && matchesCategory && matchesBrand
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EmployeeSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search product name..."
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    FilterDropdown(
                        options = categories,
                        selectedOption = selectedCategory,
                        onOptionSelected = { selectedCategory = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("BRAND", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    FilterDropdown(
                        options = brands,
                        selectedOption = selectedBrand,
                        onOptionSelected = { selectedBrand = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredProducts) { product ->
                POSProductCard(
                    product = product,
                    quantity = cart[product.id] ?: 0,
                    onUpdateQuantity = { onUpdateQuantity(product.id, it) }
                )
            }
        }
    }
}

@Composable
fun POSProductCard(product: POSProductData, quantity: Int, onUpdateQuantity: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp), color = Color.White
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(64.dp), shape = RoundedCornerShape(10.dp),
                color = if (product.stock > 0) StaffPortalButtonBlue.copy(alpha = 0.05f) else Color(0xFFF1F3F4)
            ) {
                Image(painter = painterResource(id = product.imageRes), contentDescription = null, modifier = Modifier.padding(8.dp).fillMaxSize())
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Stock: ${product.stock} • ${product.brand}", color = if (product.stock > 5) TextGray else GasTrackRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = String.format(Locale.US, "₱%,.2f", product.price), color = StaffPortalButtonBlue, fontWeight = FontWeight.Black, fontSize = 17.sp)
            }
            if (product.stock > 0) {
                if (quantity > 0) {
                    POSQuantitySelector(quantity = quantity, onUpdateQuantity = onUpdateQuantity)
                } else {
                    Button(
                        onClick = { onUpdateQuantity(1) },
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text("ADD", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            } else {
                Surface(color = Color(0xFFF1F3F4), shape = RoundedCornerShape(8.dp)) {
                    Text("OUT OF STOCK", modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun POSCartTabView(
    cart: SnapshotStateMap<Int, Int>,
    products: SnapshotStateList<POSProductData>,
    discountRate: Double,
    customerType: String,
    selectedCustomer: POSCustomerData?,
    pickupDate: String,
    pickupTime: String,
    deliveryAddress: String,
    subtotalInclusive: Double,
    discountAmount: Double,
    vatAmount: Double,
    amountNetOfVat: Double,
    totalAmountDue: Double,
    heldCount: Int,
    onDiscountChange: (Double) -> Unit,
    onCustomerTypeChange: (String) -> Unit,
    onConfigureFulfillment: () -> Unit,
    onClearCart: () -> Unit,
    onShowHeldClick: () -> Unit
) {
    var expandedDiscount by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onClearCart,
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GasTrackRed.copy(alpha = 0.1f), contentColor = GasTrackRed)
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear Cart", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
            
            Button(
                onClick = onShowHeldClick,
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue.copy(alpha = 0.1f), contentColor = StaffPortalButtonBlue)
            ) {
                Icon(Icons.Default.PauseCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Held ($heldCount)", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }

        // Fulfillment Type Bar
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ORDER FULFILLMENT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Walk-in", "Pickup", "Delivery").forEach { type ->
                        val isSelected = customerType == type
                        Surface(
                            modifier = Modifier.weight(1f).height(40.dp).clickable { onCustomerTypeChange(type) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) StaffPortalButtonBlue else Color(0xFFF1F3F4),
                            border = BorderStroke(1.dp, if (isSelected) StaffPortalButtonBlue else Color(0xFFE9ECEF))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(type, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextGray)
                            }
                        }
                    }
                }

                if (customerType != "Walk-in") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onConfigureFulfillment() },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE3F2FD),
                        border = BorderStroke(1.dp, StaffPortalButtonBlue.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(if (customerType == "Pickup") "Pickup: $pickupDate at $pickupTime" else "Delivery to: ${deliveryAddress.ifEmpty { "Set Address" }}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StaffPortalButtonBlue)
                                Text("Customer: ${selectedCustomer?.name ?: "Select Customer"}", fontSize = 10.sp, color = TextGray)
                            }
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = StaffPortalButtonBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Selected Cart Items List
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ITEMS IN CART", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))

                if (cart.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cart is empty", color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    cart.forEach { (productId, qty) ->
                        val product = products.find { it.id == productId }
                        if (product != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextDark)
                                    Text("₱${product.price} x $qty", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                }
                                Text(String.format(Locale.US, "₱%,.2f", product.price * qty), fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
                            }
                            HorizontalDivider(color = Color(0xFFF1F3F4))
                        }
                    }
                }
            }
        }

        // Financial Summary & VAT Breakdown Card
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("DISCOUNT & PAYMENT BREAKDOWN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Discount Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedDiscount = true },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when(discountRate) {
                                    0.05 -> "Senior / PWD (5%)"
                                    0.10 -> "Member (10%)"
                                    else -> "No Discount (0%)"
                                },
                                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                        }
                    }
                    DropdownMenu(expanded = expandedDiscount, onDismissRequest = { expandedDiscount = false }) {
                        DropdownMenuItem(text = { Text("No Discount (0%)", fontSize = 12.sp) }, onClick = { onDiscountChange(0.0); expandedDiscount = false })
                        DropdownMenuItem(text = { Text("Senior / PWD (5%)", fontSize = 12.sp) }, onClick = { onDiscountChange(0.05); expandedDiscount = false })
                        DropdownMenuItem(text = { Text("Member (10%)", fontSize = 12.sp) }, onClick = { onDiscountChange(0.10); expandedDiscount = false })
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                DetailValueRow("Subtotal (VAT Inclusive)", String.format(Locale.US, "₱%,.2f", subtotalInclusive))
                DetailValueRow("Less: Discount", String.format(Locale.US, "-₱%,.2f", discountAmount), color = GasTrackRed)
                DetailValueRow("Less: VAT (12%)", String.format(Locale.US, "₱%,.2f", vatAmount))
                DetailValueRow("Amount Net of VAT", String.format(Locale.US, "₱%,.2f", amountNetOfVat))
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE9ECEF))
                
                DetailValueRow("TOTAL AMOUNT DUE", String.format(Locale.US, "₱%,.2f", totalAmountDue), isBold = true, color = StaffPortalButtonBlue)
            }
        }
    }
}

@Composable
fun POSQuantitySelector(quantity: Int, onUpdateQuantity: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(10.dp)).background(Color(0xFFFBFBFE), RoundedCornerShape(10.dp)).padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        IconButton(onClick = { onUpdateQuantity(quantity - 1) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Remove, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.size(18.dp)) }
        Text(text = quantity.toString(), modifier = Modifier.padding(horizontal = 10.dp), fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
        IconButton(onClick = { onUpdateQuantity(quantity + 1) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Add, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.size(18.dp)) }
    }
}

@Composable
fun POSBottomBar(totalAmount: Double, onCheckoutClick: () -> Unit, onHoldClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onHoldClick,
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ButtonOrange),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonOrange),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(text = "Hold", fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                contentPadding = PaddingValues(horizontal = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Checkout", fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = String.format(Locale.US, "Pay ₱%,.2f", totalAmount), fontSize = 15.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Suppress("unused")
@Composable
private fun POSCustomersTab(
    customers: SnapshotStateList<POSCustomerData>,
    selectedCustomer: POSCustomerData?,
    onCustomerSelected: (POSCustomerData?) -> Unit,
    onAddNewClick: () -> Unit,
    onViewClick: (POSCustomerData) -> Unit,
    onEditClick: (POSCustomerData) -> Unit,
    onDeleteClick: (POSCustomerData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("All") }
    
    val filteredCustomers = customers.filter { 
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
        val matchesType = selectedTypeFilter == "All" || it.customerType == selectedTypeFilter
        matchesSearch && matchesType
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search customer...", color = Color.LightGray, fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Type Filter Dropdown
        var expandedTypeFilter by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("CUSTOMER TYPE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expandedTypeFilter = true },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedTypeFilter == "All") "All Customer Types" else selectedTypeFilter,
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                DropdownMenu(
                    expanded = expandedTypeFilter,
                    onDismissRequest = { expandedTypeFilter = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    listOf("All", "Residential", "Commercial").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(if (type == "All") "All Customer Types" else type, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                            onClick = {
                                selectedTypeFilter = type
                                expandedTypeFilter = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { onCustomerSelected(null) }, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.5.dp, if (selectedCustomer == null) StaffPortalButtonBlue else Color(0xFFE9ECEF))) {
                Text("Walk-in", fontWeight = FontWeight.Black, color = if (selectedCustomer == null) StaffPortalButtonBlue else TextGray, fontSize = 13.sp)
            }
            Button(onClick = onAddNewClick, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue.copy(alpha = 0.1f), contentColor = StaffPortalButtonBlue)) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add New", fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "CUSTOMER DIRECTORY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(filteredCustomers) { customer ->
                POSCustomerCard(
                    customer = customer, 
                    isSelected = selectedCustomer?.id == customer.id, 
                    onSelect = { onCustomerSelected(customer) },
                    onView = { onViewClick(customer) },
                    onEdit = { onEditClick(customer) },
                    onDelete = { onDeleteClick(customer) }
                )
            }
        }
    }
}

@Suppress("unused")
@Composable
private fun POSCustomerCard(
    customer: POSCustomerData, 
    isSelected: Boolean, 
    onSelect: () -> Unit,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)).clickable { onSelect() },
        shape = RoundedCornerShape(12.dp), color = Color.White,
        border = BorderStroke(1.5.dp, if (isSelected) StaffPortalButtonBlue else Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), color = StaffPortalButtonBlue.copy(alpha = 0.1f)) {
                    Box(contentAlignment = Alignment.Center) { Text(text = customer.initials, color = StaffPortalButtonBlue, fontWeight = FontWeight.Black, fontSize = 14.sp) }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = customer.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = if (customer.status == "Active") StatusActive.copy(alpha = 0.1f) else StatusInactive.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(customer.status.uppercase(), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 8.sp, fontWeight = FontWeight.Black, color = if (customer.status == "Active") StatusActive else StatusInactive)
                        }
                    }
                    Text(text = "${customer.customerType} • ${customer.phone}", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                RadioButton(selected = isSelected, onClick = onSelect, colors = RadioButtonDefaults.colors(selectedColor = StaffPortalButtonBlue))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = customer.address, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium, maxLines = 1)
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Last order: ${customer.lastOrder}", modifier = Modifier.weight(1f), fontSize = 9.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
                
                IconButton(onClick = onView, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Visibility, contentDescription = "View", tint = StaffPortalButtonBlue, modifier = Modifier.size(18.dp)) }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp)) }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GasTrackRed, modifier = Modifier.size(18.dp)) }
            }
        }
    }
}

@Composable
fun PayPopup(
    totalAmount: Double,
    subtotal: Double,
    discount: Double,
    vat: Double,
    netOfVat: Double,
    onDismiss: () -> Unit, 
    onConfirm: (String, String) -> Unit
) {
    var amountCollected by remember { mutableStateOf("") }
    val collected = amountCollected.toDoubleOrNull() ?: 0.0
    val change = if (collected >= totalAmount) collected - totalAmount else 0.0

    var orderType by remember { mutableStateOf("Walk-in") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var expandedOrderType by remember { mutableStateOf(false) }
    var expandedPaymentMethod by remember { mutableStateOf(false) }

    val orderTypes = listOf("Walk-in", "Pickup", "Delivery")
    val paymentMethods = listOf("Cash", "GCash", "Card", "Bank Transfer")

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.95f)) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text("Complete Payment", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                DetailValueRow("Subtotal (VAT Inc)", String.format(Locale.US, "₱%,.2f", subtotal))
                DetailValueRow("Less Discount", String.format(Locale.US, "-₱%,.2f", discount), color = GasTrackRed)
                DetailValueRow("Less VAT (12%)", String.format(Locale.US, "₱%,.2f", vat))
                DetailValueRow("Amount Net of VAT", String.format(Locale.US, "₱%,.2f", netOfVat))
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F3F4))
                
                DetailValueRow("TOTAL AMOUNT DUE", String.format(Locale.US, "₱%,.2f", totalAmount), isBold = true, color = StaffPortalButtonBlue)
                Spacer(modifier = Modifier.height(16.dp))

                // Order Type Dropdown
                Text(text = "ORDER TYPE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedOrderType = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(orderType, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                        }
                    }
                    DropdownMenu(expanded = expandedOrderType, onDismissRequest = { expandedOrderType = false }, modifier = Modifier.fillMaxWidth(0.7f)) {
                        orderTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type, fontSize = 14.sp, fontWeight = FontWeight.Medium) }, onClick = { orderType = type; expandedOrderType = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Method Dropdown
                Text(text = "PAYMENT METHOD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedPaymentMethod = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(paymentMethod, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                        }
                    }
                    DropdownMenu(expanded = expandedPaymentMethod, onDismissRequest = { expandedPaymentMethod = false }, modifier = Modifier.fillMaxWidth(0.7f)) {
                        paymentMethods.forEach { method ->
                            DropdownMenuItem(text = { Text(method, fontSize = 14.sp, fontWeight = FontWeight.Medium) }, onClick = { paymentMethod = method; expandedPaymentMethod = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "AMOUNT COLLECTED", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amountCollected, onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amountCollected = it },
                        modifier = Modifier.fillMaxWidth(), placeholder = { Text("0.00", color = Color.LightGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true,
                        shape = RoundedCornerShape(12.dp), textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, fontWeight = FontWeight.Black, fontSize = 18.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StaffPortalButtonBlue)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                DetailValueRow("Change to Return", String.format(Locale.US, "₱%,.2f", change), color = if (collected < totalAmount) TextGray else Color(0xFF2E7D32), isBold = true)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp)) { Text("Cancel", color = TextGray) }
                    Button(
                        enabled = collected >= totalAmount && totalAmount > 0,
                        onClick = { onConfirm(orderType, paymentMethod) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Text("Confirm Pay", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailsPopup(
    cart: SnapshotStateMap<Int, Int>, 
    products: SnapshotStateList<POSProductData>, 
    customer: POSCustomerData?, 
    orderType: String,
    paymentMethod: String,
    subtotal: Double,
    discount: Double,
    vat: Double,
    netOfVat: Double,
    totalAmount: Double,
    onBackClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.92f)) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Transaction Receipt", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StaffPortalButtonBlue)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                DetailValueRow("Sale ID", "SALE-1002")
                DetailValueRow("Order Type", orderType, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Payment Method", paymentMethod, isBold = true)
                DetailValueRow("Fulfillment", customer?.name ?: "Walk-in Store")
                DetailValueRow("Timestamp", "2026-01-20 02:14 PM")
                DetailValueRow("Status", "COMPLETED", color = StatusActive)
                
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF1F3F4), RoundedCornerShape(12.dp)).clip(RoundedCornerShape(12.dp))) {
                    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA)).padding(10.dp)) {
                        Text("Product", modifier = Modifier.weight(0.5f), fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray)
                        Text("Qty", modifier = Modifier.weight(0.15f), fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, textAlign = TextAlign.Center)
                        Text("Total", modifier = Modifier.weight(0.35f), fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, textAlign = TextAlign.End)
                    }
                    products.filter { cart.containsKey(it.id) }.forEach { product ->
                        val qty = cart[product.id] ?: 0
                        val lineTotal = qty * product.price
                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(product.name, modifier = Modifier.weight(0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(qty.toString(), modifier = Modifier.weight(0.15f), fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Black)
                            Text(String.format(Locale.US, "₱%,.2f", lineTotal), modifier = Modifier.weight(0.35f), fontSize = 11.sp, textAlign = TextAlign.End, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                DetailValueRow("Subtotal (VAT Inc)", String.format(Locale.US, "₱%,.2f", subtotal))
                DetailValueRow("Less Discount", String.format(Locale.US, "-₱%,.2f", discount), color = GasTrackRed)
                DetailValueRow("Less VAT (12%)", String.format(Locale.US, "₱%,.2f", vat))
                DetailValueRow("Amount Net of VAT", String.format(Locale.US, "₱%,.2f", netOfVat))
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFF1F3F4))
                
                DetailValueRow("TOTAL AMOUNT DUE", String.format(Locale.US, "₱%,.2f", totalAmount), isBold = true, color = GasTrackRed)
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Close & Print Receipt", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun HeldCartsModalDialog(
    heldCarts: SnapshotStateList<HeldCartData>,
    onDismiss: () -> Unit,
    onRestore: (HeldCartData) -> Unit,
    onDiscard: (HeldCartData) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.95f)) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Held Transactions", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (heldCarts.isEmpty()) {
                    Text("No held transactions.", fontSize = 13.sp, color = TextGray, modifier = Modifier.padding(vertical = 20.dp))
                } else {
                    heldCarts.forEach { held ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8F9FA),
                            border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(held.timestamp, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text(String.format(Locale.US, "₱%,.2f", held.totalAmount), fontSize = 13.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${held.items.values.sum()} item(s) • ${held.customerType}", fontSize = 11.sp, color = TextGray)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { onRestore(held) }, modifier = Modifier.weight(1f).height(36.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)) {
                                        Text("Restore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(onClick = { onDiscard(held) }, modifier = Modifier.weight(1f).height(36.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, GasTrackRed)) {
                                        Text("Discard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GasTrackRed)
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
fun PickupDetailsModalDialog(
    pickupDate: String,
    pickupTime: String,
    selectedCustomer: POSCustomerData?,
    customers: SnapshotStateList<POSCustomerData>,
    onCustomerSelect: (POSCustomerData) -> Unit,
    onAddNewCustomer: () -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var date by remember { mutableStateOf(pickupDate) }
    var time by remember { mutableStateOf(pickupTime) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Automatically track updates to the selectedCustomer from the parent (e.g. when a new one is added)
    var currentSelectedCustomer by remember(selectedCustomer) { mutableStateOf(selectedCustomer) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val filteredCustomers = if (searchQuery.trim().isEmpty()) {
        emptyList()
    } else {
        customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
        }
    }

    // Native Calendar Date Picker Dialog
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = String.format(Locale.US, "%02d/%02d/%04d", month + 1, dayOfMonth, year)
        },
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )

    // Native Clock Time Picker Dialog
    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val amPm = if (hourOfDay >= 12) "PM" else "AM"
            val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            time = String.format(Locale.US, "%02d:%02d %s", hour12, minute, amPm)
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        false
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .shadow(12.dp, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pickup Details",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDark,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Add New Customer Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(
                        onClick = onAddNewCustomer,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add New Customer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                // Search Customer Section
                Text(
                    text = "Search Customer",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Customer...", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedBorderColor = StaffPortalButtonBlue
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                )

                // Search Results Dropdown List
                if (filteredCustomers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Column {
                            filteredCustomers.forEach { cust ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentSelectedCustomer = cust
                                            onCustomerSelect(cust)
                                            searchQuery = ""
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(cust.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(cust.phone, fontSize = 10.sp, color = TextGray)
                                    }
                                    Surface(
                                        color = StaffPortalButtonBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Select", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StaffPortalButtonBlue)
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFE5E7EB))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SELECTED CUSTOMER Card Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, EmployeePortalBackground)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SELECTED CUSTOMER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSelectedCustomer?.name ?: "No customer selected",
                            fontSize = 13.sp,
                            fontWeight = if (currentSelectedCustomer != null) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentSelectedCustomer != null) TextDark else Color(0xFF94A3B8)
                        )
                        Text(
                            text = currentSelectedCustomer?.phone ?: "—",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pickup Schedule Section
                Text(
                    text = "Pickup Schedule",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date Column
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Date", fontSize = 11.sp, color = TextGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("mm/dd/yyyy", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", tint = TextDark, modifier = Modifier.size(16.dp))
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFD1D5DB),
                                focusedBorderColor = StaffPortalButtonBlue
                            ),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        )
                    }

                    // Time Column
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Time (8 AM – 6 PM)", fontSize = 11.sp, color = TextGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = time,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("--:-- --", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                            trailingIcon = {
                                IconButton(onClick = { timePickerDialog.show() }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Schedule, contentDescription = "Select Time", tint = TextDark, modifier = Modifier.size(16.dp))
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { timePickerDialog.show() },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFD1D5DB),
                                focusedBorderColor = StaffPortalButtonBlue
                            ),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (currentSelectedCustomer != null) {
                                onCustomerSelect(currentSelectedCustomer!!)
                            }
                            onSave(date, time)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Save Pickup Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryDetailsModalDialog(
    deliveryAddress: String,
    instructions: String,
    vehicleType: String,
    assignedRider: String,
    selectedCustomer: POSCustomerData?,
    customers: SnapshotStateList<POSCustomerData>,
    onCustomerSelect: (POSCustomerData) -> Unit,
    onAddNewCustomer: () -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var addr by remember(selectedCustomer) { mutableStateOf(deliveryAddress.ifEmpty { selectedCustomer?.address ?: "" }) }
    var inst by remember { mutableStateOf(instructions) }
    var vehicle by remember { mutableStateOf(vehicleType) }
    var rider by remember { mutableStateOf(assignedRider) }
    var searchQuery by remember { mutableStateOf("") }
    var currentSelectedCustomer by remember(selectedCustomer) { mutableStateOf(selectedCustomer) }

    var expandedVehicle by remember { mutableStateOf(false) }
    var expandedRider by remember { mutableStateOf(false) }

    val vehicles = listOf("Motor", "Tricycle", "Truck")
    val riders = listOf("EMP-005 (Juan)", "EMP-006 (Maria)", "EMP-007 (Andres)")

    val filteredCustomers = if (searchQuery.trim().isEmpty()) {
        emptyList()
    } else {
        customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .shadow(12.dp, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = StaffPortalButtonBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Delivery Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDark,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Customer Section
                Text(
                    text = "Search Customer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Customer...", color = Color(0xFF9CA3AF), fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedBorderColor = StaffPortalButtonBlue
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium)
                )

                // Search Results Dropdown List
                if (filteredCustomers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Column {
                            filteredCustomers.forEach { cust ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentSelectedCustomer = cust
                                            onCustomerSelect(cust)
                                            addr = cust.address // Auto-fill address
                                            searchQuery = ""
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(cust.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(cust.phone, fontSize = 10.sp, color = TextGray)
                                    }
                                    Surface(
                                        color = StaffPortalButtonBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Select", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StaffPortalButtonBlue)
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFE5E7EB))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add New Customer Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(
                        onClick = onAddNewCustomer,
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = StaffPortalButtonBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add New Customer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StaffPortalButtonBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SELECTED CUSTOMER Card Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, EmployeePortalBackground)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SELECTED CUSTOMER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSelectedCustomer?.name ?: "No customer selected",
                            fontSize = 12.sp,
                            fontWeight = if (currentSelectedCustomer != null) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentSelectedCustomer != null) TextDark else Color(0xFF94A3B8)
                        )
                        Text(
                            text = currentSelectedCustomer?.phone ?: "—",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Complete Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("*", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GasTrackRed)
                }
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = addr, onValueChange = { addr = it },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    placeholder = { Text("House No., Street, Barangay, City, Landmark", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = StaffPortalButtonBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Delivery Instructions (Optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = inst, onValueChange = { inst = it },
                    modifier = Modifier.fillMaxWidth(), placeholder = { Text("e.g. Leave at guard house, call upon arrival", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = StaffPortalButtonBlue
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vehicle Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedVehicle = true },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(vehicle, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Normal)
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
                                }
                            }
                            DropdownMenu(expanded = expandedVehicle, onDismissRequest = { expandedVehicle = false }, modifier = Modifier.background(Color.White)) {
                                vehicles.forEach { v ->
                                    DropdownMenuItem(text = { Text(v, fontSize = 13.sp) }, onClick = { vehicle = v; expandedVehicle = false })
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assign Rider", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedRider = true },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(rider, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Normal, maxLines = 1)
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
                                }
                            }
                            DropdownMenu(expanded = expandedRider, onDismissRequest = { expandedRider = false }, modifier = Modifier.background(Color.White)) {
                                riders.forEach { r ->
                                    DropdownMenuItem(text = { Text(r, fontSize = 13.sp) }, onClick = { rider = r; expandedRider = false })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (currentSelectedCustomer != null) {
                                onCustomerSelect(currentSelectedCustomer!!)
                            }
                            onSave(addr, inst, vehicle, rider)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Save Delivery Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomerDialog(
    customer: POSCustomerData? = null,
    onDismiss: () -> Unit, 
    onAdd: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var phone by remember { mutableStateOf(customer?.phone ?: "") }
    var address by remember { mutableStateOf(customer?.address ?: "") }
    var customerType by remember { mutableStateOf(customer?.customerType ?: "Residential") }
    var expandedCustomerType by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .shadow(12.dp, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (customer == null) "Add Customer" else "Edit Customer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Customer Name
                Column {
                    Text("Customer Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Name", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = StaffPortalButtonBlue
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number
                Column {
                    Text("Phone Number", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Phone Number", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = StaffPortalButtonBlue
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Address
                Column {
                    Text("Address", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Street, Brgy, City, Province, Zip Code", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = StaffPortalButtonBlue
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                // Customer Type Dropdown
                Column {
                    Text("Customer Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = customerType,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedCustomerType = true },
                            trailingIcon = {
                                IconButton(onClick = { expandedCustomerType = true }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFD1D5DB),
                                focusedBorderColor = StaffPortalButtonBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        )
                        DropdownMenu(
                            expanded = expandedCustomerType,
                            onDismissRequest = { expandedCustomerType = false },
                            modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
                        ) {
                            listOf("Residential", "Commercial").forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        customerType = type
                                        expandedCustomerType = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        enabled = name.isNotEmpty() && phone.isNotEmpty(),
                        onClick = { onAdd(name, phone, address, customerType, customer?.status ?: "Active") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ViewCustomerDialog(customer: POSCustomerData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(), 
            shape = RoundedCornerShape(16.dp), 
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(50.dp), shape = RoundedCornerShape(12.dp), color = StaffPortalButtonBlue.copy(alpha = 0.1f)) {
                            Box(contentAlignment = Alignment.Center) { Text(text = customer.initials, color = StaffPortalButtonBlue, fontWeight = FontWeight.Black, fontSize = 18.sp) }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = customer.name, fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark)
                            Text(text = "Customer ID: C-${customer.id.toString().padStart(4, '0')}", fontSize = 11.sp, color = TextGray)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailValueRow("Customer Type", customer.customerType, isBold = true)
                DetailValueRow("Contact Number", customer.phone)
                DetailValueRow("Complete Address", customer.address)
                DetailValueRow("Account Status", customer.status, color = if (customer.status == "Active") StatusActive else StatusInactive)
                DetailValueRow("Registered By", customer.registeredBy)
                DetailValueRow("Last Transaction", customer.lastOrder)

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close Summary", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun POSEmployeeScreenPreview() {
    POSEmployeeScreen()
}
