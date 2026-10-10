package com.example.gastrack.ui.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MapTrackingScreen(
    onBack: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isArrived by remember { mutableStateOf(value = false) }
    var showDetailedView by remember { mutableStateOf(value = false) }

    // State parameters for synchronized clock indices
    var orderPrepTimeText by remember { mutableStateOf("") }
    var outForDeliveryTimeText by remember { mutableStateOf("") }
    var estimatedArrivalTimeText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        
        calendar.add(Calendar.MINUTE, -30)
        orderPrepTimeText = timeFormat.format(calendar.time)
        
        calendar.add(Calendar.MINUTE, 15)
        outForDeliveryTimeText = timeFormat.format(calendar.time)
        
        calendar.add(Calendar.MINUTE, 30)
        estimatedArrivalTimeText = timeFormat.format(calendar.time)
    }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(100)
            progress += 0.005f
        }
        isArrived = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- BASE MAP VIEW ---
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE5E7E9))) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val start = center + Offset(-200f, 300f)
                val end = center + Offset(100f, -200f)
                drawLine(color = Color.LightGray, start = start, end = end, strokeWidth = 12f)
                drawLine(color = StaffPortalBlue, start = start, end = start + (end - start) * progress, strokeWidth = 12f)
            }
            
            // Customer Pin
            Surface(
                modifier = Modifier.align(Alignment.Center).offset(x = 50.dp, y = (-100).dp),
                shape = CircleShape, color = GasTrackRed, shadowElevation = 8.dp
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }

            // Moving Driver Pin
            val driverXOffset = (-100).dp + (150).dp * progress
            val driverYOffset = (150).dp - (250).dp * progress
            Surface(
                modifier = Modifier.align(Alignment.Center).offset(x = driverXOffset, y = driverYOffset),
                shape = CircleShape, color = StaffPortalBlue, shadowElevation = 12.dp, border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp).size(20.dp))
            }

            // Back Button
            Surface(
                modifier = Modifier.padding(24.dp).size(48.dp).shadow(8.dp, CircleShape).clickable { onBack() },
                shape = CircleShape, color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                }
            }

            // Estimated Arrival Card
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 90.dp, start = 24.dp, end = 24.dp).fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp), color = Color.White
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = if (isArrived) "ORDER ARRIVED" else "ESTIMATED ARRIVAL", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isArrived) GasTrackRed else TextGray, letterSpacing = 1.sp)
                        Text(text = if (isArrived) "Arrived" else estimatedArrivalTimeText.ifEmpty { "Calculating..." }, fontSize = 32.sp, fontWeight = FontWeight.Black, color = StaffPortalBlue)
                    }
                    Surface(shape = CircleShape, color = if (isArrived) Color.Green.copy(alpha = 0.1f) else ButtonOrange.copy(alpha = 0.1f), modifier = Modifier.size(56.dp)) {
                        Icon(imageVector = if (isArrived) Icons.Default.CheckCircle else Icons.Default.Schedule, contentDescription = null, tint = if (isArrived) Color.Green else ButtonOrange, modifier = Modifier.padding(14.dp).fillMaxSize())
                    }
                }
            }

            // Bottom Driver Details Card
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().shadow(24.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFFE9ECEF), CircleShape).align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = if (isArrived) "Order Delivered" else "Out for Delivery", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextDark)
                            Text(text = if (isArrived) "Please check your delivery" else "Juan is on his way to you", fontSize = 15.sp, color = TextGray, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = { showDetailedView = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GasTrackBlue.copy(alpha = 0.1f), contentColor = GasTrackBlue),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("View Details", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        TrackingStep(title = "Order Prepared", time = orderPrepTimeText.ifEmpty { "01:30 PM" }, isCompleted = true, isLast = false)
                        TrackingStep(title = "Out for Delivery", time = outForDeliveryTimeText.ifEmpty { "01:45 PM" }, isCompleted = true, isLast = true, isProcessing = !isArrived)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // --- DETAILED ORDER VIEW OVERLAY ---
        AnimatedVisibility(
            visible = showDetailedView,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            OrderDetailsOverlay(
                isArrived = isArrived,
                onClose = { showDetailedView = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsOverlay(
    isArrived: Boolean,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFBFBFE)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            CenterAlignedTopAppBar(
                title = { Text("Order and Delivery", fontWeight = FontWeight.Black, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(2.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION: ORDER ---
                DetailsSectionHeader("ORDER")
                DetailsCard {
                    DetailRow("OrderID", "ORD-20231024-001")
                    DetailRow("OrderNo", "2023102401")
                    DetailRow("OrderDate", "Oct 24, 2023")
                    DetailRow("OrderType", "Delivery")
                    DetailRow("OrderStatus", if (isArrived) "Delivered" else "In Transit", color = if (isArrived) Color(0xFF2E7D32) else ButtonOrange)
                    DetailRow("TotalAmount", "₱1,200.00", isBold = true)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION: ORDER DETAILS ---
                DetailsSectionHeader("ORDER DETAILS")
                DetailsCard {
                    // Item 1
                    ProductDetailRow("11kg Standard Refill", "1", "₱1,150", "₱1,150")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F3F4))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = TextGray, fontSize = 14.sp)
                        Text("₱1,150.00", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION: PAYMENT ---
                DetailsSectionHeader("PAYMENT")
                DetailsCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.gcash_logo), contentDescription = null, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("GCash", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Status: Paid", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION: DELIVERY ---
                DetailsSectionHeader("DELIVERY")
                DetailsCard {
                    DetailRow("DeliveryID", "DEL-990812")
                    DetailRow("SalesID", "SLS-77612")
                    DetailRow("DRNo", "DR-4451")
                    DetailRow("DeliveryDate", "Oct 24, 2023")
                    DetailRow("DeliveredBy", "Juan Dela Cruz (USR-092)")
                    DetailRow("DeliveryCharge", "₱50.00")
                    DetailRow("DeliveryAddress", "45 Mabini St, Malabon City")
                    DetailRow("DeliveryStatus", if (isArrived) "Completed" else "Ongoing", color = if (isArrived) Color(0xFF2E7D32) else ButtonOrange)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- ACTION BUTTONS ---
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalBlue)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Track Order", fontWeight = FontWeight.Black)
                    }

                    if (!isArrived) {
                        OutlinedButton(
                            onClick = { /* Handle Cancel */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, GasTrackRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GasTrackRed)
                        ) {
                            Text("Cancel Order", fontWeight = FontWeight.Black)
                        }
                    }

                    if (isArrived) {
                        Button(
                            onClick = { /* Handle Reorder */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reorder", fontWeight = FontWeight.Black)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailsSectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = TextGray,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
fun DetailsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) { content() }
    }
}

@Composable
fun DetailRow(label: String, value: String, isBold: Boolean = false, color: Color = TextDark) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = TextGray, fontSize = 14.sp)
        Text(text = value, color = if (isBold) StaffPortalBlue else color, fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ProductDetailRow(name: String, qty: String, price: String, subtotal: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Qty: $qty × $price", color = TextGray, fontSize = 13.sp)
            Text(text = subtotal, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
        }
    }
}

@Composable
fun TrackingStep(
    title: String, 
    time: String, 
    isCompleted: Boolean, 
    isLast: Boolean,
    isProcessing: Boolean = false
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Surface(
                modifier = Modifier.size(10.dp),
                shape = CircleShape,
                color = if (isCompleted) GasTrackRed else Color(0xFFE9ECEF)
            ) {}
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(if (isCompleted) GasTrackRed else Color(0xFFE9ECEF))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isCompleted) FontWeight.Black else FontWeight.Bold,
                color = if (isCompleted) TextDark else TextGray
            )
            Text(
                text = if (isProcessing) "Ongoing" else time,
                fontSize = 12.sp,
                color = if (isProcessing) GasTrackRed else TextGray,
                fontWeight = if (isProcessing) FontWeight.Black else FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapTrackingScreenPreview() {
    MapTrackingScreen(onBack = {})
}
