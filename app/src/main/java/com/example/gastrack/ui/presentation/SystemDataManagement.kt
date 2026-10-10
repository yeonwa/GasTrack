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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

enum class ManagementTab {
    COMPANY, BRANCH, WAREHOUSE
}

data class CompanyData(
    val id: String,
    val name: String,
    val dtiNo: String,
    val doeNo: String
)

data class BranchData(
    val id: String,
    val companyId: String,
    val name: String,
    val address: String,
    val contact: String,
    val status: String
)

data class WarehouseData(
    val id: String,
    val name: String,
    val location: String,
    val status: String
)

@Composable
fun SystemDataManagementScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(ManagementTab.COMPANY) }
    var showForm by remember { mutableStateOf(false) }
    var currentEditingEntity by remember { mutableStateOf<Any?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var entityToDelete by remember { mutableStateOf<Any?>(null) }

    // Mock Data States
    val companies = remember { mutableStateListOf(
        CompanyData("C-001", "ABC Gas Corp", "DTI-123456", "DOE-789012")
    ) }
    val branches = remember { mutableStateListOf(
        BranchData("BR-001", "C-001", "Main Branch", "Malabon City", "09123456789", "Active"),
        BranchData("BR-002", "C-001", "South Branch", "Pasay City", "09987654321", "Active")
    ) }
    val warehouses = remember { mutableStateListOf(
        WarehouseData("WH-001", "Warehouse A", "Pasig City", "Active"),
        WarehouseData("WH-002", "Warehouse B", "Taguig City", "Active")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFE))
    ) {
        EmployeePortalHeader(
            title = when(selectedTab) {
                ManagementTab.COMPANY -> "Company Profile"
                ManagementTab.BRANCH -> "Branch Network"
                ManagementTab.WAREHOUSE -> "Storage Facilities"
            },
            subtitle = "System Data",
            onBack = if (showForm) ({ showForm = false }) else onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!showForm) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                EmployeeTabSelector(
                    tabs = listOf("Company", "Branch", "Warehouse"),
                    selectedTab = selectedTab.name.lowercase().replaceFirstChar { it.uppercase() },
                    onTabSelected = { name ->
                        selectedTab = ManagementTab.valueOf(name.uppercase())
                    }
                )
            }
        }

        Crossfade(targetState = showForm, label = "FormTransition") { isForm ->
            if (isForm) {
                EntityForm(
                    tab = selectedTab,
                    entity = currentEditingEntity,
                    onSave = { updatedEntity ->
                        when (selectedTab) {
                            ManagementTab.COMPANY -> {
                                val company = updatedEntity as CompanyData
                                val index = companies.indexOfFirst { it.id == company.id }
                                if (index != -1) companies[index] = company else companies.add(company)
                            }
                            ManagementTab.BRANCH -> {
                                val branch = updatedEntity as BranchData
                                val index = branches.indexOfFirst { it.id == branch.id }
                                if (index != -1) branches[index] = branch else branches.add(branch)
                            }
                            ManagementTab.WAREHOUSE -> {
                                val warehouse = updatedEntity as WarehouseData
                                val index = warehouses.indexOfFirst { it.id == warehouse.id }
                                if (index != -1) warehouses[index] = warehouse else warehouses.add(warehouse)
                            }
                        }
                        showForm = false
                    },
                    onCancel = { showForm = false }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        placeholder = { Text("Filter By Name...", color = Color.LightGray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE9ECEF),
                            focusedBorderColor = GasTrackBlue,
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedTab.name} RECORDS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = TextGray,
                            letterSpacing = 1.2.sp
                        )
                        Button(
                            onClick = { 
                                currentEditingEntity = null
                                showForm = true 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        when (selectedTab) {
                            ManagementTab.COMPANY -> {
                                val filtered = companies.filter { it.name.contains(searchQuery, ignoreCase = true) }
                                items(filtered) { company ->
                                    ManagementCard(
                                        title = company.name,
                                        details = listOf("DTI: ${company.dtiNo}", "DOE: ${company.doeNo}", "ID: ${company.id}"),
                                        onEdit = { 
                                            currentEditingEntity = company
                                            showForm = true 
                                        },
                                        onDelete = {
                                            entityToDelete = company
                                            showDeleteConfirm = true
                                        }
                                    )
                                }
                            }
                            ManagementTab.BRANCH -> {
                                val filtered = branches.filter { it.name.contains(searchQuery, ignoreCase = true) }
                                items(filtered) { branch ->
                                    ManagementCard(
                                        title = branch.name,
                                        details = listOf("ID: ${branch.id}", "Address: ${branch.address}", "Status: ${branch.status}"),
                                        onEdit = { 
                                            currentEditingEntity = branch
                                            showForm = true 
                                        },
                                        onDelete = {
                                            entityToDelete = branch
                                            showDeleteConfirm = true
                                        }
                                    )
                                }
                            }
                            ManagementTab.WAREHOUSE -> {
                                val filtered = warehouses.filter { it.name.contains(searchQuery, ignoreCase = true) }
                                items(filtered) { wh ->
                                    ManagementCard(
                                        title = wh.name,
                                        details = listOf("ID: ${wh.id}", "Location: ${wh.location}", "Status: ${wh.status}"),
                                        onEdit = { 
                                            currentEditingEntity = wh
                                            showForm = true 
                                        },
                                        onDelete = {
                                            entityToDelete = wh
                                            showDeleteConfirm = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm && entityToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Confirm Removal", fontWeight = FontWeight.Black) },
            text = { 
                val name = when(entityToDelete) {
                    is CompanyData -> (entityToDelete as CompanyData).name
                    is BranchData -> (entityToDelete as BranchData).name
                    is WarehouseData -> (entityToDelete as WarehouseData).name
                    else -> ""
                }
                Text("Are you sure you want to delete $name? This will remove all associated resource mapping.") 
            },
            confirmButton = {
                TextButton(onClick = {
                    when (entityToDelete) {
                        is CompanyData -> companies.remove(entityToDelete as CompanyData)
                        is BranchData -> branches.remove(entityToDelete as BranchData)
                        is WarehouseData -> warehouses.remove(entityToDelete as WarehouseData)
                    }
                    showDeleteConfirm = false
                }) { Text("Remove", color = GasTrackRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Keep Record") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun EntityForm(
    tab: ManagementTab,
    entity: Any?,
    onSave: (Any) -> Unit,
    onCancel: () -> Unit
) {
    val entityName = tab.name.lowercase().replaceFirstChar { it.uppercase() }
    
    // Form States
    var companyName by remember { mutableStateOf((entity as? CompanyData)?.name ?: "") }
    var dtiNo by remember { mutableStateOf((entity as? CompanyData)?.dtiNo ?: "") }
    var doeNo by remember { mutableStateOf((entity as? CompanyData)?.doeNo ?: "") }

    var branchName by remember { mutableStateOf((entity as? BranchData)?.name ?: "") }
    var branchAddress by remember { mutableStateOf((entity as? BranchData)?.address ?: "") }
    var branchContact by remember { mutableStateOf((entity as? BranchData)?.contact ?: "") }
    var branchStatus by remember { mutableStateOf((entity as? BranchData)?.status ?: "Active") }

    var warehouseName by remember { mutableStateOf((entity as? WarehouseData)?.name ?: "") }
    var warehouseLocation by remember { mutableStateOf((entity as? WarehouseData)?.location ?: "") }
    var warehouseStatus by remember { mutableStateOf((entity as? WarehouseData)?.status ?: "Active") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (entity == null) "New $entityName Entry" else "Update $entityName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(24.dp))

                when (tab) {
                    ManagementTab.COMPANY -> {
                        EmployeeInputField(label = "COMPANY NAME", value = companyName, onValueChange = { companyName = it }, placeholder = "e.g. ABC Gas Corp", icon = Icons.Default.Business)
                        Spacer(modifier = Modifier.height(16.dp))
                        EmployeeInputField(label = "DTI REGISTRATION", value = dtiNo, onValueChange = { dtiNo = it }, placeholder = "DTI-XXXXXX", icon = Icons.Default.AppRegistration)
                        Spacer(modifier = Modifier.height(16.dp))
                        EmployeeInputField(label = "DOE REGISTRATION", value = doeNo, onValueChange = { doeNo = it }, placeholder = "DOE-XXXXXX", icon = Icons.Default.AppRegistration)
                    }
                    ManagementTab.BRANCH -> {
                        EmployeeInputField(label = "BRANCH NAME", value = branchName, onValueChange = { branchName = it }, placeholder = "e.g. Main Branch", icon = Icons.Default.Storefront)
                        Spacer(modifier = Modifier.height(16.dp))
                        EmployeeInputField(label = "OFFICE ADDRESS", value = branchAddress, onValueChange = { branchAddress = it }, placeholder = "Street, City", icon = Icons.Default.LocationOn)
                        Spacer(modifier = Modifier.height(16.dp))
                        EmployeeInputField(label = "CONTACT NUMBER", value = branchContact, onValueChange = { branchContact = it }, placeholder = "09XX-XXX-XXXX", icon = Icons.Default.Phone)
                        Spacer(modifier = Modifier.height(16.dp))
                        StatusToggle(status = branchStatus, onStatusChange = { branchStatus = it })
                    }
                    ManagementTab.WAREHOUSE -> {
                        EmployeeInputField(label = "FACILITY NAME", value = warehouseName, onValueChange = { warehouseName = it }, placeholder = "e.g. Warehouse A", icon = Icons.Default.Warehouse)
                        Spacer(modifier = Modifier.height(16.dp))
                        EmployeeInputField(label = "GEO LOCATION", value = warehouseLocation, onValueChange = { warehouseLocation = it }, placeholder = "City, Region", icon = Icons.Default.PinDrop)
                        Spacer(modifier = Modifier.height(16.dp))
                        StatusToggle(status = warehouseStatus, onStatusChange = { warehouseStatus = it })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, Color(0xFFE9ECEF))
            ) {
                Text("Discard", color = TextGray, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { 
                    val newEntity = when(tab) {
                        ManagementTab.COMPANY -> CompanyData((entity as? CompanyData)?.id ?: "C-NEW", companyName, dtiNo, doeNo)
                        ManagementTab.BRANCH -> BranchData((entity as? BranchData)?.id ?: "BR-NEW", "C-001", branchName, branchAddress, branchContact, branchStatus)
                        ManagementTab.WAREHOUSE -> WarehouseData((entity as? WarehouseData)?.id ?: "WH-NEW", warehouseName, warehouseLocation, warehouseStatus)
                    }
                    onSave(newEntity)
                },
                modifier = Modifier.weight(1f).height(56.dp).shadow(8.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
            ) {
                Text("Commit Data", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun StatusToggle(status: String, onStatusChange: (String) -> Unit) {
    Column {
        Text("RECORD STATUS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Active", "Inactive").forEach { s ->
                val isSelected = status == s
                Surface(
                    modifier = Modifier.weight(1f).height(44.dp).clickable { onStatusChange(s) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) (if (s == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) else Color.Transparent,
                    border = BorderStroke(1.dp, if (isSelected) (if (s == "Active") Color(0xFF2E7D32) else GasTrackRed) else Color(0xFFE9ECEF))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(s, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) (if (s == "Active") Color(0xFF2E7D32) else GasTrackRed) else TextGray)
                    }
                }
            }
        }
    }
}

@Composable
fun ManagementCard(
    title: String,
    details: List<String>,
    onView: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    details.forEach { detail ->
                        Text(text = detail, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onView, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = "View", tint = StaffPortalButtonBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GasTrackRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SystemDataManagementScreenPreview() {
    SystemDataManagementScreen()
}
