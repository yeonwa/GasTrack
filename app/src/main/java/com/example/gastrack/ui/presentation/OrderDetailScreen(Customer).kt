package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailCustomerScreen(
    order: OrderItem,
    onBack: () -> Unit,
    onConfirmReceived: (OrderItem) -> Unit = {},
    onReorder: (OrderItem) -> Unit = {}
) {
    val isDelivered = order.isDelivered
    val isCancelled = order.status.equals("Cancelled", ignoreCase = true)

    val statusBgColor = when {
        isDelivered -> Color(0xFFE8F5E9)
        isCancelled -> Color(0xFFFFEBEE)
        else -> Color(0xFFEBF3FF)
    }

    val statusTextColor = when {
        isDelivered -> Color(0xFF1B5E20)
        isCancelled -> Color(0xFFB71C1C)
        else -> GasTrackBlue
    }

    val statusIcon = when {
        isDelivered -> Icons.Default.CheckCircle
        isCancelled -> Icons.Default.Cancel
        else -> Icons.Default.LocalShipping
    }

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Order Details",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Order #${order.id}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(GasTrackBlue, StaffPortalBlue)
                    )
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = statusBgColor
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusTextColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Status: ${order.status}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusTextColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Order Date: ${order.date}",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                    }
                }
            }

            // Delivery Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = GasTrackBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delivery Address",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF1F3F4))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = order.recipientName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.recipientPhone,
                        fontSize = 13.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.deliveryAddress,
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }
            }

            // Purchased Item Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Items Ordered",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF1F3F4))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = order.imageRes),
                                contentDescription = order.productName,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = order.productName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Qty: ${order.itemCount}",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }

                        Text(
                            text = order.price,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDark
                        )
                    }
                }
            }

            // Payment Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Payment,
                            contentDescription = null,
                            tint = GasTrackBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Payment Summary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF1F3F4))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal", fontSize = 13.sp, color = TextGray)
                        Text(text = order.subtotal, fontSize = 13.sp, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Delivery Fee", fontSize = 13.sp, color = TextGray)
                        Text(text = order.deliveryFee, fontSize = 13.sp, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Payment Method", fontSize = 13.sp, color = TextGray)
                        Text(text = order.paymentMethod, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF1F3F4))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total Amount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(text = order.price, fontSize = 20.sp, fontWeight = FontWeight.Black, color = StaffPortalBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Confirm Received / Reorder Button
            if (!isDelivered && !isCancelled) {
                Button(
                    onClick = { onConfirmReceived(order) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm Order Received",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else if (isDelivered) {
                Button(
                    onClick = { onReorder(order) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GasTrackBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reorder This Item",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailCustomerScreenPreview() {
    OrderDetailCustomerScreen(
        order = OrderItem(
            id = "GT-98231",
            date = "Oct 24, 2023",
            status = "Delivered",
            price = "₱950.00",
            isDelivered = true,
            itemCount = 1,
            productName = "11kg Standard Refill",
            imageRes = R.drawable.tank_11kg
        ),
        onBack = {}
    )
}
