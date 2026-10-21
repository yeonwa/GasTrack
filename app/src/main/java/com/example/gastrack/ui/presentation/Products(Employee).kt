package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

data class ProductData(
    val id: String,
    val name: String,
    val category: String, // LPG Gas, Cylinder, Accessories
    val brand: String,
    val supplier: String,
    val unit: String, // e.g., Tank, Piece, Kg, Meter
    val unitPrice: Double,
    val costPrice: Double,
    val reorderLevel: Int,
    val currentStock: Int = 0,
    val unitValue: Double = 1.0,
    val imageUrl: String = "",
    val arModel: String = "",
    val status: String = "Active"
)

@Composable
fun ProductsEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All Categories") }
    var selectedStatusFilter by remember { mutableStateOf("All Status") }
    
    // CRUD States
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showViewDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductData?>(null) }
    var productToView by remember { mutableStateOf<ProductData?>(null) }
    var productToDelete by remember { mutableStateOf<ProductData?>(null) }

    val products = remember { mutableStateListOf(
        ProductData(
            id = "P-001", name = "Petron Gasul LPG 50 kg", category = "Gasul LPG", brand = "Petron", supplier = "Petron Gas Corp",
            unit = "kg", unitPrice = 950.0, costPrice = 800.0, reorderLevel = 20, currentStock = 45, unitValue = 50.0, imageUrl = "gasul-50kg.png", status = "Active"
        ),
        ProductData(
            id = "P-002", name = "Gasul Cylinder 2.7 kg", category = "Cylinder", brand = "Gasul", supplier = "Metrolist Inc",
            unit = "kg", unitPrice = 280.0, costPrice = 200.0, reorderLevel = 10, currentStock = 5, unitValue = 2.7, status = "Active"
        ),
        ProductData(
            id = "P-003", name = "Gas Regulator (Auto)", category = "Accessories", brand = "Standard", supplier = "Ace Hardware",
            unit = "pc", unitPrice = 450.0, costPrice = 320.0, reorderLevel = 50, currentStock = 52, unitValue = 1.0, status = "Active"
        )
    ) }

    val filteredProducts = products.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "All Categories" || it.category == selectedCategoryFilter
        val matchesStatus = selectedStatusFilter == "All Status" || it.status == selectedStatusFilter
        matchesSearch && matchesCategory && matchesStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F8)) // Matched web background #f3f5f8
    ) {
        EmployeePortalHeader(
            title = "Products",
            onBack = onBack
        )

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            
            EmployeeSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search Product"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category and Status Filters Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterDropdown(
                    options = listOf("All Categories", "Gasul LPG", "Cylinder", "Accessories"),
                    selectedOption = selectedCategoryFilter,
                    onOptionSelected = { selectedCategoryFilter = it },
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    options = listOf("All Status", "Active", "Inactive"),
                    selectedOption = selectedStatusFilter,
                    onOptionSelected = { selectedStatusFilter = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductManagementCard(
                        product = product,
                        onView = {
                            productToView = product
                            showViewDialog = true
                        },
                        onEdit = {
                            productToEdit = product
                            showEditDialog = true
                        },
                        onDelete = { 
                            productToDelete = product
                            showDeleteConfirm = true
                        }
                    )
                }
            }

            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0082FB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Add Product", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }

    if (showAddDialog) {
        ProductEditDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newProd ->
                products.add(newProd.copy(id = "P-${(products.size + 1).toString().padStart(3, '0')}"))
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && productToEdit != null) {
        ProductEditDialog(
            product = productToEdit,
            onDismiss = { showEditDialog = false },
            onSave = { updatedProd ->
                val index = products.indexOfFirst { it.id == updatedProd.id }
                if (index != -1) products[index] = updatedProd
                showEditDialog = false
            }
        )
    }

    if (showViewDialog && productToView != null) {
        ProductDetailDialog(product = productToView!!, onDismiss = { showViewDialog = false })
    }

    if (showDeleteConfirm && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Remove from Catalog", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to delete ${productToDelete!!.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    products.remove(productToDelete)
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
fun ProductManagementCard(
    product: ProductData,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = product.id,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0082FB)
                    )
                    if (product.supplier.isNotEmpty()) {
                        Text(
                            text = "Supplier: ${product.supplier}",
                            fontSize = 12.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Surface(
                    color = if (product.status == "Active") StatusActive.copy(alpha = 0.15f) else StatusInactive.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = product.status,
                        color = if (product.status == "Active") StatusActive else StatusInactive,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = Color(0xFFFBFBFE),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("PRICE", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(String.format(Locale.US, "₱%,.2f", product.unitPrice), fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("STOCK", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        val isLowStock = product.currentStock <= product.reorderLevel
                        Text("${product.currentStock}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isLowStock) GasTrackRed else TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("REORDER", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(product.reorderLevel.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.End) {
                        Text("CATEGORY", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(product.category.take(8), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onView, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "View", tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GasTrackRed, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditDialog(
    product: ProductData? = null,
    onDismiss: () -> Unit,
    onSave: (ProductData) -> Unit
) {
    var category by remember { mutableStateOf(product?.category ?: "Gasul LPG") }
    var brand by remember { mutableStateOf(product?.brand ?: "") }
    var supplier by remember { mutableStateOf(product?.supplier ?: "") }
    
    var unit by remember { mutableStateOf(product?.unit ?: "pc") }
    var unitValue by remember { mutableStateOf(product?.unitValue?.let { if (it % 1.0 == 0.0) it.toInt().toString() else String.format(Locale.US, "%.1f", it) } ?: "1") }
    
    var customName by remember { mutableStateOf(product != null) }
    var inputName by remember { mutableStateOf(product?.name ?: "") }
    
    // Auto-fill logic similar to React
    val canDeriveName = category.equals("Cylinder", ignoreCase = true) || category.equals("Gasul LPG", ignoreCase = true)
    val suggestedName = remember(brand, category, unitValue, unit) {
        listOf(brand, category, unitValue, unit)
            .filter { it.isNotBlank() && it != "Select Brand" && it != "Select Category" && it != "Select Unit" }
            .joinToString(" ")
    }
    val productName = if (canDeriveName && !customName) suggestedName else inputName

    var unitPrice by remember { mutableStateOf(product?.unitPrice?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var costPrice by remember { mutableStateOf(product?.costPrice?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var currentStock by remember { mutableStateOf(product?.currentStock?.toString() ?: "0") }
    var reorderLevel by remember { mutableStateOf(product?.reorderLevel?.toString() ?: "") }
    var status by remember { mutableStateOf(product?.status ?: "Active") }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var arModelUrl by remember { mutableStateOf(product?.arModel ?: "") }

    var catExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(text = if (product == null) "Product Information" else "Edit Product", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(24.dp))

                // --- Row 1: Product ID & Product Name ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(0.8f)) {
                        Text("Product ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = product?.id ?: "(auto-generated)", onValueChange = {},
                            readOnly = true, modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFFBFBFE), focusedContainerColor = Color(0xFFFBFBFE), unfocusedBorderColor = Color(0xFFE9ECEF)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                        )
                    }
                    Column(modifier = Modifier.weight(2.2f)) {
                        Text("Product Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = productName, onValueChange = { 
                                inputName = it
                                customName = true
                            },
                            placeholder = { Text("Enter product name", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = Color(0xFF0082FB)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                        if (canDeriveName) {
                            Spacer(modifier = Modifier.height(4.dp))
                            if (customName) {
                                TextButton(
                                    onClick = { customName = false },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(20.dp)
                                ) {
                                    Text("Use generated name", fontSize = 10.sp, color = Color(0xFF0082FB))
                                }
                            } else {
                                Text("Auto-filled from brand, category, unit value and unit.", fontSize = 10.sp, color = TextGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 2: Category & Supplier ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
                            OutlinedTextField(
                                value = category.ifEmpty { "Select Category" }, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("Gasul LPG", "Cylinder", "Accessories").forEach { cat ->
                                    DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; catExpanded = false })
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Supplier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = supplierExpanded, onExpandedChange = { supplierExpanded = !supplierExpanded }) {
                            OutlinedTextField(
                                value = supplier.ifEmpty { "Select Supplier" }, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = supplierExpanded, onDismissRequest = { supplierExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("Petron Gas Corp", "Solane", "M-Gas", "Phoenix").forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { supplier = s; supplierExpanded = false })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 2b: Brand & Unit ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Brand", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = brandExpanded, onExpandedChange = { brandExpanded = !brandExpanded }) {
                            OutlinedTextField(
                                value = brand.ifEmpty { "Select Brand" }, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = brandExpanded, onDismissRequest = { brandExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("Petron", "Gasul", "Standard", "Solane").forEach { b ->
                                    DropdownMenuItem(text = { Text(b) }, onClick = { brand = b; brandExpanded = false })
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = unitExpanded, onExpandedChange = { unitExpanded = !unitExpanded }) {
                            OutlinedTextField(
                                value = unit.ifEmpty { "Select Unit" }, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("pc", "kg", "m").forEach { u ->
                                    DropdownMenuItem(text = { Text(u) }, onClick = { unit = u; unitExpanded = false })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Unit Value", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = unitValue, onValueChange = { unitValue = it },
                        placeholder = { Text("e.g. 11 or 2.5", color = Color.LightGray, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = Color(0xFF0082FB)),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 3: Unit Price & Cost Price ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit Price", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = unitPrice, onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) unitPrice = it },
                            placeholder = { Text("₱ 0.00", color = Color.LightGray, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cost Price", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = costPrice, onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) costPrice = it },
                            placeholder = { Text("₱ 0.00", color = Color.LightGray, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 4: Current Stock & Reorder Level ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Current Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = currentStock, onValueChange = { if (it.all { char -> char.isDigit() }) currentStock = it },
                            placeholder = { Text("0", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reorder Level", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = reorderLevel, onValueChange = { if (it.all { char -> char.isDigit() }) reorderLevel = it },
                            placeholder = { Text("Enter Number", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 5: Status & Image URL ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded }) {
                            OutlinedTextField(
                                value = status, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("Active", "Inactive").forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusExpanded = false })
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Image URL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = imageUrl, onValueChange = { imageUrl = it },
                            placeholder = { Text("filename.png or URL", color = Color.LightGray, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = Color(0xFF0082FB)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Use filename or full https URL.", fontSize = 10.sp, color = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 6: AR Model URL ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("AR Model URL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = arModelUrl, onValueChange = { arModelUrl = it },
                        placeholder = { Text("URL link", color = Color.LightGray, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // --- Footer Buttons ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF111827))
                    ) {
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { 
                            onSave(ProductData(
                                id = product?.id ?: "",
                                name = productName,
                                category = category,
                                brand = brand,
                                supplier = supplier,
                                unit = unit,
                                unitValue = unitValue.toDoubleOrNull() ?: 1.0,
                                unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                                costPrice = costPrice.toDoubleOrNull() ?: 0.0,
                                reorderLevel = reorderLevel.toIntOrNull() ?: 0,
                                currentStock = currentStock.toIntOrNull() ?: 0,
                                imageUrl = imageUrl,
                                status = status,
                                arModel = arModelUrl
                            ))
                        },
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0082FB))
                    ) {
                        Text(if (product == null) "Add" else "Save", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailDialog(product: ProductData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight(), 
            shape = RoundedCornerShape(12.dp), 
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Product Details", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF111827))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                @Composable
                fun DetailRow(label: String, value: String) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(text = "$label: ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.weight(0.4f))
                        Text(text = value, fontSize = 14.sp, color = Color(0xFF4B5563), modifier = Modifier.weight(0.6f))
                    }
                }
                
                DetailRow("Product ID", product.id)
                DetailRow("Name", product.name)
                DetailRow("Category", product.category)
                DetailRow("Brand", product.brand)
                DetailRow("Supplier", product.supplier)
                DetailRow("Unit", product.unit)
                DetailRow("Unit Value", product.unitValue.toString())
                DetailRow("Unit Price", String.format(Locale.US, "₱%,.2f", product.unitPrice))
                DetailRow("Cost Price", String.format(Locale.US, "₱%,.2f", product.costPrice))
                DetailRow("Reorder Level", product.reorderLevel.toString())
                DetailRow("Status", product.status)
                if (product.arModel.isNotEmpty()) {
                    DetailRow("AR Asset", product.arModel)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss, 
                    modifier = Modifier.fillMaxWidth().height(44.dp), 
                    shape = RoundedCornerShape(8.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF111827))
                ) {
                    Text("Close", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProductsEmployeeScreenPreview() {
    ProductsEmployeeScreen()
}
