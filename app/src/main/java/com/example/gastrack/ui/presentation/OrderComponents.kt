package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import java.util.Locale

// Unified, shared lifecycle runtime single state object for client persistence
object SharedCartState {
    val cart = mutableStateMapOf<Int, Int>()
}

// Master Data class for all LPG products in the system
@Stable
data class POSProductData(
    val id: Int,
    val name: String,
    val price: Double,
    val imageRes: Int,
    val stock: Int,
    val brand: String,
    val category: String,
    val warehouseName: String = "Pasig Warehouse",
    val isPopular: Boolean = false
)

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val imageRes: Int,
    val stock: Int,
    val brand: String,
    val isPopular: Boolean = false,
)

@Composable
fun ProductCard(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onBuyNow: () -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = StaffPortalBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp),
                    ) {
                        Text(
                            text = product.brand.uppercase(Locale.US),
                            color = StaffPortalBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            letterSpacing = 0.5.sp,
                        )
                    }
                    if (product.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = GasTrackRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp),
                        ) {
                            Text(
                                text = "POPULAR",
                                color = GasTrackRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                letterSpacing = 0.5.sp,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val formattedPrice = String.format(Locale.US, "%,d", product.price)
                    Text(
                        text = "₱$formattedPrice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark,
                    )

                    if (quantity > 0) {
                        QuantitySelector(
                            quantity = quantity,
                            onIncrement = { onQuantityChange(quantity + 1) },
                            onDecrement = { onQuantityChange(quantity - 1) },
                        )
                    } else {
                        OutlinedIconButton(
                            onClick = { onQuantityChange(1) },
                            modifier = Modifier
                                .size(40.dp)
                                .semantics { contentDescription = "Add ${product.name} to cart" },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, StaffPortalBlue),
                            colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = StaffPortalBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Add to cart",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
    ) {
        IconButton(
            onClick = onDecrement,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = StaffPortalBlue, modifier = Modifier.size(16.dp))
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center,
        )
        IconButton(
            onClick = onIncrement,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", tint = StaffPortalBlue, modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        ProductCard(
            product = Product(
                id = 1,
                name = "11kg Standard Refill",
                description = "Perfect for household cooking.",
                price = 950,
                imageRes = R.drawable.tank_11kg,
                stock = 15,
                brand = "Petron",
                isPopular = true,
            ),
            quantity = 0,
            onQuantityChange = {},
        )
    }
}
