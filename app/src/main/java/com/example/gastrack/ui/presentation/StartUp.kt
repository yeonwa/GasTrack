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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
                        onNavigateToRegisterCompany = onNavigateToRegisterCompany,
                        onNavigateToPrivacy = { selectedTab = "Privacy" },
                        onNavigateToTerms = { selectedTab = "Terms" }
                    )
                    "About" -> StartUpAboutContent()
                    "Contact" -> StartUpContactContent()
                    "Privacy" -> StartUpPrivacyContent(onBack = { selectedTab = "Home" })
                    "Terms" -> StartUpTermsContent(onBack = { selectedTab = "Home" })
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
    onNavigateToRegisterCompany: () -> Unit,
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to GasTrack",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = GasTrackBlue,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Please select your account type to continue to the correct portal.",
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
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

            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "By continuing, you agree to our",
                    fontSize = 11.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "Privacy Policy",
                        fontSize = 11.sp,
                        color = GasTrackBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { onNavigateToPrivacy() }
                    )
                    Text(
                        text = " and ",
                        fontSize = 11.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = "Terms of Service",
                        fontSize = 11.sp,
                        color = GasTrackBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { onNavigateToTerms() }
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

@Composable
fun StartUpPrivacyContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 12.dp).size(24.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GasTrackBlue)
            }
            Text(
                text = "PRIVACY POLICY",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GasTrackBlue
            )
        }
        Text(
            text = "Last Updated: October 08, 2026",
            fontSize = 12.sp,
            color = TextGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        
        Text(
            text = "This Privacy Notice for GasTrack (\"we,\" \"us,\" or \"our\"), describes how and why we might access, collect, store, use, and/or share (\"process\") your personal information when you use our services (\"Services\"), including when you:\n\n" +
                    "• Visit our website at https://gastrack-inventory.vercel.app or any website of ours that links to this Privacy Notice\n" +
                    "• Download and use our mobile application (GasTrack), or any other application of ours that links to this Privacy Notice\n" +
                    "• Use GasTrack. GasTrack is a purpose-built inventory management system for LPG companies in the Philippines. We help LPG dealers track cylinder movements, deliveries, and stock levels across branches in real time.\n" +
                    "• Engage with us in other related ways, including any marketing or events\n\n" +
                    "Questions or concerns? Reading this Privacy Notice will help you understand your privacy rights and choices. We are responsible for making decisions about how your personal information is processed. If you do not agree with our policies and practices, please do not use our Services. If you still have any questions or concerns, please contact us at gastrack.inventory@gmail.com.",
            fontSize = 14.sp,
            color = TextGray,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        PolicySection(
            title = "SUMMARY OF KEY POINTS",
            content = "• What personal information do we process? When you visit, use, or navigate our Services, we may process personal information depending on how you interact with us and the Services, the choices you make, and the products and features you use.\n\n" +
                    "• Do we process any sensitive personal information? Some of the information may be considered \"special\" or \"sensitive\" in certain jurisdictions, for example your racial or ethnic origins, sexual orientation, and religious beliefs. We do not process sensitive personal information.\n\n" +
                    "• Do we collect any information from third parties? We do not collect any information from third parties.\n\n" +
                    "• How do we process your information? We process your information to provide, improve, and administer our Services, communicate with you, for security and fraud prevention, and to comply with law. We may also process your information for other purposes with your consent. We process your information only when we have a valid legal reason to do so.\n\n" +
                    "• In what situations and with which parties do we share personal information? We may share information in specific situations and with specific third parties.\n\n" +
                    "• How do we keep your information safe? We have adequate organizational and technical processes and procedures in place to protect your personal information. However, no electronic transmission over the internet or information storage technology can be guaranteed to be 100% secure, so we cannot promise or guarantee that hackers, cybercriminals, or other unauthorized third parties will not be able to defeat our security and improperly collect, access, steal, or modify your information.\n\n" +
                    "• What are your rights? Depending on where you are located geographically, the applicable privacy law may mean you have certain rights regarding your personal information.\n\n" +
                    "• How do you exercise your rights? The easiest way to exercise your rights is by visiting GasTrack Inventory, or by contacting us. We will consider and act upon any request in accordance with applicable data protection laws."
        )
        
        PolicySection(
            title = "1. WHAT INFORMATION DO WE COLLECT?",
            content = "Personal information you disclose to us\n\n" +
                    "In Short: We collect personal information that you provide to us.\n\n" +
                    "We collect personal information that you voluntarily provide to us when you register on the Services, express an interest in obtaining information about us or our products and Services, when you participate in activities on the Services, or otherwise when you contact us.\n\n" +
                    "Personal Information Provided by You. The personal information that we collect depends on the context of your interactions with us and the Services, the choices you make, and the products and features you use. The personal information we collect may include the following:\n\n" +
                    "• names\n" +
                    "• phone numbers\n" +
                    "• email addresses\n" +
                    "• usernames\n" +
                    "• passwords\n" +
                    "• contact or authentication data\n" +
                    "• billing addresses\n" +
                    "• debit/credit card numbers\n" +
                    "• address"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StartUpTermsContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 12.dp).size(24.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GasTrackBlue)
            }
            Text(
                text = "TERMS OF SERVICE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GasTrackBlue
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Last updated October 08, 2026",
            fontSize = 12.sp,
            color = TextGray,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PolicySection(
            title = "AGREEMENT TO OUR LEGAL TERMS",
            content = "We are GasTrack (\"Company,\" \"we,\" \"us,\" \"our\"). We operate the website gastrack-inventory.vercel.app (the \"Site\"), the mobile application GasTrack (the \"App\"), as well as any other related products and services that refer or link to these legal terms (the \"Legal Terms\") (collectively, the \"Services\").\n\nGasTrack is a purpose-built inventory management system for LPG companies in the Philippines. We help LPG dealers track cylinder movements, deliveries, and stock levels across branches in real time.\n\nYou can contact us by phone at 09926219700 or email at gastrack.inventory@gmail.com.\n\nThese Legal Terms constitute a legally binding agreement made between you, whether personally or on behalf of an entity (\"you\"), and GasTrack, concerning your access to and use of the Services. You agree that by accessing the Services, you have read, understood, and agreed to be bound by all of these Legal Terms. IF YOU DO NOT AGREE WITH ALL OF THESE LEGAL TERMS, THEN YOU ARE EXPRESSLY PROHIBITED FROM USING THE SERVICES AND YOU MUST DISCONTINUE USE IMMEDIATELY.\n\nWe will provide you with prior notice of any scheduled changes to the Services you are using. The modified Legal Terms will become effective upon posting or notifying you by gastrack.inventory@gmail.com , as stated in the email message. By continuing to use the Services after the effective date of any changes, you agree to be bound by the modified terms.\n\nThe Services are intended for users who are at least 18 years old. Persons under the age of 18 are not permitted to use or register for the Services.\n\nWe recommend that you print a copy of these Legal Terms for your records."
        )

        PolicySection(
            title = "1. OUR SERVICES",
            content = "The information provided when using the Services is not intended for distribution to or use by any person or entity in any jurisdiction or country where such distribution or use would be contrary to law or regulation or which would subject us to any registration requirement within such jurisdiction or country. Accordingly, those persons who choose to access the Services from other locations do so on their own initiative and are solely responsible for compliance with local laws, if and to the extent local laws are applicable."
        )

        PolicySection(
            title = "2. INTELLECTUAL PROPERTY RIGHTS",
            content = "Our intellectual property\n\nWe are the owner or the licensee of all intellectual property rights in our Services, including all source code, databases, functionality, software, website designs, audio, video, text, photographs, and graphics in the Services (collectively, the \"Content\"), as well as the trademarks, service marks, and logos contained therein (the \"Marks\").\n\nOur Content and Marks are protected by copyright and trademark laws (and various other intellectual property rights and unfair competition laws) and treaties around the world.\n\nThe Content and Marks are provided in or through the Services \"AS IS\" for your personal, non-commercial use or internal business purpose only.\n\nYour use of our Services\n\nSubject to your compliance with these Legal Terms, including the \"PROHIBITED ACTIVITIES\" section below, we grant you a non-exclusive, non-transferable, revocable license to:\n• access the Services; and\n• download or print a copy of any portion of the Content to which you have properly gained access, solely for your personal, non-commercial use or internal business purpose.\n\nExcept as set out in this section or elsewhere in our Legal Terms, no part of the Services and no Content or Marks may be copied, reproduced, aggregated, republished, uploaded, posted, publicly displayed, encoded, translated, transmitted, distributed, sold, licensed, or otherwise exploited for any commercial purpose whatsoever, without our express prior written permission.\n\nIf you wish to make any use of the Services, Content, or Marks other than as set out in this section or elsewhere in our Legal Terms, please address your request to: gastrack.inventory@gmail.com . If we ever grant you the permission to post, reproduce, or publicly display any part of our Services or Content, you must identify us as the owners or licensors of the Services, Content, or Marks and ensure that any copyright or proprietary notice appears or is visible on posting, reproducing, or displaying our Content.\n\nWe reserve all rights not expressly granted to you in and to the Services, Content, and Marks.\n\nAny breach of these Intellectual Property Rights will constitute a material breach of our Legal Terms and your right to use our Services will terminate immediately.\n\nYour submissions and contributions\n\nPlease review this section and the \"PROHIBITED ACTIVITIES\" section carefully prior to using our Services to understand the (a) rights you give us and (b) obligations you have when you post or upload any content through the Services.\n\nSubmissions: By directly sending us any question, comment, suggestion, idea, feedback, or other information about the Services (\"Submissions\"), you agree to assign to us all intellectual property rights in such Submission. You agree that we shall own this Submission and be entitled to its unrestricted use and dissemination for any lawful purpose, commercial or otherwise, without acknowledgment or compensation to you.\n\nContributions: The Services may invite you to chat, contribute to, or participate in blogs, message boards, online forums, and other functionality during which you may create, submit, post, display, transmit, publish, distribute, or broadcast content and materials to us or through the Services, including but not limited to text, writings, video, audio, photographs, music, graphics, comments, reviews, rating suggestions, personal information, or other material (\"Contributions\"). Any Submission that is publicly posted shall also be treated as a Contribution. You understand that Contributions may be viewable by other users of the Services.\n\nWhen you post Contributions, you grant us a license (including use of your name, trademarks, and logos): By posting any Contributions, you grant us an unrestricted, unlimited, irrevocable, perpetual, non-exclusive, transferable, royalty-free, fully-paid, worldwide right, and license to: use, copy, reproduce, distribute, sell, resell, publish, broadcast, retitle, store, publicly perform, publicly display, reformat, translate, excerpt (in whole or in part), and exploit your Contributions (including, without limitation, your image, name, and voice) for any purpose, commercial, advertising, or otherwise, to prepare derivative works of, or incorporate into other works, your Contributions, and to sublicense the licenses granted in this section. Our use and distribution may occur in any media formats and through any media channels. This license includes our use of your name, company name, and franchise name, as applicable, and any of the trademarks, service marks, trade names, logos, and personal and commercial images you provide.\n\nYou are responsible for what you post or upload: By sending us Submissions and/or posting Contributions through any part of the Services or making Contributions accessible through the Services by linking your account through the Services to any of your social networking accounts, you:\n• confirm that you have read and agree with our \"PROHIBITED ACTIVITIES\" and will not post, send, publish, upload, or transmit through the Services any Submission nor post any Contribution that is illegal, harassing, hateful, harmful, defamatory, obscene, bullying, abusive, discriminatory, threatening to any person or group, sexually explicit, false, inaccurate, deceitful, or misleading;\n• to the extent permissible by applicable law, waive any and all moral rights to any such Submission and/or Contribution;\n• warrant that any such Submission and/or Contributions are original to you or that you have the necessary rights and licenses to submit such Submissions and/or Contributions and that you have full authority to grant us the above-mentioned rights in relation to your Submissions and/or Contributions; and\n• warrant and represent that your Submissions and/or Contributions do not constitute confidential information.\n\nYou are solely responsible for your Submissions and/or Contributions and you expressly agree to reimburse us for any and all losses that we may suffer because of your breach of (a) this section, (b) any third party's intellectual property rights, or (c) applicable law.\n\nWe may remove or edit your Content: Although we have no obligation to monitor any Contributions, we shall have the right to remove or edit any Contributions at any time without notice if in our reasonable opinion we consider such Contributions harmful or in breach of these Legal Terms. If we remove or edit any such Contributions, we may also suspend or disable your account and report you to the authorities."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

