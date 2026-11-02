package com.example.gastrack.ui.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*

@Immutable
data class SupplierData(
    val id: String,
    val name: String,
    val contactPerson: String,
    val email: String,
    val address: String,
    val contact: String,
    val leadTimeDays: String,
    val supplierType: String = "Manufacturer",
    val status: String // Active, Inactive
)

fun formatPhoneNumber(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return when (digits.length) {
        11 -> "${digits.substring(0, 4)} ${digits.substring(4, 7)} ${digits.substring(7)}"
        12 -> "${digits.substring(0, 4)} ${digits.substring(4, 7)} ${digits.substring(7, 11)}"
        10 -> "${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6)}"
        else -> if (phone.isNotBlank()) phone else "—"
    }
}

fun formatDateString(dateStr: String): String {
    if (dateStr.contains("/")) {
        val parts = dateStr.split("/")
        if (parts.size == 3) {
            val month = parts[0].toIntOrNull() ?: return dateStr
            val day = parts[1].padStart(2, '0')
            val year = parts[2]
            val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            if (month in 1..12) {
                return "${monthNames[month]} $day, $year"
            }
        }
    }
    return dateStr
}

@Composable
fun SuppliersEmployeeScreen(
    onBack: () -> Unit = {}
) {
    // CRUD States
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showViewDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    var supplierToEdit by remember { mutableStateOf<SupplierData?>(null) }
    var supplierToView by remember { mutableStateOf<SupplierData?>(null) }
    var supplierToDelete by remember { mutableStateOf<SupplierData?>(null) }

    val suppliers = remember { mutableStateListOf(
        SupplierData("S-001", "ABC Company", "Juan Dela Cruz", "abc@corp.com", "#59 Kahoy St, Malabon", "09564785123", "2", "Manufacturer", "Active"),
        SupplierData("S-002", "XYZ Inc", "Patrick Garcia", "xyz@inc.com", "#12 Rizal Ave, Navotas", "09123456789", "3", "Wholesaler", "Active"),
        SupplierData("S-003", "DEF Company", "Juana Tolentino", "def@co.com", "#8 Bonifacio St, Caloocan", "09987654321", "5", "Manufacturer", "Inactive"),
        SupplierData("S-004", "NOV Inc", "Yvonne Cruz", "nov@biz.com", "#45 Mabini St, Pasig", "09456789123", "2", "Distributor", "Active"),
        SupplierData("S-005", "POW Co", "Andres Rizal", "pow@energy.com", "#59 Kahoy St", "09564785123", "4", "Manufacturer", "Inactive")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeePortalBackground)
    ) {
        EmployeePortalHeader(
            title = "Suppliers",
            onBack = onBack
        )

        SuppliersDirectoryTab(
            suppliers = suppliers,
            onAddClick = { showAddDialog = true },
            onViewClick = {
                supplierToView = it
                showViewDialog = true
            },
            onEditClick = {
                supplierToEdit = it
                showEditDialog = true
            },
            onDeleteClick = { 
                supplierToDelete = it
                showDeleteConfirm = true
            }
        )
    }

    if (showAddDialog) {
        SupplierEditDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newSupplier ->
                suppliers.add(newSupplier.copy(id = "S-00${suppliers.size + 1}"))
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && supplierToEdit != null) {
        SupplierEditDialog(
            supplier = supplierToEdit,
            onDismiss = { showEditDialog = false },
            onSave = { updatedSupplier ->
                val index = suppliers.indexOfFirst { it.id == updatedSupplier.id }
                if (index != -1) suppliers[index] = updatedSupplier
                showEditDialog = false
            }
        )
    }

    if (showViewDialog && supplierToView != null) {
        SupplierDetailDialog(supplier = supplierToView!!, onDismiss = { showViewDialog = false })
    }

    if (showDeleteConfirm && supplierToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Deactivate Supplier", fontWeight = FontWeight.Black) },
            text = { Text("Deactivate \"${supplierToDelete!!.name}\"? Their product and purchase order history will be kept, and they'll stop appearing as an option when adding new products.") },
            confirmButton = {
                TextButton(onClick = {
                    suppliers.remove(supplierToDelete)
                    showDeleteConfirm = false
                }) { Text("Deactivate", color = GasTrackRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun SuppliersDirectoryTab(
    suppliers: androidx.compose.runtime.snapshots.SnapshotStateList<SupplierData>,
    onAddClick: () -> Unit,
    onViewClick: (SupplierData) -> Unit,
    onEditClick: (SupplierData) -> Unit,
    onDeleteClick: (SupplierData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All Status") }

    val filteredSuppliers by remember(suppliers, searchQuery, selectedStatus) {
        derivedStateOf {
            suppliers.filter {
                val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true) || it.contactPerson.contains(searchQuery, ignoreCase = true)
                val matchesStatus = selectedStatus == "All Status" || it.status == selectedStatus
                matchesSearch && matchesStatus
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        
        EmployeeSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search suppliers..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilterDropdown(
            options = listOf("All Status", "Active", "Inactive"),
            selectedOption = selectedStatus,
            onOptionSelected = { selectedStatus = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.SpaceBetween, 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Supplier List", 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Black, 
                color = TextDark
            )
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Supplier", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSuppliers) { supplier ->
                SupplierCardMobile(
                    supplier = supplier,
                    onView = { onViewClick(supplier) },
                    onEdit = { onEditClick(supplier) },
                    onDelete = { onDeleteClick(supplier) }
                )
            }
        }
    }
}


@Composable
fun SupplierCardMobile(
    supplier: SupplierData,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplier.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = supplier.id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = if (supplier.status == "Active") StatusActive.copy(alpha = 0.15f) else StatusInactive.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = supplier.status.uppercase(),
                        color = if (supplier.status == "Active") StatusActive else StatusInactive,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color(0xFFFBFBFE),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("CONTACT PERSON", fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = supplier.contactPerson,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFE9ECEF)))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("PHONE NUMBER", fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatPhoneNumber(supplier.contact),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFE9ECEF)))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("LEAD TIME", fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        val leadTimeText = remember(supplier.leadTimeDays) {
                            val clean = supplier.leadTimeDays.trim()
                            if (clean.endsWith("day", ignoreCase = true) || clean.endsWith("days", ignoreCase = true)) {
                                clean
                            } else {
                                if (clean == "1") "1 Day" else "$clean Days"
                            }
                        }
                        Text(
                            text = leadTimeText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onView, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "View", tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GasTrackRed, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    containerColor: Color = Color.White,
    borderColor: Color = Color(0xFFD1D5DB),
    textStyle: TextStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        readOnly = readOnly,
        enabled = enabled,
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = if (placeholder.isNotEmpty()) {
                    { Text(placeholder, color = Color(0xFF6B7280), fontSize = 13.sp) }
                } else null,
                trailingIcon = trailingIcon,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = containerColor,
                            focusedContainerColor = containerColor,
                            disabledContainerColor = containerColor,
                            unfocusedBorderColor = borderColor,
                            focusedBorderColor = if (readOnly) borderColor else StaffPortalButtonBlue,
                            disabledBorderColor = borderColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierEditDialog(
    supplier: SupplierData? = null,
    onDismiss: () -> Unit,
    onSave: (SupplierData) -> Unit
) {
    var name by remember { mutableStateOf(supplier?.name ?: "") }
    var contactPerson by remember { mutableStateOf(supplier?.contactPerson ?: "") }
    var email by remember { mutableStateOf(supplier?.email ?: "") }
    var address by remember { mutableStateOf(supplier?.address ?: "") }
    var contact by remember { mutableStateOf(supplier?.contact ?: "") }
    var leadTimeDays by remember { mutableStateOf(supplier?.leadTimeDays ?: "") }
    var supplierType by remember { mutableStateOf(supplier?.supplierType ?: "Manufacturer") }
    var status by remember { mutableStateOf(supplier?.status ?: "Active") }

    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                    )

                    // Header with Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Supplier Information", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close form", tint = TextGray)
                        }
                    }
                    
                    Text("BASIC INFORMATION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)

                    // Supplier ID & Full Name
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Supplier ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Spacer(modifier = Modifier.height(6.dp))
                            FormInputField(
                                value = supplier?.id ?: "S-XXX",
                                onValueChange = {},
                                readOnly = true,
                                containerColor = Color(0xFFF1F5F9),
                                borderColor = Color.Transparent,
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            )
                        }
                        Column(modifier = Modifier.weight(1.8f)) {
                            Text("Full Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Spacer(modifier = Modifier.height(6.dp))
                            FormInputField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = "ABC Company"
                            )
                        }
                    }

                    // Supplier Type & Lead Times
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("Supplier Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray, maxLines = 1, softWrap = false)
                            Spacer(modifier = Modifier.height(6.dp))
                            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = !typeExpanded }) {
                                FormInputField(
                                    value = supplierType,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }, modifier = Modifier.background(Color.White)) {
                                    listOf("Manufacturer", "Wholesaler", "Distributor").forEach { t ->
                                        DropdownMenuItem(text = { Text(t) }, onClick = { supplierType = t; typeExpanded = false })
                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Lead Time (Days)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray, maxLines = 1, softWrap = false)
                            Spacer(modifier = Modifier.height(6.dp))
                            FormInputField(
                                value = leadTimeDays,
                                onValueChange = { if (it.all { c -> c.isDigit() }) leadTimeDays = it },
                                placeholder = "Enter Number",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    // Status
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded }) {
                            FormInputField(
                                value = status,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("Active", "Inactive").forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusExpanded = false })
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text("CONTACT INFORMATION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)

                    // Contact Person
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Contact Person", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        FormInputField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            placeholder = "Juan Dela Cruz"
                        )
                    }

                    // Email Address
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Email Address", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        FormInputField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "supplier@domain.com"
                        )
                    }

                    // Phone Number
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Phone Number", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        FormInputField(
                            value = contact,
                            onValueChange = { contact = it },
                            placeholder = "09875412558",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }

                    // Address
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Address", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        FormInputField(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = "Manila City"
                        )
                    }
                }

                // Sticky Footer with Top Border and Elevation
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)
                        ) {
                            Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { 
                                onSave(SupplierData(
                                    id = supplier?.id ?: "S-${System.currentTimeMillis() % 1000}",
                                    name = name,
                                    contactPerson = contactPerson,
                                    email = email,
                                    address = address,
                                    contact = contact,
                                    leadTimeDays = leadTimeDays,
                                    supplierType = supplierType,
                                    status = status
                                ))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                        ) {
                            Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupplierDetailDialog(supplier: SupplierData, onDismiss: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Supplier Products") }
    val tabs = listOf("Supplier Products", "Purchase Order", "Delivery History")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(supplier.name, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${supplier.contactPerson} · ${formatPhoneNumber(supplier.contact)} · ${supplier.email}", fontSize = 12.sp, color = TextGray)
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Scrollable Tab Row for Mobile
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = selectedTab == tab
                            Column(modifier = Modifier.clickable { selectedTab = tab }, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = tab,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) StaffPortalButtonBlue else TextGray,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                if (isSelected) {
                                    Box(modifier = Modifier
                                        .height(3.dp)
                                        .width(30.dp)
                                        .background(
                                            StaffPortalButtonBlue,
                                            RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                                        ))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        when(selectedTab) {
                            "Supplier Products" -> {
                                val mockProducts = listOf(
                                    listOf("P-2C6BAD10", "Gasul LPG 2.7 kg", "₱ 200.00", "kg", "30", "Active"),
                                    listOf("P-2C6BB5AE", "Gasul LPG 7 kg", "₱ 560.00", "kg", "20", "Active"),
                                    listOf("P-2C6BB681", "Gasul LPG 11 kg Elite", "₱ 850.00", "kg", "40", "Active"),
                                    listOf("P-2C6BB6E9", "Gasul LPG 11 kg", "₱ 870.00", "kg", "50", "Active"),
                                    listOf("P-2C6BB752", "Gasul LPG 22 kg", "₱ 1,680.00", "kg", "15", "Active"),
                                    listOf("P-2C6BB7BB", "Gasul LPG 50 kg", "₱ 3,890.00", "kg", "10", "Active"),
                                    listOf("P-3SPQAVRP", "Cylinder 11 kg", "₱ 870.00", "kg", "50", "Active"),
                                    listOf("P-6QRURL4V", "Cylinder 22 kg", "₱ 1,680.00", "kg", "15", "Active"),
                                    listOf("P-78GEBL8H", "Cylinder LPG 7 kg", "₱ 560.00", "kg", "20", "Active")
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(mockProducts) { row ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, EmployeePortalBackground)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(row[1], fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                                    Text(row[5], fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("ID: ${row[0]} | Unit: ${row[3]} | Reorder: ${row[4]}", fontSize = 11.sp, color = TextGray)
                                                    Text("Cost: ${row[2]}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "Purchase Order" -> {
                                val mockPO = listOf(
                                    listOf("PO-2026-001", "Jan 05, 2026", "₱ 42,500.00", "Jan 08, 2026", "Submitted"),
                                    listOf("PO-2026-002", "Jan 06, 2026", "₱ 21,250.00", "Jan 09, 2026", "Sent")
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(mockPO) { row ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, EmployeePortalBackground)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                    Text(row[0], fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StaffPortalButtonBlue)
                                                    Surface(
                                                        color = Color(0xFFDBEAFE),
                                                        shape = RoundedCornerShape(999.dp)
                                                    ) {
                                                        Text(
                                                            text = row[4],
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF1E3A8A)
                                                        )
                                                    }
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Date: ${formatDateString(row[1])}", fontSize = 11.sp, color = TextGray)
                                                    Text("Expected: ${formatDateString(row[3])}", fontSize = 11.sp, color = TextGray)
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Total Amount:", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                                                    Text(row[2], fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextDark)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "Delivery History" -> {
                                val mockDelivery = listOf(
                                    listOf("PO-2026-001", "Jan 08, 2026", "50", "Received"),
                                    listOf("PO-2026-002", "Jan 09, 2026", "30", "Received")
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(mockDelivery) { row ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, EmployeePortalBackground)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                    Text(row[0], fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                                    Surface(
                                                        color = Color(0xFFDCFCE7),
                                                        shape = RoundedCornerShape(999.dp)
                                                    ) {
                                                        Text(
                                                            text = row[3],
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF166534)
                                                        )
                                                    }
                                                }
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("Date Received: ${formatDateString(row[1])}", fontSize = 11.sp, color = TextGray)
                                                    Text("Total Qty: ${row[2]}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Solid White Footer Block with Top Border & Elevation Shadow
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(140.dp)
                                .height(42.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = TextDark)
                        ) {
                            Text("Back", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuppliersEmployeeScreenPreview() {
    SuppliersEmployeeScreen()
}
