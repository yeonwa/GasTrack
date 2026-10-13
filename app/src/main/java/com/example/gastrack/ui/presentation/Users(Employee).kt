package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    val branch: String? = null,
    val passwordHash: String = "********",
    val createdAt: String = "2026-01-01 10:00 AM"
)

data class ActivityLogEntry(
    val activityId: String,
    val userId: String,
    val userName: String,
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
        UserItemData("U-001", "C-001", "Administrator", "Juan", "Dela Cruz", "juan.delacruz@abc.com", "Active", "San Juan Warehouse"),
        UserItemData("U-002", "C-001", "Operations Supervisor", "Patrick", "Garcia", "patrick.garcia@abc.com", "Active", "Pasig Warehouse"),
        UserItemData("U-003", "C-001", "Operations Supervisor", "Juana", "Tolentino", "juana.tolentino@abc.com", "Active", "San Juan Warehouse"),
        UserItemData("U-004", "C-001", "Stockman", "Yvonne", "Cruz", "yvonne.cruz@abc.com", "Inactive", "Pasig Warehouse"),
        UserItemData("U-005", "C-001", "Drivers", "Mark", "Reyes", "mark.reyes@abc.com", "Active", "Pasig Warehouse"),
        UserItemData("U-006", "C-001", "Helpers", "Luis", "Santos", "luis.santos@abc.com", "Active", "Pasig Warehouse"),
        UserItemData("U-007", "C-001", "Store Supervisor", "Ana", "Mendoza", "ana.mendoza@abc.com", "Active", "San Juan Warehouse"),
        UserItemData("U-008", "C-001", "Stockman", "Leo", "Perez", "leo.perez@abc.com", "Active", "Pasig Warehouse"),
        UserItemData("U-009", "C-001", "Operations Supervisor", "Maria", "Luna", "maria.luna@abc.com", "Inactive", "San Juan Warehouse"),
        UserItemData("U-010", "C-001", "Administrator", "David", "Sison", "david.sison@abc.com", "Active", "San Juan Warehouse"),
        UserItemData("U-011", "C-001", "Helpers", "Jose", "Rizal", "jose.rizal@abc.com", "Active", "Pasig Warehouse")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
    ) {
        EmployeePortalHeader(
            title = "User Accounts",
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
            onSave = { firstName, lastName, email, role, status, branch, _ ->
                val newId = "U-${(users.size + 1).toString().padStart(3, '0')}"
                users.add(UserItemData(newId, "C-001", role, firstName, lastName, email, status, branch))
                showAddUserDialog = false
            }
        )
    }

    if (showEditUserDialog && userToEdit != null) {
        UserEditDialog(
            user = userToEdit,
            onDismiss = { showEditUserDialog = false },
            onSave = { firstName, lastName, email, role, status, branch, _ ->
                val index = users.indexOfFirst { it.id == userToEdit!!.id }
                if (index != -1) {
                    users[index] = userToEdit!!.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        roleId = role,
                        status = status,
                        branch = branch
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
    users: SnapshotStateList<UserItemData>,
    onAddClick: () -> Unit,
    onViewClick: (UserItemData) -> Unit,
    onEditClick: (UserItemData) -> Unit,
    onDeleteClick: (UserItemData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("All Roles") }
    var selectedStatusFilter by remember { mutableStateOf("All Status") }
    var selectedBranchFilter by remember { mutableStateOf("All Branch/Warehouse") }
    
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 10

    val predefinedRoles = listOf("Administrator", "Operations Supervisor", "Assistant Operations Supervisor", "Store Supervisor", "Assistant Store Supervisor", "Stockman", "Head Maintenance", "Drivers", "Helpers", "Customer")
    val roles = remember(users) {
        val existing = users.map { it.roleId }.toSet()
        val ordered = predefinedRoles + existing.filter { it !in predefinedRoles }
        listOf("All Roles") + ordered
    }
    val statuses = listOf("All Status", "Active", "Inactive")
    val branches = remember(users) {
        listOf("All Branch/Warehouse") + users.mapNotNull { it.branch }.filter { it.isNotBlank() }.toSet().sorted()
    }
    
    val filteredUsers = remember(users, searchQuery, selectedRoleFilter, selectedStatusFilter, selectedBranchFilter) {
        users.filter { 
            val term = searchQuery.lowercase()
            val fullName = "${it.firstName} ${it.lastName}".lowercase()
            val matchesSearch = fullName.contains(term) || it.email.lowercase().contains(term)
            val matchesRole = selectedRoleFilter == "All Roles" || it.roleId == selectedRoleFilter
            val matchesStatus = selectedStatusFilter == "All Status" || it.status == selectedStatusFilter
            val matchesBranch = selectedBranchFilter == "All Branch/Warehouse" || it.branch == selectedBranchFilter
            matchesSearch && matchesRole && matchesStatus && matchesBranch
        }
    }

    val totalPages = maxOf(1, (filteredUsers.size + pageSize - 1) / pageSize)
    val safeCurrentPage = minOf(maxOf(1, currentPage), totalPages)
    
    LaunchedEffect(searchQuery, selectedRoleFilter, selectedStatusFilter, selectedBranchFilter) {
        currentPage = 1
    }

    val paginatedUsers = filteredUsers.drop((safeCurrentPage - 1) * pageSize).take(pageSize)

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search by Full Name or Email...", color = Color.LightGray, fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Filters Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Role Filter
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (selectedRoleFilter != "All Roles") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Text(if (selectedRoleFilter == "All Roles") "Role" else selectedRoleFilter, fontSize = 11.sp, color = TextDark, maxLines = 1)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    roles.forEach { role ->
                        DropdownMenuItem(text = { Text(role, fontSize = 12.sp) }, onClick = { selectedRoleFilter = role; expanded = false })
                    }
                }
            }

            // Status Filter
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (selectedStatusFilter != "All Status") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Text(if (selectedStatusFilter == "All Status") "Status" else selectedStatusFilter, fontSize = 11.sp, color = TextDark, maxLines = 1)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    statuses.forEach { status ->
                        DropdownMenuItem(text = { Text(status, fontSize = 12.sp) }, onClick = { selectedStatusFilter = status; expanded = false })
                    }
                }
            }
            
            // Branch Filter
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (selectedBranchFilter != "All Branch/Warehouse") StaffPortalButtonBlue else Color(0xFFE9ECEF)),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Text(if (selectedBranchFilter == "All Branch/Warehouse") "Branch" else selectedBranchFilter, fontSize = 11.sp, color = TextDark, maxLines = 1)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    branches.forEach { branch ->
                        DropdownMenuItem(text = { Text(branch, fontSize = 12.sp) }, onClick = { selectedBranchFilter = branch; expanded = false })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (paginatedUsers.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No users match your filters.", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(paginatedUsers) { user ->
                    UserCard(
                        user = user, 
                        onView = { onViewClick(user) },
                        onEdit = { onEditClick(user) },
                        onDelete = { onDeleteClick(user) }
                    )
                }
            }
        }

        PaginationControls(
            currentPage = safeCurrentPage,
            totalPages = totalPages,
            onPageChange = { currentPage = it }
        )

        Button(
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(28.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Add User", fontWeight = FontWeight.Black, fontSize = 15.sp)
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
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 7

    val logs = remember { listOf(
        ActivityLogEntry("LOG-001", "U-001", "Juan Dela Cruz", "Update", "Users", "U-002", "Updated user status for U-002", "01/01/2026 10:30 AM"),
        ActivityLogEntry("LOG-002", "U-002", "Patrick Garcia", "Create", "POS", "O-102", "Processed new retail order", "01/01/2026 11:15 AM"),
        ActivityLogEntry("LOG-003", "U-001", "Juan Dela Cruz", "Delete", "Inventory", "P-005", "Removed outdated product record", "01/01/2026 02:45 PM"),
        ActivityLogEntry("LOG-004", "U-003", "Juana Tolentino", "Update", "Sales", "SL-042", "Adjusted payment method", "01/02/2026 09:00 AM"),
        ActivityLogEntry("LOG-005", "U-001", "Juan Dela Cruz", "Create", "Users", "U-005", "Added new driver account", "01/02/2026 10:00 AM"),
        ActivityLogEntry("LOG-006", "U-002", "Patrick Garcia", "Update", "Inventory", "P-010", "Restocked 50 units", "01/02/2026 01:30 PM"),
        ActivityLogEntry("LOG-007", "U-003", "Juana Tolentino", "Delete", "POS", "O-098", "Cancelled order due to payment failure", "01/03/2026 08:45 AM"),
        ActivityLogEntry("LOG-008", "U-004", "Yvonne Cruz", "Login", "System", "Auth", "User authenticated successfully", "01/03/2026 09:15 AM")
    ) }

    val filteredLogs = remember(logs, searchQuery) {
        logs.filter {
            val term = searchQuery.lowercase()
            it.userId.lowercase().contains(term) || it.userName.lowercase().contains(term)
        }
    }

    val totalPages = maxOf(1, (filteredLogs.size + pageSize - 1) / pageSize)
    val safeCurrentPage = minOf(maxOf(1, currentPage), totalPages)
    
    LaunchedEffect(searchQuery) {
        currentPage = 1
    }

    val paginatedLogs = filteredLogs.drop((safeCurrentPage - 1) * pageSize).take(pageSize)

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search by Full Name or User ID...", color = Color.LightGray, fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (paginatedLogs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No activity matches your search.", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(paginatedLogs) { log ->
                    ActivityLogCard(log, onViewDetails = { onViewDetails(log) })
                }
            }
        }
        
        PaginationControls(
            currentPage = safeCurrentPage,
            totalPages = totalPages,
            onPageChange = { currentPage = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
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
                    Text(text = "${log.userName} (${log.userId})", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
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
                DetailValueRow("User Name", log.userName)
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
    onSave: (String, String, String, String, String, String, List<String>) -> Unit
) {
    var fullName by remember { mutableStateOf(if (user != null) "${user.firstName} ${user.lastName}" else "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(user?.roleId ?: "") }
    var branch by remember { mutableStateOf(user?.branch ?: "") }
    var status by remember { mutableStateOf(user?.status ?: "Active") }
    
    val defaultModules = listOf("Dashboard", "POS Terminal", "Inventory", "Products", "Sales", "Restocking", "Order and Delivery", "Suppliers", "Report and Compliance", "Data", "Users", "Settings")
    
    // Auto-check modules based on role (mirroring React's buildModulesByRole)
    val selectedModules = remember(role) {
        when (role) {
            "Administrator" -> defaultModules
            "Operations Supervisor", "Assistant Operations Supervisor" -> listOf("Dashboard", "Inventory", "Products", "Sales", "Restocking", "Order and Delivery", "Report and Compliance", "Data")
            "Store Supervisor", "Assistant Store Supervisor" -> listOf("Dashboard", "POS Terminal", "Inventory", "Sales", "Restocking")
            "Stockman" -> listOf("Dashboard", "Inventory", "Products", "Restocking")
            "Head Maintenance" -> listOf("Dashboard", "Inventory", "Report and Compliance")
            "Drivers", "Helpers" -> listOf("Dashboard", "Order and Delivery")
            else -> emptyList()
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(if (user == null) "New User Information" else "Edit User", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("BASIC INFORMATION", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Username / Email Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        enabled = user == null,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE9ECEF), 
                            focusedBorderColor = GasTrackBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (user != null) "New Password (optional)" else "Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(if (user != null) "Confirm New Password" else "Confirm Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE9ECEF), focusedBorderColor = GasTrackBlue)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("ROLE & ASSIGNMENT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    var expandedRole by remember { mutableStateOf(false) }
                    val rolesList = listOf("Administrator", "Operations Supervisor", "Assistant Operations Supervisor", "Store Supervisor", "Assistant Store Supervisor", "Stockman", "Head Maintenance", "Drivers", "Helpers", "Customer")
                    
                    var expandedBranch by remember { mutableStateOf(false) }
                    val branchList = listOf("Main Warehouse", "Makati Branch", "QC Warehouse", "Pasig Branch", "Alabang Branch")
                    
                    var expandedStatus by remember { mutableStateOf(false) }
                    val statusList = listOf("Active", "Inactive")

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Role Dropdown
                        Column {
                            Text("Role", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                OutlinedButton(
                                    onClick = { expandedRole = true },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text(role.ifEmpty { "Select role" }, color = if (role.isEmpty()) Color.Gray else TextDark, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                                }
                                DropdownMenu(expanded = expandedRole, onDismissRequest = { expandedRole = false }, modifier = Modifier.fillMaxWidth(0.85f)) {
                                    rolesList.forEach { r ->
                                        DropdownMenuItem(text = { Text(r) }, onClick = { role = r; expandedRole = false })
                                    }
                                }
                            }
                        }

                        // Branch Dropdown
                        Column {
                            Text("Branch / Warehouse", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                OutlinedButton(
                                    onClick = { expandedBranch = true },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text(branch.ifEmpty { "Select Branch / Warehouse" }, color = if (branch.isEmpty()) Color.Gray else TextDark, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                                }
                                DropdownMenu(expanded = expandedBranch, onDismissRequest = { expandedBranch = false }, modifier = Modifier.fillMaxWidth(0.85f)) {
                                    branchList.forEach { b ->
                                        DropdownMenuItem(text = { Text(b) }, onClick = { branch = b; expandedBranch = false })
                                    }
                                }
                            }
                        }

                        // Status Dropdown
                        Column {
                            Text("Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                OutlinedButton(
                                    onClick = { expandedStatus = true },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text(status, color = TextDark, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                                }
                                DropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }, modifier = Modifier.fillMaxWidth(0.85f)) {
                                    statusList.forEach { s ->
                                        DropdownMenuItem(text = { Text(s) }, onClick = { status = s; expandedStatus = false })
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("MODULE ACCESS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Chunk the modules into rows of 2 for perfect alignment and mobile friendliness
                    Column(modifier = Modifier.fillMaxWidth()) {
                        defaultModules.chunked(2).forEach { rowModules ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowModules.forEach { module ->
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedModules.contains(module),
                                            onCheckedChange = null, // ReadOnly per React logic
                                            enabled = false,
                                            colors = CheckboxDefaults.colors(
                                                disabledCheckedColor = StaffPortalButtonBlue.copy(alpha = 0.5f),
                                                disabledUncheckedColor = Color.LightGray
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = module, 
                                            fontSize = 12.sp, 
                                            color = if (selectedModules.contains(module)) TextDark else Color.Gray,
                                            maxLines = 1
                                        )
                                    }
                                }
                                // Ensure the last row aligns correctly if there's an odd number of items
                                if (rowModules.size < 2) {
                                    repeat(2 - rowModules.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val isFormValid = fullName.isNotBlank() && 
                    email.isNotBlank() && email.contains("@") && 
                    role.isNotBlank() && 
                    branch.isNotBlank() && 
                    (user != null || (password.isNotBlank() && password == confirmPassword)) &&
                    (user == null || password.isEmpty() || password == confirmPassword)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0), contentColor = Color(0xFF1E293B))
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        enabled = isFormValid,
                        onClick = {
                            val parts = fullName.trim().split(" ", limit = 2)
                            val fName = parts.getOrElse(0) { "" }
                            val lName = parts.getOrElse(1) { "" }
                            onSave(fName, lName, email, role, status, branch, selectedModules) 
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                    ) {
                        Text(if (user == null) "Save User" else "Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
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
                if (user.branch != null) {
                    DetailValueRow("Branch/Warehouse", user.branch)
                }
                DetailValueRow("Account Status", user.status, color = if (user.status == "Active") Color(0xFF2E7D32) else GasTrackRed)
                DetailValueRow("Created At", user.createdAt)

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

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    if (totalPages <= 1) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(32.dp)
                .clickable(enabled = currentPage > 1) { onPageChange(currentPage - 1) },
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFD1D5DB))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", modifier = Modifier.size(16.dp), tint = if (currentPage > 1) Color(0xFF374151) else Color.LightGray)
            }
        }
        
        Spacer(modifier = Modifier.width(6.dp))

        val maxVisible = 6
        var start = maxOf(1, currentPage - maxVisible / 2)
        var end = start + maxVisible - 1
        if (end > totalPages) {
            end = totalPages
            start = maxOf(1, end - maxVisible + 1)
        }

        for (p in start..end) {
            val isActive = p == currentPage
            Surface(
                modifier = Modifier
                    .height(32.dp)
                    .widthIn(min = 32.dp)
                    .clickable { onPageChange(p) },
                shape = RoundedCornerShape(8.dp),
                color = if (isActive) StaffPortalButtonBlue else Color.White,
                border = BorderStroke(1.dp, if (isActive) StaffPortalButtonBlue else Color(0xFFD1D5DB))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = p.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.White else Color(0xFF1F2937)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Surface(
            modifier = Modifier
                .size(32.dp)
                .clickable(enabled = currentPage < totalPages) { onPageChange(currentPage + 1) },
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFD1D5DB))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next", modifier = Modifier.size(16.dp), tint = if (currentPage < totalPages) Color(0xFF374151) else Color.LightGray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsersEmployeeScreenPreview() {
    UsersEmployeeScreen()
}
