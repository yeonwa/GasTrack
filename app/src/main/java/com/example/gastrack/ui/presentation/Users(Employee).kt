package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gastrack.ui.theme.*

data class UserItemData(
    val id: String,
    val companyId: String,
    val roleId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val status: String,
    val passwordHash: String = "********"
)

data class ActivityLogEntry(
    val activityId: String,
    val userId: String,
    val activityType: String,
    val module: String,
    val recordId: String,
    val description: String,
    val activityDate: String
)

@Composable
fun UsersEmployeeScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Users") }
    
    // CRUD States
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showEditUserDialog by remember { mutableStateOf(false) }
    var showViewUserDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    var userToEdit by remember { mutableStateOf<UserItemData?>(null) }
    var userToView by remember { mutableStateOf<UserItemData?>(null) }
    var userToDelete by remember { mutableStateOf<UserItemData?>(null) }

    var showViewLogDialog by remember { mutableStateOf(false) }
    var logToView by remember { mutableStateOf<ActivityLogEntry?>(null) }

    val users = remember { mutableStateListOf(
        UserItemData("U-001", "C-001", "Admin", "Juan", "Dela Cruz", "juan.delacruz@abc.com", "Active"),
        UserItemData("U-002", "C-001", "Manager", "Patrick", "Garcia", "patrick.garcia@abc.com", "Active"),
        UserItemData("U-003", "C-001", "Operations Supervisor", "Juana", "Tolentino", "juana.tolentino@abc.com", "Active"),
        UserItemData("U-004", "C-001", "Stockman", "Yvonne", "Cruz", "yvonne.cruz@abc.com", "Inactive")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "User Accounts",
            subtitle = "Access Management",
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            EmployeeTabSelector(
                tabs = listOf("Users", "Activity Log"),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = selectedTab, label = "UserTabTransition") { tab ->
                when (tab) {
                    "Users" -> UsersListTab(
                        users = users,
                        onAddClick = { showAddUserDialog = true },
                        onViewClick = { user ->
                            userToView = user
                            showViewUserDialog = true
                        },
                        onEditClick = { user ->
                            userToEdit = user
                            showEditUserDialog = true
                        },
                        onDeleteClick = { user ->
                            userToDelete = user
                            showDeleteConfirm = true
                        }
                    )
                    "Activity Log" -> ActivityLogTab(
                        onViewDetails = { log ->
                            logToView = log
                            showViewLogDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showViewLogDialog && logToView != null) {
        ActivityLogDetailDialog(log = logToView!!, onDismiss = { showViewLogDialog = false })
    }

    if (showAddUserDialog) {
        UserEditDialog(
            onDismiss = { showAddUserDialog = false },
            onSave = { firstName, lastName, email, role, status ->
                val newId = "U-${(users.size + 1).toString().padStart(3, '0')}"
                users.add(UserItemData(newId, "C-001", role, firstName, lastName, email, status))
                showAddUserDialog = false
            }
        )
    }

    if (showEditUserDialog && userToEdit != null) {
        UserEditDialog(
            user = userToEdit,
            onDismiss = { showEditUserDialog = false },
            onSave = { firstName, lastName, email, role, status ->
                val index = users.indexOfFirst { it.id == userToEdit!!.id }
                if (index != -1) {
                    users[index] = userToEdit!!.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        roleId = role,
                        status = status
                    )
                }
                showEditUserDialog = false
            }
        )
    }

    if (showViewUserDialog && userToView != null) {
        ViewUserDialog(user = userToView!!, onDismiss = { showViewUserDialog = false })
    }

    if (showDeleteConfirm && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete User Account", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to delete the account for ${userToDelete!!.firstName} ${userToDelete!!.lastName}?") },
            confirmButton = {
                TextButton(onClick = {
                    users.removeAll { it.id == userToDelete!!.id }
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
fun UsersListTab(
    users: List<UserItemData>,
    onAddClick: () -> Unit,
    onViewClick: (UserItemData) -> Unit,
    onEditClick: (UserItemData) -> Unit,
    onDeleteClick: (UserItemData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("All") }
    
    val filteredUsers = users.filter { 
        val matchesSearch = it.email.contains(searchQuery, ignoreCase = true)
        val matchesRole = if (selectedRoleFilter == "All") true else it.roleId == selectedRoleFilter
        matchesSearch && matchesRole
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search by Email...", color = Color.LightGray, fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Role Filter Row
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Admin", "Manager", "Operations Supervisor", "Store Supervisor", "Stockman", "Driver").forEach { role ->
                val isSelected = selectedRoleFilter == role
                Surface(
                    modifier = Modifier.height(36.dp).clickable { selectedRoleFilter = role },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) StaffPortalButtonBlue else Color(0xFFF1F3F4),
                    border = BorderStroke(1.dp, if (isSelected) StaffPortalButtonBlue else Color(0xFFE9ECEF))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(role, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextGray)
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
            items(filteredUsers) { user ->
                UserCard(
                    user = user, 
                    onView = { onViewClick(user) },
                    onEdit = { onEditClick(user) },
                    onDelete = { onDeleteClick(user) }
                )
            }
        }

        Button(
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(28.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Create New Account", fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
    }
}

@Composable
fun UserCard(
    user: UserItemData,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = user.id, fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark)
                    Text(text = user.roleId, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ID: ${user.companyId}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                    Surface(
                        color = if (user.status == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = user.status.uppercase(), 
                            color = if (user.status == "Active") Color(0xFF2E7D32) else GasTrackRed, 
                            fontSize = 8.sp, 
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = Color(0xFFFBFBFE),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NAME", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black)
                        Text("${user.firstName} ${user.lastName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color(0xFFE9ECEF)))
                    Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("EMAIL", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Black)
                        Text(user.email, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = StaffPortalButtonBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
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

@Composable
fun ActivityLogTab(
    onViewDetails: (ActivityLogEntry) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("All") }
    var selectedModuleFilter by remember { mutableStateOf("All") }

    val logs = remember { listOf(
        ActivityLogEntry("LOG-001", "U-001", "Update", "Users", "U-002", "Updated user status for U-002", "01/01/2026 10:30 AM"),
        ActivityLogEntry("LOG-002", "U-002", "Create", "POS", "O-102", "Processed new retail order", "01/01/2026 11:15 AM"),
        ActivityLogEntry("LOG-003", "U-001", "Delete", "Inventory", "P-005", "Removed outdated product record", "01/01/2026 02:45 PM"),
        ActivityLogEntry("LOG-004", "U-003", "Update", "Sales", "SL-042", "Adjusted payment method", "01/02/2026 09:00 AM")
    ) }

    val filteredLogs = logs.filter {
        val matchesSearch = it.userId.contains(searchQuery, ignoreCase = true)
        val matchesType = if (selectedTypeFilter == "All") true else it.activityType == selectedTypeFilter
        val matchesModule = if (selectedModuleFilter == "All") true else it.module == selectedModuleFilter
        matchesSearch && matchesType && matchesModule
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search by User ID...", color = Color.LightGray, fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Activity Type Filter
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, if (selectedTypeFilter != "All") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(if (selectedTypeFilter == "All") "All Types" else selectedTypeFilter, fontSize = 10.sp, color = TextGray)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("All", "Create", "Update", "Delete", "Login").forEach { type ->
                        DropdownMenuItem(text = { Text(type, fontSize = 12.sp) }, onClick = { selectedTypeFilter = type; expanded = false })
                    }
                }
            }

            // Module Filter
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, if (selectedModuleFilter != "All") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(if (selectedModuleFilter == "All") "All Modules" else selectedModuleFilter, fontSize = 10.sp, color = TextGray)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("All", "Users", "POS", "Inventory", "Sales", "Suppliers").forEach { module ->
                        DropdownMenuItem(text = { Text(module, fontSize = 12.sp) }, onClick = { selectedModuleFilter = module; expanded = false })
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
            items(filteredLogs) { log ->
                ActivityLogCard(log, onViewDetails = { onViewDetails(log) })
            }
        }
    }
}

@Composable
fun ActivityLogCard(log: ActivityLogEntry, onViewDetails: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = log.activityId, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextDark)
                    Text(text = "User: ${log.userId}", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = when(log.activityType) {
                        "Create" -> Color(0xFFE8F5E9)
                        "Delete" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = log.activityType.uppercase(), 
                        color = when(log.activityType) {
                            "Create" -> Color(0xFF2E7D32)
                            "Delete" -> GasTrackRed
                            else -> StaffPortalButtonBlue
                        }, 
                        fontSize = 8.sp, 
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = log.description, fontSize = 13.sp, color = TextDark, maxLines = 1, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.activityDate, 
                        fontSize = 10.sp, 
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                TextButton(
                    onClick = onViewDetails,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("View Details", fontSize = 11.sp, fontWeight = FontWeight.Black, color = StaffPortalButtonBlue)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp), tint = StaffPortalButtonBlue)
                }
            }
        }
    }
}

@Composable
fun ActivityLogDetailDialog(log: ActivityLogEntry, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Activity Insights", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                DetailValueRow("UserActivityID", log.activityId)
                DetailValueRow("UserID", log.userId)
                DetailValueRow("ActivityType", log.activityType, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Module", log.module)
                DetailValueRow("RecordID", log.recordId)
                DetailValueRow("ActivityDate", log.activityDate)
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("DESCRIPTION", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFBFBFE),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = log.description,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close Details", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun UserEditDialog(
    user: UserItemData? = null,
    onDismiss: () -> Unit, 
    onSave: (String, String, String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var role by remember { mutableStateOf(user?.roleId ?: "Staff") }
    var status by remember { mutableStateOf(user?.status ?: "Active") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(if (user == null) "New Staff Member" else "Update Account", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))
                
                Text("BASIC INFORMATION", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        EmployeeInputField(label = "FIRST NAME", value = firstName, onValueChange = { firstName = it }, placeholder = "Juan", icon = Icons.Default.Person)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        EmployeeInputField(label = "LAST NAME", value = lastName, onValueChange = { lastName = it }, placeholder = "Dela Cruz", icon = Icons.Default.Person)
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                EmployeeInputField(label = "EMAIL ADDRESS", value = email, onValueChange = { email = it }, placeholder = "juan.dc@abc.com", icon = Icons.Default.Email)
                
                Spacer(modifier = Modifier.height(14.dp))
                EmployeeInputField(label = "PASSWORD", value = password, onValueChange = { password = it }, placeholder = "Enter Secure Password", icon = Icons.Default.Lock)

                Spacer(modifier = Modifier.height(24.dp))
                Text("ROLE ASSIGNMENT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                var expandedRole by remember { mutableStateOf(false) }
                val roles = listOf(
                    "Admin",
                    "Manager",
                    "Operations Supervisor",
                    "Assistant Operations Supervisor",
                    "Store Supervisor",
                    "Assistant Store Supervisor",
                    "Stockman",
                    "Head Maintenance",
                    "Driver",
                    "Helper",
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedRole = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(role, color = TextDark, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = StaffPortalButtonBlue)
                        }
                    }
                    DropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false },
                        modifier = Modifier.fillMaxWidth(0.7f).background(Color.White)
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    role = r
                                    expandedRole = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("ACCOUNT STATUS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Active", "Inactive").forEach { s ->
                        val isSelected = status == s
                        Surface(
                            modifier = Modifier.weight(1f).height(40.dp).clickable { status = s },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) (if (s == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) (if (s == "Active") Color(0xFF2E7D32) else GasTrackRed) else Color(0xFFE9ECEF))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(s, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) (if (s == "Active") Color(0xFF2E7D32) else GasTrackRed) else TextGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                    ) {
                        Text("Cancel", color = TextGray, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        enabled = firstName.isNotEmpty() && lastName.isNotEmpty() && email.contains("@"),
                        onClick = { onSave(firstName, lastName, email, role, status) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Text(if (user == null) "Provision" else "Update", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun ViewUserDialog(user: UserItemData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = StaffPortalButtonBlue.copy(alpha = 0.08f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("${user.firstName.take(1)}${user.lastName.take(1)}", color = StaffPortalButtonBlue, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "${user.firstName} ${user.lastName}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextDark)
                        Text(text = "System ID: ${user.id}", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailValueRow("Email Address", user.email)
                DetailValueRow("Company ID", user.companyId)
                DetailValueRow("System Role", user.roleId, isBold = true, color = StaffPortalButtonBlue)
                DetailValueRow("Account Status", user.status, color = if (user.status == "Active") Color(0xFF2E7D32) else GasTrackRed)
                DetailValueRow("Password Hash", user.passwordHash)

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Close Summary", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsersEmployeeScreenPreview() {
    UsersEmployeeScreen()
}
