package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*
import java.util.Locale

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
        SupplierData("S-001", "ABC Company", "Juan Dela Cruz", "abc@corp.com", "#59 Kahoy St, Malabon", "095647851234", "2", "Manufacturer", "Active"),
        SupplierData("S-002", "XYZ Inc", "Patrick Garcia", "xyz@inc.com", "#12 Rizal Ave, Navotas", "091234567890", "3", "Wholesaler", "Active"),
        SupplierData("S-003", "DEF Company", "Juana Tolentino", "def@co.com", "#8 Bonifacio St, Caloocan", "099876543211", "5", "Manufacturer", "Inactive"),
        SupplierData("S-004", "NOV Inc", "Yvonne Cruz", "nov@biz.com", "#45 Mabini St, Pasig", "094567891232", "2", "Distributor", "Active"),
        SupplierData("S-005", "POW Co", "Andres Rizal", "pow@energy.com", "#59 Kahoy St", "095647851234", "4", "Manufacturer", "Inactive")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "Suppliers",
            subtitle = "Resource Management",
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
            title = { Text("Vendor Termination", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to remove ${supplierToDelete!!.name} from your directory? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    suppliers.remove(supplierToDelete)
                    showDeleteConfirm = false
                }) { Text("Terminate", color = GasTrackRed, fontWeight = FontWeight.Bold) }
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
    suppliers: List<SupplierData>,
    onAddClick: () -> Unit,
    onViewClick: (SupplierData) -> Unit,
    onEditClick: (SupplierData) -> Unit,
    onDeleteClick: (SupplierData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPerformance by remember { mutableStateOf("All Performance") }
    var selectedStatus by remember { mutableStateOf("All Status") }
    var selectedLocation by remember { mutableStateOf("All Locations") }

    val filteredSuppliers = suppliers.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true) || it.contactPerson.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatus == "All Status") true else it.status == selectedStatus
        matchesSearch && matchesStatus
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            placeholder = { Text("Search by Supplier Name, ID or Contact Person", color = Color.LightGray, fontSize = 12.sp) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipDropdown(selectedPerformance) { /* Action */ }
            FilterChipDropdown(selectedStatus) { /* Action */ }
            FilterChipDropdown(selectedLocation) { /* Action */ }
        }

        Spacer(modifier = Modifier.height(20.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Supplier List", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextDark)
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
fun FilterChipDropdown(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(36.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
        color = Color.White
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
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
                        text = supplier.id,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Text(
                        text = supplier.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StaffPortalButtonBlue
                    )
                }
                Surface(
                    color = if (supplier.status == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = supplier.status.uppercase(),
                        color = if (supplier.status == "Active") Color(0xFF2E7D32) else GasTrackRed,
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
                        Text("CONTACT PERSON", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(supplier.contactPerson.split(" ").first(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PHONE NUMBER", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(supplier.contact, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("LEAD TIME", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text("${supplier.leadTimeDays} Days", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
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
            modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Supplier Information", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("BASIC INFORMATION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Supplier ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = supplier?.id ?: "S-XXX", onValueChange = {},
                            readOnly = true, modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFFBFBFE), focusedContainerColor = Color(0xFFFBFBFE), unfocusedBorderColor = Color(0xFFE9ECEF)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                        )
                    }
                    Column(modifier = Modifier.weight(2.5f)) {
                        Text("Full Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = name, onValueChange = { name = it },
                            placeholder = { Text("ABC Company", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = StaffPortalButtonBlue),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text("Supplier Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = !typeExpanded }) {
                            OutlinedTextField(
                                value = supplierType, onValueChange = {}, readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            )
                            ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }, modifier = Modifier.background(Color.White)) {
                                listOf("Manufacturer", "Wholesaler", "Distributor").forEach { t ->
                                    DropdownMenuItem(text = { Text(t) }, onClick = { supplierType = t; typeExpanded = false })
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Default Lead Times", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = leadTimeDays, onValueChange = { if(it.all { c -> c.isDigit() }) leadTimeDays = it },
                            placeholder = { Text("Enter Number", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
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

                Spacer(modifier = Modifier.height(24.dp))
                Text("CONTACT INFORMATION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Contact Person", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = contactPerson, onValueChange = { contactPerson = it },
                            placeholder = { Text("Juan Dela Cruz", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Email Address", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = email, onValueChange = { email = it },
                            placeholder = { Text("abccompany@supplier.com", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Phone Number", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = contact, onValueChange = { contact = it },
                            placeholder = { Text("09875412558", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text("Address", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = address, onValueChange = { address = it },
                            placeholder = { Text("146 Makinang Manila City", color = Color.LightGray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF)),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)
                    ) {
                        Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Black)
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
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when(selectedTab) {
                            "Supplier Products" -> "Supplier View"
                            "Purchase Order" -> "Supplier View - PO"
                            else -> "Supplier View - Delivery"
                        }, 
                        fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Tab Row for Mobile
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), 
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
                                Box(modifier = Modifier.height(3.dp).width(30.dp).background(StaffPortalButtonBlue, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when(selectedTab) {
                        "Supplier Products" -> {
                            val mockProducts = listOf(
                                listOf("P-001", "Gas LPG 2.7 kg", "₱ 243.00", "2 days", "10", "Active"),
                                listOf("P-002", "Gas LPG 7kg", "₱ 603.00", "2 days", "10", "Active"),
                                listOf("P-003", "Gas LPG 11kg", "₱ 921.00", "2 days", "10", "Active")
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(mockProducts) { row ->
                                    SupplierProductCard(
                                        id = row[0],
                                        name = row[1],
                                        cost = row[2],
                                        lead = row[3],
                                        minQty = row[4],
                                        status = row[5]
                                    )
                                }
                            }
                        }
                        "Purchase Order" -> {
                            val mockPO = listOf(
                                listOf("PO-001", "01/05/2026", "20", "₱ 4,860.00", "01/07/2026", "Sent"),
                                listOf("PO-002", "01/05/2026", "20", "₱ 12,060.00", "01/07/2026", "Sent")
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(mockPO) { row ->
                                    SupplierGenericDataCard(
                                        id = row[0],
                                        name = "Date: ${row[1]}",
                                        label1 = "TOTAL QTY", value1 = row[2],
                                        label2 = "TOTAL AMOUNT", value2 = row[3],
                                        label3 = "EXPECTED", value3 = row[4],
                                        status = row[5]
                                    )
                                }
                            }
                        }
                        "Delivery History" -> {
                            val mockDelivery = listOf(
                                listOf("SD-001", "PO Ref: PO-001", "01/07/2026", "20", "Received", "U-001"),
                                listOf("SD-002", "PO Ref: PO-002", "01/07/2026", "20", "Received", "U-002")
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(mockDelivery) { row ->
                                    SupplierGenericDataCard(
                                        id = row[0],
                                        name = row[1],
                                        label1 = "DATE", value1 = row[2],
                                        label2 = "TOTAL QTY", value2 = row[3],
                                        label3 = "RECEIVED BY", value3 = row[5],
                                        status = row[4]
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = TextDark)
                ) {
                    Text("Close Overview", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}


@Composable
fun SupplierProductCard(
    id: String,
    name: String,
    cost: String,
    lead: String,
    minQty: String,
    status: String
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
                        text = id,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StaffPortalButtonBlue
                    )
                }
                Surface(
                    color = if (status == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        color = if (status == "Active") Color(0xFF2E7D32) else GasTrackRed,
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
                        Text("COST PRICE", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(cost, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("LEAD TIME", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(lead, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("MIN QTY", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(minQty, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SupplierGenericDataCard(
    id: String,
    name: String,
    label1: String, value1: String,
    label2: String, value2: String,
    label3: String, value3: String,
    status: String
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
                        text = id,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StaffPortalButtonBlue
                    )
                }
                Surface(
                    color = when(status.lowercase()) {
                        "active", "received" -> Color(0xFFE8F5E9)
                        "inactive", "cancelled" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        color = when(status.lowercase()) {
                            "active", "received" -> Color(0xFF2E7D32)
                            "inactive", "cancelled" -> GasTrackRed
                            else -> StaffPortalButtonBlue
                        },
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
                        Text(label1, fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(value1, fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label2, fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(value2, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(label3, fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                        Text(value3, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Delete, contentDescription = null, tint = GasTrackRed, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuppliersEmployeeScreenPreview() {
    SuppliersEmployeeScreen()
}
