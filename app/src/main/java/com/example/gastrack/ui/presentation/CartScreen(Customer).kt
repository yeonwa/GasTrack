package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import java.util.Locale

data class CartItemData(
    val id: Int,
    val name: String,
    val price: Int,
    val imageRes: Int,
    val quantity: Int
)

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    // Shared Runtime state connection mapping
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

    // Build the dynamic cart contents directly out of the live additions
    val currentCartItems = cart.entries.mapNotNull { entry ->
        val product = allProductsReference.find { it.id == entry.key }
        if (product != null) {
            CartItemData(
                id = product.id,
                name = product.name,
                price = product.price,
                imageRes = product.imageRes,
                quantity = entry.value
            )
        } else null
    }

    val subtotal = currentCartItems.sumOf { it.price * it.quantity }
    val deliveryCharge = if (currentCartItems.isEmpty()) 0 else 50
    val totalAmount = subtotal + deliveryCharge
    val totalItems = currentCartItems.sumOf { it.quantity }

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GasTrackBlue, StaffPortalBlue)
                        )
                    )
                    .padding(top = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Cart Summary",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = {
            if (currentCartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = "Proceed to Checkout",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentCartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Your cart is empty", color = TextGray, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(onClick = onContinueShopping) {
                            Text("Continue Shopping", color = GasTrackBlue, fontWeight = FontWeight.Black)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(currentCartItems) { item ->
                        CartItemCard(
                            item = item,
                            onQuantityChange = { newQty ->
                                if (newQty <= 0) {
                                    cart.remove(item.id)
                                } else {
                                    cart[item.id] = newQty
                                }
                            },
                            onRemove = { cart.remove(item.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(24.dp)),
                            color = Color.White,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "ORDER SUMMARY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextGray,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Total Items", fontSize = 14.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                    Text(text = totalItems.toString(), fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Subtotal", fontSize = 14.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                    Text(text = "₱${String.format(Locale.US, "%,d", subtotal)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Delivery Charge", fontSize = 14.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                    Text(text = "₱${String.format(Locale.US, "%,d", deliveryCharge)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Discount", fontSize = 14.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                    Text(text = "-₱0", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFFF1F3F4))
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Total Amount", fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextDark)
                                    Text(
                                        text = "₱${String.format(Locale.US, "%,d", totalAmount)}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ButtonOrange
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = onContinueShopping) {
                                Text("Continue Shopping", color = GasTrackBlue, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { cart.clear() }) {
                                Text("Clear Cart", color = GasTrackRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemData,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        color = Color.White,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8F9FA)
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp).fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                    Text(text = "Unit Price: ₱${item.price}", fontSize = 13.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Subtotal: ₱${String.format(Locale.US, "%,d", item.price * item.quantity)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ButtonOrange
                    )
                }
                
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = GasTrackRed)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantitySelector(
                    quantity = item.quantity,
                    onIncrement = { onQuantityChange(item.quantity + 1) },
                    onDecrement = { onQuantityChange(item.quantity - 1) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    CartScreen(onBack = {}, onNavigateToCheckout = {}, onContinueShopping = {})
}
