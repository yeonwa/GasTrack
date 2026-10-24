package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onPlaceOrder: () -> Unit,
    onCancel: () -> Unit = {},
    onNavigateToSuccess: () -> Unit = {},
    onNavigateToAddresses: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
) {
    // Input parameters for Customer Information
    val customerName = "Althea De Guzman"
    val contactNumber = "09123456789"
    val deliveryAddress = "45 Mabini St, Brgy. Concepcion, Malabon City"

    // Order Type state: "Pickup" or "Delivery"
    var orderType by remember { mutableStateOf("Delivery") }

    // Payment Method state: "Cash", "GCash", "Card"
    var paymentMethod by remember { mutableStateOf("GCash") }

    // Connect runtime cart state dynamically to map the items added by the user
    val cart = SharedCartState.cart

    // High fidelity global product reference directory map
    val allProductsReference = listOf(
        Product(1, "11kg Standard Refill", "Refill only", 1150, R.drawable.tank_11kg, 20, "Petron"),
        Product(2, "2.7kg Camping Refill", "Portable size", 350, R.drawable.tank_2_7kg, 15, "Petron"),
        Product(3, "50kg Commercial Refill", "Bulk usage", 8500, R.drawable.tank_50kg, 10, "Petron"),
        Product(4, "11kg Standard Tank", "New cylinder", 1150, R.drawable.tank_11kg, 20, "Solane"),
        Product(5, "2.7kg Camping Tank", "Small cylinder", 350, R.drawable.tank_2_7kg, 15, "M-Gas"),
        Product(6, "50kg Commercial Tank", "Heavy duty", 8500, R.drawable.tank_50kg, 10, "Phoenix")
    )

    // Filter out only active products currently held inside the shared cart state
    val checkoutItems = cart.entries.mapNotNull { entry ->
        val product = allProductsReference.find { it.id == entry.key }
        if (product != null) Pair(product, entry.value) else null
    }

    // Dynamic calculations tied directly to live user selection memory
    val subtotal = checkoutItems.sumOf { it.first.price * it.second }
    val deliveryCharge = if (checkoutItems.isEmpty() || orderType == "Pickup") 0 else 50
    val totalAmount = subtotal + deliveryCharge

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // ================= CUSTOMER INFORMATION =================
                Text(
                    text = "CUSTOMER INFORMATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Customer Name", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                Text(customerName, fontSize = 16.sp, color = TextDark, fontWeight = FontWeight.Black)
                            }
                            Text(
                                text = "Change",
                                color = Color(0xFFE74C3C),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .clickable { onNavigateToProfile() }
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF5F5F5))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Contact Number", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                Text(contactNumber, fontSize = 16.sp, color = TextDark, fontWeight = FontWeight.Black)
                            }
                            Text(
                                text = "Change",
                                color = Color(0xFFE74C3C),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .clickable { onNavigateToProfile() }
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF5F5F5))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Delivery Address", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (orderType == "Delivery") deliveryAddress else "Store Pickup Selected",
                                    fontSize = 15.sp,
                                    color = if (orderType == "Delivery") TextDark else Color.Gray,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            if (orderType == "Delivery") {
                                Text(
                                    text = "Change",
                                    color = Color(0xFFE74C3C),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clickable { onNavigateToAddresses() }
                                        .padding(vertical = 4.dp, horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ================= ORDER TYPE (PICKUP / DELIVERY) =================
                Text(
                    text = "ORDER TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val isDelivery = orderType == "Delivery"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .clickable { orderType = "Delivery" },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDelivery) ButtonOrange.copy(alpha = 0.08f) else Color.White,
                        border = BorderStroke(1.5.dp, if (isDelivery) ButtonOrange else Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = if (isDelivery) ButtonOrange else TextGray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delivery", fontWeight = FontWeight.Bold, color = if (isDelivery) ButtonOrange else TextDark, fontSize = 15.sp)
                        }
                    }

                    val isPickup = orderType == "Pickup"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .clickable { orderType = "Pickup" },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isPickup) ButtonOrange.copy(alpha = 0.08f) else Color.White,
                        border = BorderStroke(1.5.dp, if (isPickup) ButtonOrange else Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = if (isPickup) ButtonOrange else TextGray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pickup", fontWeight = FontWeight.Bold, color = if (isPickup) ButtonOrange else TextDark, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ================= ORDER INFORMATION =================
                Text(
                    text = "ORDER INFORMATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (checkoutItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No items in cart", color = TextGray, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        } else {
                            checkoutItems.forEach { (item, qty) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .background(Color(0xFFF7F8FA), RoundedCornerShape(10.dp))
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(painter = painterResource(id = item.imageRes), contentDescription = null, modifier = Modifier.fillMaxSize())
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                                        Text("Qty: $qty × ₱${String.format(Locale.US, "%,d", item.price)}", fontSize = 13.sp, color = TextGray)
                                    }
                                    Text(
                                        text = "₱${String.format(Locale.US, "%,d", item.price * qty)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = TextDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFF2F2F2))
                        Spacer(modifier = Modifier.height(14.dp))

                        CheckoutSummaryRow("Subtotal", String.format(Locale.US, "₱%,d", subtotal))
                        CheckoutSummaryRow("Delivery Charge", String.format(Locale.US, "₱%,d", deliveryCharge))
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount", fontWeight = FontWeight.Black, fontSize = 17.sp, color = TextDark)
                            Text("₱${String.format(Locale.US, "%,d", totalAmount)}", fontWeight = FontWeight.Black, fontSize = 22.sp, color = ButtonOrange)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ================= PAYMENT METHOD =================
                Text(
                    text = "PAYMENT METHOD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CheckoutPaymentRow(
                        title = "Cash",
                        isSelected = paymentMethod == "Cash",
                        icon = { Icon(Icons.Default.Payments, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.padding(8.dp)) },
                        onClick = { paymentMethod = "Cash" }
                    )

                    CheckoutPaymentRow(
                        title = "GCash",
                        isSelected = paymentMethod == "GCash",
                        icon = { 
                            Image(
                                painter = painterResource(id = R.drawable.gcash_logo), 
                                contentDescription = null, 
                                modifier = Modifier.padding(6.dp).fillMaxSize()
                            ) 
                        },
                        onClick = { paymentMethod = "GCash" }
                    )

                    CheckoutPaymentRow(
                        title = "Card",
                        isSelected = paymentMethod == "Card",
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.padding(8.dp)) },
                        onClick = { paymentMethod = "Card" }
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // ================= OPERATION BUTTONS ACTION ZONE =================
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            onPlaceOrder()
                            onNavigateToSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange)
                    ) {
                        Text("Place Order", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            onCancel()
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFE74C3C)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE74C3C))
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Ext-helper to keep clean layout boundaries

@Composable
fun CheckoutSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextGray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(text = value, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun CheckoutPaymentRow(
    title: String,
    isSelected: Boolean,
    icon: @Composable BoxScope.() -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, if (isSelected) ButtonOrange else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF7F8FA)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { icon() }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.weight(1f))
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = ButtonOrange)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    CheckoutScreen(onBack = {}, onPlaceOrder = {})
}
