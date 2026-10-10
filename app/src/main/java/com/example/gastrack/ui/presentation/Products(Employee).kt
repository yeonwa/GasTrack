package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import java.util.Locale

data class ProductData(
    val id: String,
    val name: String,
    val category: String, // LPG Gas, Cylinder, Accessories
    val brand: String,
    val supplier: String,
    val unit: String, // e.g., Tank, Piece
    val unitPrice: Double,
    val costPrice: Double,
    val reorderLevel: Int,
    val image: String = "",
    val arModel: String = "",
    val status: String = "Active"
)

@Composable
fun ProductsEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    
    // CRUD States
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showViewDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductData?>(null) }
    var productToView by remember { mutableStateOf<ProductData?>(null) }
    var productToDelete by remember { mutableStateOf<ProductData?>(null) }

    val products = remember { mutableStateListOf(
        ProductData("P-001", "11kg Standard Refill", "LPG Gas", "Petron", "Petron Gas Corp", "Tank", 950.0, 800.0, 20),
        ProductData("P-002", "2.7kg Camping Cylinder", "Cylinder", "Gasul", "Metrolist Inc", "Piece", 280.0, 200.0, 10),
        ProductData("P-003", "Gas Regulator (Auto)", "Accessories", "Standard", "Ace Hardware", "Piece", 450.0, 320.0, 50)
    ) }

    val filteredProducts = products.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (selectedCategoryFilter == "All") true else it.category == selectedCategoryFilter
        val matchesStatus = if (selectedStatusFilter == "All") true else it.status == selectedStatusFilter
        matchesSearch && matchesCategory && matchesStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "Catalog Master",
            subtitle = "Product Management",
            onBack = onBack
        )

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                placeholder = { Text("Search Products...", color = Color.LightGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category and Status Filters Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Category Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (selectedCategoryFilter != "All") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (selectedCategoryFilter == "All") "All Categories" else selectedCategoryFilter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategoryFilter != "All") StaffPortalButtonBlue else TextGray,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.45f)) {
                        listOf("All", "LPG Gas", "Cylinder", "Accessories").forEach { cat ->
                            DropdownMenuItem(text = { Text(cat, fontSize = 12.sp) }, onClick = { selectedCategoryFilter = cat; expanded = false })
                        }
                    }
                }

                // Status Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (selectedStatusFilter != "All") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (selectedStatusFilter == "All") "All Status" else selectedStatusFilter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedStatusFilter != "All") StaffPortalButtonBlue else TextGray,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.45f)) {
                        listOf("All", "Active", "Inactive").forEach { status ->
                            DropdownMenuItem(text = { Text(status, fontSize = 12.sp) }, onClick = { selectedStatusFilter = status; expanded = false })
                        }
                    }
                }
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
                    .shadow(8.dp, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Create New Product", fontWeight = FontWeight.Black, fontSize = 15.sp)
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
                        color = StaffPortalButtonBlue
                    )
                }
                Surface(
                    color = if (product.status == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = product.status.uppercase(),
                        color = if (product.status == "Active") Color(0xFF2E7D32) else GasTrackRed,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
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
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RETAIL PRICE", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(String.format(Locale.US, "₱%,.2f", product.unitPrice), fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CATEGORY", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(product.category.take(10), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark, maxLines = 1)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("REORDER", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(product.reorderLevel.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
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
    var category by remember { mutableStateOf(product?.category ?: "LPG Gas") }
    var brand by remember { mutableStateOf(product?.brand ?: "Petron") }
    var supplier by remember { mutableStateOf(product?.supplier ?: "Petron Gas Corp") }
    var productName by remember { mutableStateOf(product?.name ?: "") }
    var unit by remember { mutableStateOf(product?.unit ?: "Tank") }
    var unitPrice by remember { mutableStateOf(product?.unitPrice?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var costPrice by remember { mutableStateOf(product?.costPrice?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var reorderLevel by remember { mutableStateOf(product?.reorderLevel?.toString() ?: "") }
    var status by remember { mutableStateOf(product?.status ?: "Active") }
    var arModelUrl by remember { mutableStateOf(product?.arModel ?: "") }

    var catExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(text = "Product information", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(24.dp))

                // --- Row 1: Product ID & Product Name ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(0.8f)) {
                        Text("Product ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = product?.id ?: "P-XXX", onValueChange = {},
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
                            value = productName, onValueChange = { productName = it },
                            placeholder = { Text("Name", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
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
                                value = category, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("LPG Gas", "Cylinder", "Accessories").forEach { cat ->
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
                                value = supplier, onValueChange = {}, readOnly = true,
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

                // --- Row 3: Unit, Unit Price, Cost Price ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(0.8f)) {
                        Text("Unit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = unitExpanded, onExpandedChange = { unitExpanded = !unitExpanded }) {
                            OutlinedTextField(
                                value = unit, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("Tank", "Piece", "Unit").forEach { u ->
                                    DropdownMenuItem(text = { Text(u) }, onClick = { unit = u; unitExpanded = false })
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1.1f)) {
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
                    Column(modifier = Modifier.weight(1.1f)) {
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

                // --- Row 4: Reorder Level & Status ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 5: AR Model URL ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("AR Model URL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = arModelUrl, onValueChange = { arModelUrl = it },
                        placeholder = { Text("url link", color = Color.LightGray, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Row 6: Image Upload ---
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Image Upload:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.width(16.dp))
                    Surface(
                        modifier = Modifier.height(32.dp).width(120.dp).clickable { /* Upload Logic */ },
                        color = Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Attached File", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // --- Footer Buttons ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)
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
                                unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                                costPrice = costPrice.toDoubleOrNull() ?: 0.0,
                                reorderLevel = reorderLevel.toIntOrNull() ?: 0,
                                status = status,
                                arModel = arModelUrl
                            ))
                        },
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
                    ) {
                        Text(if (product == null) "Add" else "Update", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(), 
            shape = RoundedCornerShape(24.dp), 
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(14.dp), color = StaffPortalButtonBlue.copy(alpha = 0.08f)) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.padding(14.dp), tint = StaffPortalButtonBlue)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = product.name, fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextDark)
                            Text(text = "System ID: ${product.id}", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailValueRow("Category", product.category, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Brand", product.brand)
                DetailValueRow("Supplier", product.supplier)
                DetailValueRow("Unit Type", product.unit)
                DetailValueRow("Retail Price", String.format(Locale.US, "₱%,.2f", product.unitPrice), isBold = true)
                DetailValueRow("Cost Price", String.format(Locale.US, "₱%,.2f", product.costPrice))
                DetailValueRow("Reorder Level", "${product.reorderLevel} ${product.unit}s")
                DetailValueRow("Status", product.status, color = if (product.status == "Active") Color(0xFF2E7D32) else GasTrackRed)
                if (product.arModel.isNotEmpty()) {
                    DetailValueRow("AR Asset", "LINKED", color = StaffPortalButtonBlue)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close Catalog", fontWeight = FontWeight.Black)
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
