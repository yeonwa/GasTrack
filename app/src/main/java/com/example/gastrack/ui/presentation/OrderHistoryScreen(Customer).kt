package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

data class OrderItem(
    val id: String,
    val date: String,
    val status: String,
    val price: String,
    val isDelivered: Boolean,
    val itemCount: Int = 1,
    val productName: String = "11kg Standard Refill",
    val imageRes: Int = R.drawable.tank_11kg,
    val deliveryAddress: String = "123 Sampaguita St., Brgy. San Jose, Quezon City",
    val paymentMethod: String = "GCash",
    val subtotal: String = "₱900.00",
    val deliveryFee: String = "₱50.00",
    val recipientName: String = "Juan Dela Cruz",
    val recipientPhone: String = "+63 917 123 4567"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onBack: () -> Unit,
    onOrderClick: (OrderItem) -> Unit = {},
    onReorder: (OrderItem) -> Unit = {}
) {
    var selectedOrderForDetail by remember { mutableStateOf<OrderItem?>(null) }

    val orders = remember {
        mutableStateListOf(
            OrderItem(
                id = "GT-98231",
                date = "Oct 24, 2023",
                status = "Delivered",
                price = "₱950.00",
                isDelivered = true,
                itemCount = 1,
                productName = "11kg Standard Refill",
                imageRes = R.drawable.tank_11kg,
                deliveryAddress = "123 Sampaguita St., Brgy. San Jose, Quezon City",
                paymentMethod = "GCash",
                subtotal = "₱900.00",
                deliveryFee = "₱50.00"
            ),
            OrderItem(
                id = "GT-97120",
                date = "Today",
                status = "On the Way",
                price = "₱1,840.00",
                isDelivered = false,
                itemCount = 2,
                productName = "2.7kg Compact Cylinder",
                imageRes = R.drawable.tank_2_7kg,
                deliveryAddress = "Unit 4B, Floral Palms, Quezon City",
                paymentMethod = "Cash on Delivery",
                subtotal = "₱1,790.00",
                deliveryFee = "₱50.00"
            ),
            OrderItem(
                id = "GT-95011",
                date = "Aug 05, 2023",
                status = "Cancelled",
                price = "₱4,500.00",
                isDelivered = false,
                itemCount = 1,
                productName = "50kg Industrial Cylinder",
                imageRes = R.drawable.tank_50kg,
                deliveryAddress = "123 Sampaguita St., Brgy. San Jose, Quezon City",
                paymentMethod = "GCash",
                subtotal = "₱4,450.00",
                deliveryFee = "₱50.00"
            )
        )
    }

    val handleConfirmReceived: (OrderItem) -> Unit = { targetOrder ->
        val index = orders.indexOfFirst { it.id == targetOrder.id }
        if (index != -1) {
            val updated = orders[index].copy(status = "Delivered", isDelivered = true)
            orders[index] = updated
            if (selectedOrderForDetail?.id == targetOrder.id) {
                selectedOrderForDetail = updated
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GasTrackBlue, StaffPortalBlue)
                        )
                    )
                    .statusBarsPadding()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Order History",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderHistoryCard(
                    order = order,
                    onOrderClick = { selectedOrder ->
                        selectedOrderForDetail = selectedOrder
                        onOrderClick(selectedOrder)
                    },
                    onConfirmReceived = handleConfirmReceived,
                    onReorder = onReorder
                )
            }
        }

        // Full Details Modal Bottom Sheet
        selectedOrderForDetail?.let { detailOrder ->
            ModalBottomSheet(
                onDismissRequest = { selectedOrderForDetail = null },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order Details",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark
                            )
                            Text(
                                text = "Order #${detailOrder.id}",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }

                        IconButton(onClick = { selectedOrderForDetail = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OrderDetailCustomerScreen(
                        order = detailOrder,
                        onBack = { selectedOrderForDetail = null },
                        onConfirmReceived = handleConfirmReceived,
                        onReorder = { reorderItem ->
                            selectedOrderForDetail = null
                            onReorder(reorderItem)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: OrderItem,
    onOrderClick: (OrderItem) -> Unit = {},
    onConfirmReceived: (OrderItem) -> Unit = {},
    onReorder: (OrderItem) -> Unit = {}
) {
    val statusBg = when {
        order.isDelivered -> Color(0xFFE8F5E9)
        order.status.equals("Cancelled", ignoreCase = true) -> Color(0xFFFFEBEE)
        else -> Color(0xFFEBF3FF)
    }

    val statusTextClr = when {
        order.isDelivered -> Color(0xFF1B5E20)
        order.status.equals("Cancelled", ignoreCase = true) -> Color(0xFFB71C1C)
        else -> GasTrackBlue
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick(order) }
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.date,
                        fontSize = 13.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = order.status.uppercase(),
                        color = statusTextClr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF1F3F4),
                thickness = 1.dp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = order.productName,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${order.itemCount} ${if (order.itemCount == 1) "Item" else "Items"}",
                            fontSize = 12.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.price,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = StaffPortalBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { onOrderClick(order) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.NavigateNext,
                            contentDescription = "Details",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (!order.isDelivered && !order.status.equals("Cancelled", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onConfirmReceived(order) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Confirm Order Received",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else if (order.isDelivered) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = { onReorder(order) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GasTrackBlue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GasTrackBlue
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reorder",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderHistoryScreenPreview() {
    OrderHistoryScreen(onBack = {})
}
