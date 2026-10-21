package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingCart
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
fun OrderTabScreen(
    initialTab: String = "New Tank",
    onNavigateToHome: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToMenu: () -> Unit,
) {
    // Search and Filter States
    var selectedTab by remember { mutableStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("All") }
    val brands = listOf("All", "Petron", "Solane", "M-Gas", "Phoenix")

    // Persistent runtime state reference
    val cart = SharedCartState.cart

    // Master Product List
    val allProducts = listOf(
        Product(1, "11kg Standard Refill", "Perfect for household cooking. Lasts 1 month.", 1150, R.drawable.tank_11kg, 20, "Petron", isPopular = true),
        Product(2, "2.7kg Camping Refill", "Portable size for outdoor grills and stoves.", 350, R.drawable.tank_2_7kg, 15, "Petron"),
        Product(3, "50kg Commercial Refill", "For restaurants and commercial kitchens.", 8500, R.drawable.tank_50kg, 10, "Petron"),
        Product(4, "11kg Standard Tank", "New cylinder, perfect for household cooking.", 1150, R.drawable.tank_11kg, 20, "Solane", isPopular = true),
        Product(5, "2.7kg Camping Tank", "Small cylinder, portable size for outdoor use.", 350, R.drawable.tank_2_7kg, 15, "M-Gas"),
        Product(6, "50kg Commercial Tank", "Heavy duty cylinder for bulk usage.", 8500, R.drawable.tank_50kg, 10, "Phoenix")
    )

    // Dynamic Filtering Logic
    val filteredProducts = allProducts.filter { product ->
        val matchesCategory = if (selectedTab == "Refill") product.name.contains("Refill") else product.name.contains("Tank")
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
        val matchesBrand = if (selectedBrand == "All") true else product.brand == selectedBrand
        matchesCategory && matchesSearch && matchesBrand
    }

    val totalItems = cart.values.sum()
    val totalPrice = cart.entries.sumOf { entry ->
        val product = allProducts.find { it.id == entry.key }
        (product?.price ?: 0) * entry.value
    }
    val formattedTotalPrice = String.format(Locale.US, "₱%,d", totalPrice)

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "order",
                onHomeClick = onNavigateToHome,
                onMenuClick = onNavigateToMenu,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // --- Header: Logo on left, "Order LPG" small on right ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Image(painter = painterResource(id = R.drawable.favicon), contentDescription = null, modifier = Modifier.size(24.dp))
                        Text(text = "GasTrack", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GasTrackBlue)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Order LPG", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        IconButton(
                            onClick = { onNavigateToCheckout() },
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = TextDark,
                                    modifier = Modifier.size(26.dp)
                                )
                                if (totalItems > 0) {
                                    Surface(
                                        color = GasTrackRed,
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(8.dp)
                                            .align(Alignment.TopEnd)
                                            .offset(x = 2.dp, y = (-2).dp)
                                    ) {}
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Search Product Section ---
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Product", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalBlue) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue,
                    ),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // --- Filter By Category Section ---
                Text(text = "FILTER BY CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(50.dp).background(Color(0xFFEFEFEF), RoundedCornerShape(25.dp)).padding(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        val isRefillSelected = selectedTab == "Refill"
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().shadow(if (isRefillSelected) 4.dp else 0.dp, RoundedCornerShape(22.dp))
                                .background(if (isRefillSelected) Color.White else Color.Transparent, RoundedCornerShape(22.dp))
                                .clickable { selectedTab = "Refill" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Refill", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isRefillSelected) TextDark else Color.Gray)
                        }

                        val isNewTankSelected = selectedTab == "New Tank"
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().shadow(if (isNewTankSelected) 4.dp else 0.dp, RoundedCornerShape(22.dp))
                                .background(if (isNewTankSelected) Color.White else Color.Transparent, RoundedCornerShape(22.dp))
                                .clickable { selectedTab = "New Tank" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "New Tank", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isNewTankSelected) TextDark else Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Filter By Brand Section ---
                Text(text = "FILTER BY BRAND", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedBrand,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE9ECEF),
                            focusedBorderColor = GasTrackBlue
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        brands.forEach { brand ->
                            DropdownMenuItem(
                                text = { Text(brand) },
                                onClick = {
                                    selectedBrand = brand
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Product List ---
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 90.dp),
                ) {
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            quantity = cart[product.id] ?: 0,
                            onQuantityChange = { qty ->
                                if (qty <= 0) cart.remove(product.id) else cart[product.id] = qty
                            },
                            onBuyNow = {
                                cart[product.id] = 1
                                onNavigateToCheckout()
                            }
                        )
                    }
                }
            }

            // Bottom View Cart Bar
            if (totalItems > 0) {
                Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Button(
                        onClick = onNavigateToCheckout,
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.25f), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(text = totalItems.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(text = "View Cart", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                            Text(text = formattedTotalPrice, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderTabScreenPreview() {
    OrderTabScreen(onNavigateToHome = {}, onNavigateToCheckout = {}, onNavigateToMenu = {})
}
