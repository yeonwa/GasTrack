package com.example.gastrack.ui.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

@Composable
fun StartUpScreen(
    onNavigateToCustomer: () -> Unit,
    onNavigateToEmployee: () -> Unit,
    onNavigateToRegisterCompany: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Home") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- Top Header Bar ---
        StartUpTopBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // --- Main Body Content ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Crossfade(targetState = selectedTab, label = "TabContent") { tab ->
                when (tab) {
                    "Home" -> StartUpHomeContent(
                        onNavigateToCustomer = onNavigateToCustomer,
                        onNavigateToEmployee = onNavigateToEmployee,
                        onNavigateToRegisterCompany = onNavigateToRegisterCompany
                    )
                    "About" -> StartUpAboutContent()
                    "Contact" -> StartUpContactContent()
                }
            }
        }
    }
}

@Composable
fun StartUpTopBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GasTrackBlue,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Home", "About", "Contact").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f),
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StartUpHomeContent(
    onNavigateToCustomer: () -> Unit,
    onNavigateToEmployee: () -> Unit,
    onNavigateToRegisterCompany: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            LogoSection()

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome to GasTrack",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = GasTrackBlue,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Please select your account type to continue to the correct portal.",
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            AccountTypeCard(
                icon = Icons.Default.Person,
                overlineText = "I'm a",
                titleText = "Customer",
                subtitleText = "Order gas, track deliveries",
                accentColor = GasTrackRed,
                onClick = onNavigateToCustomer
            )

            Spacer(modifier = Modifier.height(14.dp))

            AccountTypeCard(
                icon = Icons.Default.LocalShipping,
                overlineText = "I'm an",
                titleText = "Employee",
                subtitleText = "Manage stock, do deliveries",
                accentColor = GasTrackBlue,
                onClick = onNavigateToEmployee
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Register LPG Company Card at the bottom
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .clickable { onNavigateToRegisterCompany() },
                shape = RoundedCornerShape(14.dp),
                color = GasTrackRed
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Business",
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NEW LPG BUSINESS?",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Register LPG Company",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Set up your business & branch network",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Register",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "v1.0.4 • Powered by GasTrack",
            fontSize = 11.sp,
            color = BorderColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun StartUpAboutContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = GasTrackBlue
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "ABOUT GASTRACK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = GasTrackRed,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.White)) { append("Managing LPG inventory ") }
                        withStyle(style = SpanStyle(color = GasTrackRed)) { append("smarter, ") }
                        withStyle(style = SpanStyle(color = Color.White)) { append("not harder.") }
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GasTrack is a purpose-built inventory management system for LPG companies in the Philippines. We help LPG dealers track cylinder movements, deliveries, and stock levels across branches — in real time.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AboutStatBox(
                        value = "24/7",
                        label = "System Availability",
                        modifier = Modifier.fillMaxWidth()
                    )
                    AboutStatBox(
                        value = "100%",
                        label = "LPG focused",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "v1.0.4 • Powered by GasTrack",
            fontSize = 11.sp,
            color = BorderColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AboutStatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2B4C7E)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = GasTrackRed
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StartUpContactContent() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Select a topic") }
    var subjectExpanded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Left Info Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = GasTrackBlue
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Get in touch",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Have questions about GasTrack or want to register your LPG company? Our team is here to help.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ContactInfoItem(
                    icon = Icons.Default.Phone,
                    title = "Phone",
                    value = "+63 9 8453 1234"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ContactInfoItem(
                    icon = Icons.Default.Email,
                    title = "Email",
                    value = "gastrack.inventory@gmail.com"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ContactInfoItem(
                    icon = Icons.Default.Schedule,
                    title = "Office hours",
                    value = "Mon - Sat, 8:00AM - 5:00PM"
                )
            }
        }

        // Right Message Form Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Send us a message",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "We'll get back to you within one business day.",
                    fontSize = 12.sp,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("First name")
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = { Text("Juan", color = Color.LightGray, fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Last name")
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = { Text("Dela Cruz", color = Color.LightGray, fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Email address")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("juandelacruz@gmail.com", color = Color.LightGray, fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Subject")
                Box {
                    OutlinedButton(
                        onClick = { subjectExpanded = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = subject,
                                color = if (subject == "Select a topic") TextGray else TextDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                        }
                    }
                    DropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                        listOf("Company Registration", "General Inquiry", "Technical Support", "Partnership").forEach { topic ->
                            DropdownMenuItem(
                                text = { Text(topic, fontSize = 13.sp) },
                                onClick = {
                                    subject = topic
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Message")
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Describe your inquiry or concern...", color = Color.LightGray, fontSize = 13.sp) },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE9ECEF),
                        focusedBorderColor = GasTrackBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showSuccessDialog = true },
                    enabled = firstName.isNotEmpty() && email.isNotEmpty() && message.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GasTrackBlue)
                ) {
                    Text("Send Message", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showSuccessDialog) {
        PortalSuccessDialog(
            message = "Thank you for reaching out! Your message has been sent to our team.",
            onDismiss = {
                showSuccessDialog = false
                firstName = ""
                lastName = ""
                email = ""
                subject = "Select a topic"
                message = ""
            }
        )
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = TextDark,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun ContactInfoItem(icon: ImageVector, title: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF2B4C7E)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = GasTrackRed.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GasTrackRed,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LogoSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.favicon),
                contentDescription = "Logo Icon",
                modifier = Modifier.padding(8.dp).fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = GasTrackBlue, fontWeight = FontWeight.Black)) {
                        append("Gas")
                    }
                    withStyle(style = SpanStyle(color = GasTrackRed, fontWeight = FontWeight.Black)) {
                        append("Track")
                    }
                },
                fontSize = 24.sp,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Optimize Your LPG, Effortlessly.",
                fontSize = 10.sp,
                color = GasTrackBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun AccountTypeCard(
    icon: ImageVector,
    overlineText: String,
    titleText: String,
    subtitleText: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = titleText,
                    tint = accentColor,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = overlineText.uppercase(),
                    fontSize = 10.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = titleText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitleText,
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Continue",
                tint = BorderColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartUpScreenPreview() {
    StartUpScreen(onNavigateToCustomer = {}, onNavigateToEmployee = {})
}
