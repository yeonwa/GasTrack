package com.example.gastrack.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gastrack.ui.theme.*

data class AddressItem(
    val id: Int,
    val label: String,
    val address: String,
    val icon: ImageVector,
    val isPrimary: Boolean = false
)

@Composable
fun AddressesScreen(
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var addressToEdit by remember { mutableStateOf<AddressItem?>(null) }
    var addressToDelete by remember { mutableStateOf<AddressItem?>(null) }
    val addresses = remember { mutableStateListOf(
        AddressItem(1, "Home", "45 Mabini St, Brgy. Concepcion, Malabon City", Icons.Default.Home, true),
        AddressItem(2, "Work", "GasTrack HQ, Bonifacio Global City, Taguig", Icons.Default.Work),
        AddressItem(3, "Mom's House", "12 Rizal Ave, Brgy. San Jose, Navotas", Icons.Default.LocationOn)
    ) }

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GasTrackBlue, StaffPortalBlue)
                        )
                    )
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(86.dp).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Saved Addresses",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        },
        floatingActionButton = {
            if (!showAddDialog && addressToEdit == null && addressToDelete == null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = GasTrackBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Address")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(addresses) { address ->
                AddressCard(
                    address = address,
                    onSelect = {
                        // Logic to set as primary
                        val index = addresses.indexOf(address)
                        if (index != -1) {
                            val newList = addresses.map { it.copy(isPrimary = it.id == address.id) }
                            addresses.clear()
                            addresses.addAll(newList)
                        }
                    },
                    onEdit = {
                        addressToEdit = address
                    },
                    onDelete = {
                        addressToDelete = address
                    }
                )
            }
        }
    }

    if (addressToDelete != null) {
        AlertDialog(
            onDismissRequest = { addressToDelete = null },
            title = { Text("Delete Address", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${addressToDelete?.label}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        addresses.remove(addressToDelete)
                        addressToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { addressToDelete = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextDark)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showAddDialog || addressToEdit != null) {
        val isEdit = addressToEdit != null
        AddAddressDialog(
            initialLabel = addressToEdit?.label ?: "",
            initialLocation = addressToEdit?.address ?: "",
            initialType = when(addressToEdit?.icon) {
                Icons.Default.Home -> "Home"
                Icons.Default.Work -> "Work"
                else -> "Other"
            },
            isEditMode = isEdit,
            onDismiss = { 
                showAddDialog = false
                addressToEdit = null
            },
            onSave = { label, location, type ->
                val icon = when(type) {
                    "Home" -> Icons.Default.Home
                    "Work" -> Icons.Default.Work
                    else -> Icons.Default.LocationOn
                }
                if (isEdit) {
                    val index = addresses.indexOfFirst { it.id == addressToEdit!!.id }
                    if (index != -1) {
                        addresses[index] = addressToEdit!!.copy(label = label, address = location, icon = icon)
                    }
                } else {
                    val newId = (addresses.maxOfOrNull { it.id } ?: 0) + 1
                    addresses.add(AddressItem(newId, label, location, icon, addresses.isEmpty()))
                }
                showAddDialog = false
                addressToEdit = null
            }
        )
    }
}

@Composable
fun AddressCard(
    address: AddressItem,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .semantics(mergeDescendants = true) {
                stateDescription = if (address.isPrimary) "Selected as primary address" else "Not selected"
            }
            .clickable(onClickLabel = "Select ${address.label} as primary address") { onSelect() },
        color = Color.White,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp).wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (address.isPrimary) StaffPortalButtonBlue.copy(alpha = 0.1f) else Color(0xFFF1F3F4)
            ) {
                Icon(
                    imageVector = address.icon,
                    contentDescription = null,
                    tint = if (address.isPrimary) StaffPortalButtonBlue else StaffPortalBlue,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = address.label,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextDark,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (address.isPrimary) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF005FCC),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "DEFAULT",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = address.address,
                    fontSize = 13.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
                if (!address.isPrimary) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Set as Default",
                        fontSize = 12.sp,
                        color = Color(0xFF005FCC),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextGray, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AddAddressDialog(
    initialLabel: String = "",
    initialLocation: String = "",
    initialType: String = "Home",
    isEditMode: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var label by remember { mutableStateOf(initialLabel) }
    var location by remember { mutableStateOf(initialLocation) }
    var selectedType by remember { mutableStateOf(initialType) }
    
    val types = listOf("Home", "Work", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditMode) "Edit Address" else "Add New Address",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "ADDRESS LABEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g. My Apartment", color = BorderColor) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "LOCATION DETAILS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    placeholder = { Text("Complete address here...", color = BorderColor) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ADDRESS TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { type ->
                        val isSelected = selectedType == type
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { selectedType = type }
                                .border(
                                    1.dp, 
                                    if (isSelected) StaffPortalBlue else Color(0xFFE9ECEF), 
                                    RoundedCornerShape(10.dp)
                                ),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GasTrackBlue.copy(alpha = 0.15f) else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) GasTrackBlue else TextGray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    enabled = label.isNotEmpty() && location.isNotEmpty(),
                    onClick = { onSave(label, location, selectedType) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GasTrackBlue),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = if (isEditMode) "Save Changes" else "Save Address",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddressesScreenPreview() {
    AddressesScreen(onBack = {})
}
