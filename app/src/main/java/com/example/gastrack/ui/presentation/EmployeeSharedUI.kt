package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun EmployeePortalHeader(
    title: String,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    onMenu: (() -> Unit)? = null,
    onProfile: () -> Unit = {},
    showLogo: Boolean = true,
    showProfile: Boolean = false,
    backgroundColor: Color = GasTrackBlue,
    rightContent: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp),
        color = backgroundColor
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = if (backgroundColor == GasTrackBlue) Color.White else StaffPortalButtonBlue, 
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else if (onMenu != null) {
                    IconButton(
                        onClick = { onMenu() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Menu, 
                            contentDescription = "Menu", 
                            tint = if (backgroundColor == GasTrackBlue) Color.White else StaffPortalButtonBlue, 
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (showLogo) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.favicon), contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) { append("Gas") }
                                withStyle(style = SpanStyle(color = GasTrackRed, fontWeight = FontWeight.Black)) { append("Track") }
                            },
                            fontSize = 18.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }
                }

                if (rightContent != null) {
                    rightContent()
                } else if (showProfile) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onProfile() },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White, modifier = Modifier.padding(2.dp).fillMaxSize())
                    }
                } else {
                    Box(modifier = Modifier.size(40.dp))
                }
            }

            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = subtitle, fontSize = 11.sp, color = GasTrackRed, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(2.dp))
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            Text(
                text = title, 
                fontSize = 22.sp, 
                fontWeight = FontWeight.Black, 
                color = if (backgroundColor == GasTrackBlue) Color.White else TextDark
            )
        }
    }
}

@Composable
fun EmployeeTabSelector(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    EmployeeTabSelector(
        tabs = DropdownOptions(tabs),
        selectedTab = selectedTab,
        onTabSelected = onTabSelected
    )
}

@Composable
fun EmployeeTabSelector(
    tabs: DropdownOptions,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F3F4)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(3.dp), verticalAlignment = Alignment.CenterVertically) {
            tabs.items.forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelected(tab) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color.White else Color.Transparent,
                    shadowElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = tab,
                            color = if (isSelected) TextDark else TextGray,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = TextGray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(Color(0xFFFBFBFE), RoundedCornerShape(16.dp)),
            placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 14.sp) },
            leadingIcon = icon?.let {
                {
                    Icon(imageVector = it, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp))
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE9ECEF),
                focusedBorderColor = StaffPortalButtonBlue,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
        )
    }
}

@Composable
fun EmployeeActionItem(
    icon: ImageVector, 
    text: String, 
    color: Color, 
    modifier: Modifier = Modifier, 
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.height(36.dp).clickable { onClick() },
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = color)
        }
    }
}

@Composable
fun DetailValueRow(label: String, value: String, isBold: Boolean = false, color: Color = TextDark) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), 
        horizontalArrangement = Arrangement.SpaceBetween, 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextGray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 14.sp, color = color, fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold)
    }
}

@Composable
fun PortalSuccessDialog(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.85f)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = Color(0xFFE8F5E9)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp).fillMaxSize())
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Success", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))
                Text(message, fontSize = 14.sp, color = TextGray, textAlign = TextAlign.Center, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)
                ) {
                    Text("Understood", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ActionFeedbackDialog(message: String, onDismiss: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1.seconds)
        step = 1
        delay(1.5.seconds)
        step = 2
    }

    Dialog(onDismissRequest = if (step == 2) onDismiss else ({})) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.85f)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (step < 2) {
                    CircularProgressIndicator(color = StaffPortalButtonBlue, modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(if (step == 0) "Initializing..." else message, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                } else {
                    Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = Color(0xFFE8F5E9)) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Operation Complete", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Task finished successfully.", fontSize = 14.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = StaffPortalButtonBlue)) {
                        Text("Continue", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryStatCard(
    label: String, 
    value: String, 
    color: Color, 
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    sub: String? = null
) {
    Surface(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = color.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = color,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (sub != null) {
                    Text(
                        text = sub,
                        fontSize = 9.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StaffPortalButtonBlue, modifier = Modifier.size(20.dp)) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE9ECEF),
            focusedBorderColor = StaffPortalButtonBlue
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
    )
}

@Composable
fun FilterDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterDropdown(
        options = DropdownOptions(options),
        selectedOption = selectedOption,
        onOptionSelected = onOptionSelected,
        modifier = modifier
    )
}

@Composable
fun FilterDropdown(
    options: DropdownOptions,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clickable { expanded = true },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFE9ECEF)),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = selectedOption, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium, maxLines = 1)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.items.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, fontSize = 12.sp) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


