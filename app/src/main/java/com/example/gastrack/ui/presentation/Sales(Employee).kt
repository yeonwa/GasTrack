package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

data class SalesData(
    val salesId: String,
    val orderId: String,
    val customerId: String,
    val saleNo: String,
    val saleDate: String,
    val salesDiscount: Double,
    val totalAmount: Double,
    val processedBy: String,
    val remarks: String = ""
)

data class PaymentData(
    val paymentId: String,
    val paymentType: String, // e.g., "Sale", "Purchase Order"
    val saleId: String? = null,
    val purchaseOrderId: String? = null,
    val paymentMethod: String, // e.g., "Cash", "GCash", "Card"
    val amountPaid: Double,
    val paymentDate: String,
    val referenceNo: String = "N/A",
    val remarks: String = ""
)

@Composable
fun SalesEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Dashboard") }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedSale by remember { mutableStateOf<SalesData?>(null) }
    
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var saleToDelete by remember { mutableStateOf<SalesData?>(null) }

    // Mock Data
    val salesRecords = remember { mutableStateListOf(
        SalesData("S-001", "O-001", "C-001", "SALE-2026-001", "2026-01-05 10:35 AM", 0.0, 950.0, "EMP-001", "Regular customer"),
        SalesData("S-002", "O-002", "C-002", "SALE-2026-002", "2026-01-05 10:22 AM", 50.0, 1000.0, "EMP-002"),
        SalesData("S-003", "O-003", "C-003", "SALE-2026-003", "2026-01-04 03:30 PM", 0.0, 550.0, "EMP-001"),
        SalesData("S-004", "O-004", "C-004", "SALE-2026-004", "2026-01-03 10:35 AM", 0.0, 105.0, "EMP-003")
    ) }

    val paymentRecords = remember { listOf(
        PaymentData("P-001", "Sale", "S-001", null, "Cash", 950.0, "2026-01-05 10:35 AM"),
        PaymentData("P-002", "Sale", "S-002", null, "GCash", 1000.0, "2026-01-05 10:22 AM", "REF-8821"),
        PaymentData("P-003", "Sale", "S-003", null, "Card", 550.0, "2026-01-04 03:30 PM", "AUTH-9912")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "Sales Analytics",
            subtitle = "Performance Tracking",
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            EmployeeTabSelector(
                tabs = listOf("Dashboard", "Records"),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        Crossfade(targetState = selectedTab, label = "SalesTabTransition") { tab ->
            when (tab) {
                "Dashboard" -> SalesDashboardTab()
                "Records" -> SalesRecordsTab(
                    sales = salesRecords,
                    payments = paymentRecords,
                    onViewClick = { sale -> 
                        selectedSale = sale
                        showDetailDialog = true 
                    },
                    onDeleteClick = { sale ->
                        saleToDelete = sale
                        showDeleteConfirm = true
                    }
                )
            }
        }
    }

    if (showDetailDialog && selectedSale != null) {
        val relatedPayment = paymentRecords.find { it.saleId == selectedSale!!.salesId }
        SalesDetailDialog(
            sale = selectedSale!!,
            payment = relatedPayment,
            onDismiss = { showDetailDialog = false }
        )
    }

    if (showDeleteConfirm && saleToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Transaction", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to delete ${saleToDelete!!.saleNo}? This will permanently remove the record from system audit.") },
            confirmButton = {
                TextButton(onClick = {
                    salesRecords.remove(saleToDelete)
                    showDeleteConfirm = false
                }) { Text("Delete", color = GasTrackRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}



@Composable
fun SalesDashboardTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Today's Performance Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, StaffPortalButtonBlue.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Today's Gross Revenue", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "₱ 15,240.50", color = StaffPortalButtonBlue, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                        Text(text = "+12.5%", color = Color(0xFF2E7D32), fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "vs yesterday", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Stats Grid
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SalesStatCard("TOTAL SALES", "₱ 15,000", Color(0xFF3F51B5), modifier = Modifier.weight(1f))
                SalesStatCard("TRANSACTIONS", "50", Color(0xFF4CAF50), modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SalesStatCard("AVG ORDER", "₱ 500", Color(0xFFFF9800), modifier = Modifier.weight(1f))
                SalesStatCard("PEAK HOUR", "12:00 PM", Color(0xFF9C27B0), modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SalesStatCard(label: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = RoundedCornerShape(6.dp),
                color = accentColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(6.dp).background(accentColor, CircleShape))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
            Text(text = label, fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun SalesRecordsTab(
    sales: List<SalesData>,
    payments: List<PaymentData>,
    onViewClick: (SalesData) -> Unit,
    onDeleteClick: (SalesData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPaymentType by remember { mutableStateOf("All") }

    val filteredSales = sales.filter { sale ->
        val payment = payments.find { it.saleId == sale.salesId }
        val matchesSearch = sale.saleNo.contains(searchQuery, ignoreCase = true)
        val matchesType = if (selectedPaymentType == "All") true else payment?.paymentMethod == selectedPaymentType
        matchesSearch && matchesType
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().height(52.dp), 
            placeholder = { Text("Search by Sale No...", color = Color.LightGray, fontSize = 14.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Payment Type Filter Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            var expanded by remember { mutableStateOf(false) }
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (selectedPaymentType != "All") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPaymentType == "All") "Filter by Payment Type" else "Payment: $selectedPaymentType",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedPaymentType == "All") TextGray else TextDark
                        )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
            ) {
                listOf("All", "Cash", "GCash", "Card").forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                        onClick = {
                            selectedPaymentType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredSales) { sale ->
                val payment = payments.find { it.saleId == sale.salesId }
                SalesRecordCard(
                    sale = sale,
                    paymentMethod = payment?.paymentMethod ?: "Unknown",
                    onViewClick = { onViewClick(sale) },
                    onDeleteClick = { onDeleteClick(sale) }
                )
            }
        }
    }
}

@Composable
fun SalesRecordCard(
    sale: SalesData,
    paymentMethod: String,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = sale.saleNo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Text(text = sale.saleDate, fontSize = 10.sp, color = TextGray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = String.format(Locale.US, "₱%,.2f", sale.totalAmount), fontWeight = FontWeight.Black, fontSize = 17.sp, color = StaffPortalButtonBlue)
                    Surface(color = Color(0xFFF1F3F4), shape = RoundedCornerShape(4.dp)) {
                        Text(text = paymentMethod.uppercase(), color = TextGray, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detail Box
            Surface(
                color = Color(0xFFFBFBFE),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PROCESSED BY", fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Black)
                        Text(sale.processedBy, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(16.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ORDER ID", fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Black)
                        Text(sale.orderId, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onViewClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "View", tint = StaffPortalButtonBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(onClick = { /* Print Logic */ }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Print, contentDescription = "Print", tint = TextDark, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GasTrackRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun SalesDetailDialog(
    sale: SalesData,
    payment: PaymentData?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Transaction Receipt", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailValueRow("Sale No", sale.saleNo, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Order ID", sale.orderId)
                DetailValueRow("Customer ID", sale.customerId)
                DetailValueRow("Processed By", sale.processedBy)
                DetailValueRow("Timestamp", sale.saleDate)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(12.dp))

                Text("PRICING SUMMARY", fontWeight = FontWeight.Black, fontSize = 10.sp, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailValueRow("Subtotal", String.format(Locale.US, "₱%,.2f", sale.totalAmount + sale.salesDiscount))
                DetailValueRow("Discount", String.format(Locale.US, "₱%,.2f", sale.salesDiscount), color = GasTrackRed)
                Spacer(modifier = Modifier.height(4.dp))
                DetailValueRow("TOTAL AMOUNT", String.format(Locale.US, "₱%,.2f", sale.totalAmount), isBold = true, color = GasTrackRed)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(12.dp))

                Text("PAYMENT INFORMATION", fontWeight = FontWeight.Black, fontSize = 10.sp, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (payment != null) {
                    DetailValueRow("Payment ID", payment.paymentId)
                    DetailValueRow("Type", payment.paymentType)
                    DetailValueRow("Method", payment.paymentMethod, isBold = true, color = StaffPortalButtonBlue)
                    DetailValueRow("Amount Paid", String.format(Locale.US, "₱%,.2f", payment.amountPaid))
                    DetailValueRow("Payment Date", payment.paymentDate)
                    DetailValueRow("Reference No", payment.referenceNo)
                    if (payment.remarks.isNotEmpty()) {
                        DetailValueRow("Pymt Remarks", payment.remarks)
                    }
                } else {
                    Text("No payment record found", fontSize = 12.sp, color = TextGray)
                }
                
                if (sale.remarks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("REMARKS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                    Text(sale.remarks, fontSize = 12.sp, color = TextDark)
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Print Copy", fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalesEmployeeScreenPreview() {
    SalesEmployeeScreen()
}
