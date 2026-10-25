package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

@Immutable
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

@Immutable
data class PaymentData(
    val paymentId: String,
    val paymentType: String,
    val saleId: String? = null,
    val purchaseOrderId: String? = null,
    val paymentMethod: String,
    val amountPaid: Double,
    val paymentDate: String,
    val referenceNo: String = "N/A",
    val remarks: String = ""
)

@Composable
fun SalesEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedSale by remember { mutableStateOf<SalesData?>(null) }
    
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var saleToDelete by remember { mutableStateOf<SalesData?>(null) }
    
    var showSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Mock Data
    val salesRecords = remember { mutableStateListOf(
        SalesData("S-001", "O-001", "C-001", "SALE-2026-001", "2026-01-05 10:35 AM", 0.0, 950.0, "EMP-001", "Regular customer"),
        SalesData("S-002", "O-002", "C-002", "SALE-2026-002", "2026-01-05 10:22 AM", 50.0, 1000.0, "EMP-002"),
        SalesData("S-003", "O-003", "C-003", "SALE-2026-003", "2026-01-04 03:30 PM", 0.0, 550.0, "EMP-001"),
        SalesData("S-004", "O-004", "C-004", "SALE-2026-004", "2026-01-03 10:35 AM", 0.0, 105.0, "EMP-003")
    ) }

    val paymentRecords = remember { mutableStateListOf(
        PaymentData("P-001", "Sale", "S-001", null, "Cash", 950.0, "2026-01-05 10:35 AM"),
        PaymentData("P-002", "Sale", "S-002", null, "GCash", 1000.0, "2026-01-05 10:22 AM", "REF-8821"),
        PaymentData("P-003", "Sale", "S-003", null, "Card", 550.0, "2026-01-04 03:30 PM", "AUTH-9912")
    ) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCashier by remember { mutableStateOf("All Cashier") }
    var selectedStatus by remember { mutableStateOf("All Status") }

    val filteredSales = salesRecords.filter { sale ->
        val payment = paymentRecords.find { it.saleId == sale.salesId }
        val matchesSearch = sale.saleNo.contains(searchQuery, ignoreCase = true)
        val matchesCashier = selectedCashier == "All Cashier" || sale.processedBy == selectedCashier
        val matchesStatus = selectedStatus == "All Status" || payment?.paymentMethod == selectedStatus
        matchesSearch && matchesCashier && matchesStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground)
    ) {
        EmployeePortalHeader(
            title = "Sales",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // STATS GRID SECTION
            item {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SalesStatCard("TODAY'S SALE", "₱ 15,240.50", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF3F51B5), modifier = Modifier.weight(1f))
                        SalesStatCard("TOTAL TRANSACTIONS", "50", Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFF4CAF50), modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SalesStatCard("AVERAGE ORDER VALUE", "₱ 500.00", Icons.Default.ShoppingCart, Color(0xFFFF9800), modifier = Modifier.weight(1f))
                        SalesStatCard("TODAY'S TRANSACTIONS", "12", Icons.Default.DateRange, Color(0xFF9C27B0), modifier = Modifier.weight(1f))
                    }
                }
            }

            // TOOLBAR (Search, Filters, Import Buttons)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EmployeeSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search By Sales ID"
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterDropdown(
                            options = listOf("All Cashier", "EMP-001", "EMP-002", "EMP-003"),
                            selectedOption = selectedCashier,
                            onOptionSelected = { selectedCashier = it },
                            modifier = Modifier.weight(1f)
                        )
                        FilterDropdown(
                            options = listOf("All Status", "Cash", "GCash", "Card"), // Payment method mock
                            selectedOption = selectedStatus,
                            onOptionSelected = { selectedStatus = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showSuccessMessage = "CSV Template Downloaded" },
                            modifier = Modifier.weight(1f).height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CSV Template", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showSuccessMessage = "Sales Data Imported Successfully" },
                            modifier = Modifier.weight(1f).height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Sales", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // SALES RECORDS LIST
            if (filteredSales.isEmpty()) {
                item {
                    Text(
                        text = "No sales match your filters.",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(filteredSales) { sale ->
                    val payment = paymentRecords.find { it.saleId == sale.salesId }
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        SalesRecordCard(
                            sale = sale,
                            paymentMethod = payment?.paymentMethod ?: "Unknown",
                            onViewClick = { 
                                selectedSale = sale
                                showDetailDialog = true 
                            },
                            onDeleteClick = { 
                                saleToDelete = sale
                                showDeleteConfirm = true
                            }
                        )
                    }
                }
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
            title = { Text("Void Sale", fontWeight = FontWeight.Black) },
            text = { Text("Void sale ${saleToDelete!!.saleNo}? This restores the sold stock back to inventory and cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    salesRecords.remove(saleToDelete)
                    showDeleteConfirm = false
                }) { Text("Void", color = GasTrackRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showSuccessMessage != null) {
        PortalSuccessDialog(
            message = showSuccessMessage!!,
            onDismiss = { showSuccessMessage = null }
        )
    }
}

@Composable
fun SalesStatCard(label: String, value: String, icon: ImageVector, accentColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = TextDark, 
                    letterSpacing = 0.5.sp, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value, 
                    fontSize = 15.sp, 
                    fontWeight = FontWeight.Black, 
                    color = accentColor, 
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
