package com.example.gastrack.ui.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

// --- Data Models ---

@Immutable
data class RestockRecommendationData(
    val restockId: String,
    val productId: String,
    val productName: String,
    val supplierId: String,
    val supplierName: String,
    val currentStock: Int,
    val reorderLevel: Int,
    val suggestedQty: Int,
    val confidence: Double,
    val priority: String, // "Low", "Critical"
    val costPrice: Double,
    val forecastDate: String,
    val status: String // Pending, Approved, Rejected, Cancelled
)

@Immutable
data class PurchaseOrderData(
    val purchaseOrderId: String,
    val supplierId: String,
    val supplierName: String,
    val restockIds: List<String>,
    val createdByUserId: String,
    val poNo: String,
    val orderDate: String,
    val expectedDeliveryDate: String,
    val status: String, // Draft, Submitted, Approved, Received, Cancelled
    val totalAmount: Double,
    val totalQty: Int,
    val remarks: String = "",
    val items: List<PurchaseOrderItemData> = emptyList()
)

@Immutable
data class PurchaseOrderItemData(
    val id: String,
    val purchaseOrderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitCost: Double,
    val subtotal: Double
)

private const val HISTORY_PAGE_SIZE = 6

fun formatPeso(amount: Double): String {
    return String.format(Locale.US, "₱ %,.2f", amount)
}

fun getPaginationGroup(currentPage: Int, totalPages: Int, maxVisible: Int = 6): List<Int> {
    var start = maxOf(1, currentPage - maxVisible / 2)
    var end = start + maxVisible - 1
    if (end > totalPages) {
        end = totalPages
        start = maxOf(1, end - maxVisible + 1)
    }
    val pages = mutableListOf<Int>()
    for (page in start..end) {
        pages.add(page)
    }
    return pages
}

fun displayPurchaseOrderStatus(status: String): String {
    return when (status) {
        "Received" -> "Received"
        "Cancelled" -> "Cancelled"
        else -> "Sent"
    }
}

fun canReceivePurchaseOrder(status: String): Boolean {
    return status == "Approved" || status == "Submitted" || status == "Sent"
}

@Composable
fun RestockingEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf("restocking") }

    val recommendations = remember { mutableStateListOf(
        RestockRecommendationData("REC-001", "P-001", "11kg Standard Refill", "S-001", "Petron Gas Corp", 15, 20, 85, 95.0, "Critical", 850.0, "2026-01-10", "Pending"),
        RestockRecommendationData("REC-002", "P-002", "2.7kg Camping Refill", "S-002", "Metrolist Inc", 8, 10, 42, 90.0, "Low", 250.0, "2026-01-12", "Approved"),
        RestockRecommendationData("REC-003", "P-003", "Gas Regulator (Auto)", "S-003", "Ace Hardware", 5, 15, 25, 88.0, "Critical", 320.0, "2026-01-15", "Pending")
    ) }

    val purchaseOrderHistory = remember { mutableStateListOf(
        PurchaseOrderData("PO-001", "S-001", "Petron Gas Corp", listOf("REC-001"), "U-001", "PO-2026-001", "01/01/2026", "01/05/2026", "Submitted", 72250.0, 85, "", listOf(
            PurchaseOrderItemData("POI-001", "PO-001", "P-001", "11kg Standard Refill", 85, 850.0, 72250.0)
        ))
    ) }

    val loadError by remember { mutableStateOf("") }
    val historyLoadError by remember { mutableStateOf("") }
    var actionError by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedSupplier by remember { mutableStateOf("All Supplier") }
    var selectedPriority by remember { mutableStateOf("All Priority") }
    val selectedRows = remember { mutableStateListOf<String>() } // restockIds

    // Modal States
    var isConfirmModalOpen by remember { mutableStateOf(false) }
    var isCustomizeOpen by remember { mutableStateOf(false) }
    var isSingleCustomizeOpen by remember { mutableStateOf(false) }
    var selectedItemForCustomize by remember { mutableStateOf<RestockRecommendationData?>(null) }

    // Draft Purchase Order
    var poDraft by remember { mutableStateOf<PurchaseOrderData?>(null) }
    var savedPoId by remember { mutableStateOf<String?>(null) }
    var isSavingDraft by remember { mutableStateOf(false) }

    var historyPage by remember { mutableIntStateOf(1) }
    var viewingPurchaseOrderId by remember { mutableStateOf<String?>(null) }
    var viewPurchaseOrder by remember { mutableStateOf<PurchaseOrderData?>(null) }
    var isReceiving by remember { mutableStateOf(false) }
    var receiveWarehouseId by remember { mutableStateOf("") }
    val warehouses = remember { mutableStateListOf(
        WarehouseData("WH-001", "Main Warehouse", "Pasig City", "Active"),
        WarehouseData("WH-002", "North Branch Depot", "Taguig City", "Active")
    ) }

    val suppliers = remember(recommendations) {
        listOf("All Supplier") + recommendations.map { it.supplierName }.distinct()
    }

    val filteredData = remember(recommendations, searchQuery, selectedSupplier, selectedPriority) {
        recommendations.filter { item ->
            val matchesSearch = searchQuery.isEmpty() || item.productName.contains(searchQuery, ignoreCase = true)
            val matchesSupplier = selectedSupplier == "All Supplier" || item.supplierName == selectedSupplier
            val matchesPriority = selectedPriority == "All Priority" || item.priority == selectedPriority
            matchesSearch && matchesSupplier && matchesPriority
        }
    }

    val historyTotalPages = maxOf(1, (purchaseOrderHistory.size + HISTORY_PAGE_SIZE - 1) / HISTORY_PAGE_SIZE)
    val historyCurrentPage = minOf(historyPage, historyTotalPages)
    val paginatedPurchaseOrderHistory = remember(purchaseOrderHistory, historyCurrentPage) {
        val start = (historyCurrentPage - 1) * HISTORY_PAGE_SIZE
        val end = minOf(start + HISTORY_PAGE_SIZE, purchaseOrderHistory.size)
        if (start < purchaseOrderHistory.size) purchaseOrderHistory.subList(start, end) else emptyList()
    }

    val criticalCount = recommendations.count { it.priority == "Critical" }
    val estimatedCost = recommendations.sumOf { it.suggestedQty * it.costPrice }

    val toggleSelectAll = {
        if (selectedRows.size == filteredData.size && filteredData.isNotEmpty()) {
            selectedRows.clear()
        } else {
            selectedRows.clear()
            selectedRows.addAll(filteredData.map { it.restockId })
        }
    }

    val toggleRowSelect = { id: String ->
        if (selectedRows.contains(id)) selectedRows.remove(id) else selectedRows.add(id)
    }

    val handleGenerate = {
        isGenerating = true
        actionError = ""
        val newRec = RestockRecommendationData(
            restockId = "REC-00${recommendations.size + 1}",
            productId = "P-004",
            productName = "50kg Industrial Cylinder",
            supplierId = "S-001",
            supplierName = "Petron Gas Corp",
            currentStock = 4,
            reorderLevel = 10,
            suggestedQty = 20,
            confidence = 92.0,
            priority = "Critical",
            costPrice = 2800.0,
            forecastDate = "2026-01-20",
            status = "Pending"
        )
        recommendations.add(newRec)
        isGenerating = false
    }

    val handleDeleteRecommendation = { item: RestockRecommendationData ->
        recommendations.removeAll { it.restockId == item.restockId }
        selectedRows.remove(item.restockId)
    }

    val handleOpenSingleCustomize = { item: RestockRecommendationData ->
        selectedItemForCustomize = item
        isSingleCustomizeOpen = true
    }

    val buildDraftFromItems = { items: List<RestockRecommendationData> ->
        if (items.isNotEmpty()) {
            val supplierIds = items.map { it.supplierId }.distinct()
            if (supplierIds.size > 1) {
                actionError = "Selected items belong to different suppliers. A purchase order can only include items from one supplier."
            } else {
                actionError = ""
                val poItems = items.map { i ->
                    PurchaseOrderItemData(
                        id = "POI-${System.currentTimeMillis()}-${i.productId}",
                        purchaseOrderId = "PO-NEW",
                        productId = i.productId,
                        productName = i.productName,
                        quantity = i.suggestedQty,
                        unitCost = i.costPrice,
                        subtotal = i.suggestedQty * i.costPrice
                    )
                }
                poDraft = PurchaseOrderData(
                    purchaseOrderId = "PO-${System.currentTimeMillis()}",
                    supplierId = items[0].supplierId,
                    supplierName = items[0].supplierName,
                    restockIds = items.map { it.restockId },
                    createdByUserId = "U-001",
                    poNo = "PO-2026-00${purchaseOrderHistory.size + 1}",
                    orderDate = "01/02/2026",
                    expectedDeliveryDate = "01/08/2026",
                    status = "Draft",
                    totalAmount = poItems.sumOf { it.subtotal },
                    totalQty = poItems.sumOf { it.quantity },
                    items = poItems
                )
                savedPoId = null
                activeTab = "purchaseOrder"
            }
        }
    }

    val handleBuildFromSelection = {
        val items = recommendations.filter { selectedRows.contains(it.restockId) }
        if (items.isEmpty()) {
            actionError = "Check at least one item in the table before building a purchase order."
        } else {
            buildDraftFromItems(items)
        }
    }

    val poTotals = remember(poDraft) {
        val draft = poDraft
        if (draft == null) {
            Pair(0.0, 0)
        } else {
            val sub = draft.items.sumOf { it.subtotal }
            val qty = draft.items.sumOf { it.quantity }
            Pair(sub, qty)
        }
    }

    val saveDraft = {
        val draft = poDraft
        if (draft != null) {
            isSavingDraft = true
            val newPo = draft.copy(status = "Submitted", totalAmount = poTotals.first, totalQty = poTotals.second)
            purchaseOrderHistory.add(newPo)
            savedPoId = newPo.purchaseOrderId
            selectedRows.clear()
            isSavingDraft = false
            newPo.purchaseOrderId
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground)
    ) {
        EmployeePortalHeader(
            title = "Restocking Assistant",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                if (loadError.isNotEmpty()) {
                    Text(loadError, color = GasTrackRed, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                }
                if (historyLoadError.isNotEmpty()) {
                    Text(historyLoadError, color = GasTrackRed, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                }
                if (actionError.isNotEmpty()) {
                    Text(actionError, color = GasTrackRed, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                }

                // Metric Cards (Uniform 2-Row Grid matching Inventory)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InventoryStatCard(
                            label = "Critical Urgencies",
                            value = "$criticalCount",
                            color = GasTrackRed,
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Warning,
                            sub = "Products"
                        )
                        InventoryStatCard(
                            label = "Restocking Cost",
                            value = formatPeso(estimatedCost),
                            color = StaffPortalButtonBlue,
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.AttachMoney,
                            sub = "Est. Total"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InventoryStatCard(
                            label = "Open Recommendations",
                            value = recommendations.size.toString(),
                            color = Color(0xFFFFA000),
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Assessment,
                            sub = "Active"
                        )
                    }
                }
            }

            item {
                // Navigation Tabs & Actions Bar
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EmployeeTabSelector(
                        tabs = listOf("Restocking", "Purchase Order", "PO History"),
                        selectedTab = when (activeTab) {
                            "restocking" -> "Restocking"
                            "purchaseOrder" -> "Purchase Order"
                            else -> "PO History"
                        },
                        onTabSelected = { tab ->
                            if (tab == "Purchase Order" && poDraft == null) {
                                actionError = "Select items in Restocking first to build a purchase order"
                            } else {
                                actionError = ""
                                activeTab = when (tab) {
                                    "Restocking" -> "restocking"
                                    "Purchase Order" -> "purchaseOrder"
                                    else -> "purchaseOrderHistory"
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (activeTab == "restocking") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = handleGenerate,
                                enabled = !isGenerating,
                                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(44.dp).shadow(2.dp, RoundedCornerShape(10.dp)),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isGenerating) "Generating..." else "Generate", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            Button(
                                onClick = handleBuildFromSelection,
                                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(44.dp).shadow(2.dp, RoundedCornerShape(10.dp)),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PO (${selectedRows.size})", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            Button(
                                onClick = { isCustomizeOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(44.dp).shadow(2.dp, RoundedCornerShape(10.dp)),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Customize", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // TAB 1: RESTOCKING VIEW
            if (activeTab == "restocking") {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Search Field Row
                        EmployeeSearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "Search by Product"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filters Row (Supplier & Priority Dropdowns)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterDropdown(
                                options = suppliers,
                                selectedOption = selectedSupplier,
                                onOptionSelected = { selectedSupplier = it },
                                modifier = Modifier.weight(1f)
                            )
                            FilterDropdown(
                                options = listOf("All Priority", "Low", "Critical"),
                                selectedOption = selectedPriority,
                                onOptionSelected = { selectedPriority = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Recommendations List Card View
                        Surface(
                            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = selectedRows.size == filteredData.size && filteredData.isNotEmpty(),
                                            onCheckedChange = { toggleSelectAll() }
                                        )
                                        Text("Select All (${filteredData.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                    Text("Restock Items", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StaffPortalButtonBlue)
                                }

                                if (filteredData.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No open recommendations found.", fontSize = 13.sp, color = TextGray)
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        filteredData.forEach { item ->
                                            val isSelected = selectedRows.contains(item.restockId)
                                            RestockRecommendationCardItem(
                                                item = item,
                                                isSelected = isSelected,
                                                onSelectToggle = { toggleRowSelect(item.restockId) },
                                                onCustomize = { handleOpenSingleCustomize(item) },
                                                onPackage = { buildDraftFromItems(listOf(item)) },
                                                onDelete = { handleDeleteRecommendation(item) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: PURCHASE ORDER BUILDER VIEW
            if (activeTab == "purchaseOrder" && poDraft != null) {
                val draft = poDraft!!
                item {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Left Main Card: Form Inputs & Items
                        Surface(
                            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                // Top Header Inputs: Row 1 (Purchase Order & Items Count), Row 2 (Supplier)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text("Purchase Order", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = if (savedPoId != null) "Saved (#$savedPoId)" else "Not yet saved",
                                            onValueChange = {},
                                            readOnly = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Items Count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = draft.items.size.toString(),
                                            onValueChange = {},
                                            readOnly = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, textAlign = TextAlign.Center)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Supplier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = draft.supplierName,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                    )
                                }

                                Text("Items", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextDark)

                                draft.items.forEachIndexed { idx, itm ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, EmployeePortalBackground)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text(itm.productName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                                Text("Cost: ${formatPeso(itm.unitCost)}", fontSize = 12.sp, color = TextGray)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text("Qty:", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                                    OutlinedTextField(
                                                        value = itm.quantity.toString(),
                                                        onValueChange = { newText ->
                                                            val newQty = newText.toIntOrNull() ?: 0
                                                            val updatedItems = draft.items.mapIndexed { i, cur ->
                                                                if (i == idx) cur.copy(quantity = newQty, subtotal = newQty * cur.unitCost) else cur
                                                            }
                                                            poDraft = draft.copy(items = updatedItems)
                                                        },
                                                        modifier = Modifier.width(80.dp).height(48.dp),
                                                        singleLine = true,
                                                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                                    )
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text("Subtotal", fontSize = 10.sp, color = TextGray)
                                                    Text(formatPeso(itm.subtotal), fontSize = 14.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = EmployeePortalBackground)

                                Text("Total Summary", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextDark)

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Items:", fontSize = 13.sp, color = TextGray)
                                        Text(draft.items.size.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Quantity:", fontSize = 13.sp, color = TextGray)
                                        Text(poTotals.second.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Amount:", fontSize = 13.sp, color = TextGray)
                                        Text(formatPeso(poTotals.first), fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
                                    }
                                }
                            }
                        }

                        // Right Side Summary Panel Card
                        Surface(
                            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                // Header
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = StaffPortalButtonBlue,
                                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Text("Summary", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }

                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Subtotal:", fontSize = 13.sp, color = TextGray)
                                        Text(formatPeso(poTotals.first), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }

                                    HorizontalDivider(color = EmployeePortalBackground)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Total Cost", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
                                        Text(formatPeso(poTotals.first), fontSize = 18.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Button(
                                        onClick = { isConfirmModalOpen = true },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Confirm Purchase Order", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    }

                                    Button(
                                        onClick = {
                                            saveDraft()
                                            activeTab = "purchaseOrderHistory"
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmployeePortalBackground, contentColor = TextDark),
                                        shape = RoundedCornerShape(8.dp),
                                        enabled = !isSavingDraft
                                    ) {
                                        Text(if (isSavingDraft) "Saving..." else "Save as Draft", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    }

                                    Button(
                                        onClick = {
                                            poDraft = null
                                            activeTab = "restocking"
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GasTrackRed),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cancel Purchase Order", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 3: PURCHASE ORDER HISTORY
            if (activeTab == "purchaseOrderHistory") {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Purchase Order History", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextDark, modifier = Modifier.padding(bottom = 12.dp))

                                if (purchaseOrderHistory.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No purchase order history yet.", fontSize = 13.sp, color = TextGray)
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        paginatedPurchaseOrderHistory.forEach { po ->
                                            val displayStatus = displayPurchaseOrderStatus(po.status)
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = Color(0xFFF8FAFC),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, EmployeePortalBackground)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(po.poNo, fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextDark)
                                                        Text("Supplier: ${po.supplierName}", fontSize = 11.sp, color = TextGray)
                                                        Text("Date: ${po.orderDate} | Total: ${formatPeso(po.totalAmount)}", fontSize = 11.sp, color = TextDark)
                                                    }
                                                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Surface(
                                                            color = when (displayStatus) {
                                                                "Received" -> Color(0xFFDCFCE7)
                                                                "Cancelled" -> Color(0xFFFEE2E2)
                                                                else -> Color(0xFFDBEAFE)
                                                            },
                                                            shape = RoundedCornerShape(999.dp)
                                                        ) {
                                                            Text(
                                                                text = displayStatus,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = when (displayStatus) {
                                                                    "Received" -> Color(0xFF166534)
                                                                    "Cancelled" -> Color(0xFF991B1B)
                                                                    else -> Color(0xFF1E3A8A)
                                                                }
                                                            )
                                                        }
                                                        Button(
                                                            onClick = {
                                                                viewingPurchaseOrderId = po.purchaseOrderId
                                                                viewPurchaseOrder = po
                                                            },
                                                            modifier = Modifier.height(28.dp),
                                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                                                            shape = RoundedCornerShape(6.dp)
                                                        ) {
                                                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("View", fontSize = 11.sp)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Pagination Controls
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { if (historyPage > 1) historyPage-- },
                                            enabled = historyPage > 1
                                        ) {
                                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous page")
                                        }

                                        getPaginationGroup(historyCurrentPage, historyTotalPages, 6).forEach { page ->
                                            Button(
                                                onClick = { historyPage = page },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (page == historyCurrentPage) StaffPortalButtonBlue else Color.White,
                                                    contentColor = if (page == historyCurrentPage) Color.White else TextDark
                                                ),
                                                modifier = Modifier.size(36.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(page.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }

                                        IconButton(
                                            onClick = { if (historyPage < historyTotalPages) historyPage++ },
                                            enabled = historyPage < historyTotalPages
                                        ) {
                                            Icon(Icons.Default.ChevronRight, contentDescription = "Next page")
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

    // Confirmation Modal
    if (isConfirmModalOpen) {
        Dialog(onDismissRequest = { isConfirmModalOpen = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Are you sure you want to confirm this purchase order?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Once confirmed, the purchase order will be submitted and sent to the supplier.",
                        fontSize = 13.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { isConfirmModalOpen = false },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = TextDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = {
                                saveDraft()
                                isConfirmModalOpen = false
                                poDraft = null
                                activeTab = "purchaseOrderHistory"
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Confirm", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    // Bulk Customize Modal
    if (isCustomizeOpen) {
        CustomizeRestockModal(
            onClose = { isCustomizeOpen = false },
            restockList = RestockItemList(recommendations.filter { selectedRows.contains(it.restockId) }),
            onApplied = {
                isCustomizeOpen = false
            }
        )
    }

    // Single Customize Modal
    if (isSingleCustomizeOpen && selectedItemForCustomize != null) {
        CustomizeSingleItemModal(
            onClose = { isSingleCustomizeOpen = false },
            selectedItem = selectedItemForCustomize!!,
            onSaved = { updatedQty ->
                val index = recommendations.indexOfFirst { it.restockId == selectedItemForCustomize!!.restockId }
                if (index != -1) {
                    recommendations[index] = recommendations[index].copy(suggestedQty = updatedQty)
                }
                isSingleCustomizeOpen = false
            }
        )
    }

    // View PO Details Dialog & Receive
    if (viewingPurchaseOrderId != null && viewPurchaseOrder != null) {
        val po = viewPurchaseOrder!!
        Dialog(onDismissRequest = { viewingPurchaseOrderId = null; viewPurchaseOrder = null }) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Purchase Order Details", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                        IconButton(onClick = { viewingPurchaseOrderId = null; viewPurchaseOrder = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, EmployeePortalBackground)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("PO Number:", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                Text(po.poNo, fontSize = 12.sp, color = StaffPortalButtonBlue, fontWeight = FontWeight.Black)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Supplier:", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                Text(po.supplierName, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Order Date:", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                Text(po.orderDate, fontSize = 12.sp, color = TextDark)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Status:", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                Surface(
                                    color = when (displayPurchaseOrderStatus(po.status)) {
                                        "Received" -> Color(0xFFDCFCE7)
                                        "Cancelled" -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFDBEAFE)
                                    },
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = displayPurchaseOrderStatus(po.status),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (displayPurchaseOrderStatus(po.status)) {
                                            "Received" -> Color(0xFF166534)
                                            "Cancelled" -> Color(0xFF991B1B)
                                            else -> Color(0xFF1E3A8A)
                                        }
                                    )
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Amount:", fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Bold)
                                Text(formatPeso(po.totalAmount), fontSize = 13.sp, color = GasTrackRed, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Items", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    val poItems = po.items.ifEmpty {
                        listOf(
                            PurchaseOrderItemData("POI-DEFAULT", po.purchaseOrderId, "P-001", "Cylinder 11 kg", if (po.totalQty > 0) po.totalQty else 50, if (po.totalQty > 0) po.totalAmount / po.totalQty else 870.0, po.totalAmount)
                        )
                    }

                    poItems.forEach { item ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EmployeePortalBackground)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(item.productName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Qty: ${item.quantity} x ${formatPeso(item.unitCost)}", fontSize = 11.sp, color = TextGray)
                                    Text(formatPeso(item.subtotal), fontSize = 13.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                                }
                            }
                        }
                    }

                    if (canReceivePurchaseOrder(po.status)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Receive to Warehouse", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))

                        var warehouseExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { warehouseExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(warehouses.find { it.id == receiveWarehouseId }?.name ?: "Select Warehouse", fontSize = 13.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(expanded = warehouseExpanded, onDismissRequest = { warehouseExpanded = false }) {
                                warehouses.forEach { wh ->
                                    DropdownMenuItem(text = { Text(wh.name) }, onClick = {
                                        receiveWarehouseId = wh.id
                                        warehouseExpanded = false
                                    })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isReceiving = true
                                val idx = purchaseOrderHistory.indexOfFirst { it.purchaseOrderId == po.purchaseOrderId }
                                if (idx != -1) {
                                    purchaseOrderHistory[idx] = po.copy(status = "Received")
                                    viewPurchaseOrder = purchaseOrderHistory[idx]
                                }
                                isReceiving = false
                            },
                            modifier = Modifier.fillMaxWidth().height(45.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isReceiving && receiveWarehouseId.isNotEmpty()
                        ) {
                            Text(if (isReceiving) "Receiving..." else "Mark as Received", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestockRecommendationCardItem(
    item: RestockRecommendationData,
    isSelected: Boolean,
    onSelectToggle: () -> Unit,
    onCustomize: () -> Unit,
    onPackage: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isSelected) StaffPortalButtonBlue else EmployeePortalBackground)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Checkbox(checked = isSelected, onCheckedChange = { onSelectToggle() })
                    Column {
                        Text(item.productName, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
                        Text("${item.restockId} · ${item.supplierName}", fontSize = 11.sp, color = TextGray)
                    }
                }
                Surface(
                    color = if (item.priority == "Critical") GasTrackRed else Color(0xFF22C55E),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = item.priority,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DataPoint("Stock", item.currentStock.toString())
                DataPoint("Reorder", item.reorderLevel.toString())
                DataPoint("Suggested", item.suggestedQty.toString())
                DataPoint("Confidence", "${item.confidence.toInt()}%")
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = EmployeePortalBackground)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCustomize, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = "Customize", tint = Color(0xFF22C55E), modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onPackage, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Inventory, contentDescription = "Build PO", tint = StaffPortalButtonBlue, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GasTrackRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun DataPoint(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextDark)
    }
}

@Immutable
data class CustomizeRowState(
    val restockId: String,
    val productId: String,
    val productName: String,
    val suggestedQty: Int,
    val preferredQtyText: String,
    val costPrice: Double
)

@Immutable
data class RestockItemList(
    val items: List<RestockRecommendationData>
)

@Composable
fun CustomizeRestockModal(onClose: () -> Unit, restockList: RestockItemList, onApplied: () -> Unit) {
    val items = restockList.items
    val rows = remember(items) {
        mutableStateListOf<CustomizeRowState>().apply {
            addAll(items.map { itm ->
                CustomizeRowState(
                    restockId = itm.restockId,
                    productId = itm.productId,
                    productName = itm.productName,
                    suggestedQty = itm.suggestedQty,
                    preferredQtyText = itm.suggestedQty.toString(),
                    costPrice = itm.costPrice
                )
            })
        }
    }

    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val totalBudget = rows.sumOf { (it.preferredQtyText.toIntOrNull() ?: 0) * it.costPrice }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Restocking Customize", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (error.isNotEmpty()) {
                    Text(error, color = GasTrackRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Selected Items (${rows.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))

                if (rows.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No items selected. Check items in the Restocking table first.", fontSize = 13.sp, color = TextGray)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        rows.forEachIndexed { idx, row ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, EmployeePortalBackground)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text("${idx + 1}. ${row.productName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text("Suggested: ${row.suggestedQty}", fontSize = 11.sp, color = TextGray)
                                    }
                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Preferred", fontSize = 10.sp, color = TextGray)
                                        OutlinedTextField(
                                            value = row.preferredQtyText,
                                            onValueChange = { newText ->
                                                val i = rows.indexOfFirst { r -> r.restockId == row.restockId }
                                                if (i != -1) {
                                                    rows[i] = rows[i].copy(preferredQtyText = newText)
                                                }
                                            },
                                            modifier = Modifier.width(80.dp).height(50.dp),
                                            singleLine = true,
                                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, textAlign = TextAlign.Center)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.End) {
                                        Text("Est. Cost", fontSize = 10.sp, color = TextGray)
                                        val qty = row.preferredQtyText.toIntOrNull() ?: 0
                                        Text(formatPeso(qty * row.costPrice), fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = EmployeePortalBackground)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Estimated Total:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(formatPeso(totalBudget), fontSize = 16.sp, fontWeight = FontWeight.Black, color = GasTrackRed)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            isSubmitting = true
                            onApplied()
                            isSubmitting = false
                            onClose()
                        },
                        modifier = Modifier.weight(1f).height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isSubmitting && rows.isNotEmpty()
                    ) {
                        Text(if (isSubmitting) "Applying..." else "Apply", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomizeSingleItemModal(onClose: () -> Unit, selectedItem: RestockRecommendationData, onSaved: (Int) -> Unit) {
    var qty by remember { mutableStateOf(selectedItem.suggestedQty.toString()) }
    Dialog(onDismissRequest = onClose) {
        Surface(modifier = Modifier.fillMaxWidth(0.85f), shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Customize: ${selectedItem.productName}", fontSize = 16.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Suggested Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(onClick = {
                        val newQty = qty.toIntOrNull() ?: selectedItem.suggestedQty
                        onSaved(newQty)
                    }, modifier = Modifier.weight(1f)) { Text("Save") }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestockingEmployeeScreenPreview() {
    RestockingEmployeeScreen()
}
