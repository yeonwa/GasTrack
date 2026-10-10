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
                    "Privacy" -> StartUpPrivacyContent()
                    "Terms" -> StartUpTermsContent()
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

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Privacy Policy",
                    fontSize = 11.sp,
                    color = GasTrackBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
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
                    modifier = Modifier.padding(top = 2.dp)
                )
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
fun StartUpPrivacyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "PRIVACY POLICY",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = GasTrackBlue
        )
        Text(
            text = "Last updated October 08, 2026",
            fontSize = 12.sp,
            color = TextGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Text(
            text = "This Privacy Notice for GasTrack (\"we,\" \"us,\" or \"our\"), describes how and why we might access, collect, store, use, and/or share (\"process\") your personal information when you use our services (\"Services\"), including when you:\n\n" +
                    "• Visit our website at https://gastrack-inventory.vercel.app or any website of ours that links to this Privacy Notice\n\n" +
                    "• Download and use our mobile application (GasTrack), or any other application of ours that links to this Privacy Notice\n\n" +
                    "• Use GasTrack. GasTrack is a purpose-built inventory management system for LPG companies in the Philippines. We help LPG dealers track cylinder movements, deliveries, and stock levels across branches in real time.\n\n" +
                    "• Engage with us in other related ways, including any marketing or events\n\n" +
                    "Questions or concerns? Reading this Privacy Notice will help you understand your privacy rights and choices. We are responsible for making decisions about how your personal information is processed. If you do not agree with our policies and practices, please do not use our Services. If you still have any questions or concerns, please contact us at gastrack.inventory@gmail.com.\n\n" +
                    "SUMMARY OF KEY POINTS\n\n" +
                    "• What personal information do we process? When you visit, use, or navigate our Services, we may process personal information depending on how you interact with us and the Services, the choices you make, and the products and features you use.\n\n" +
                    "• Do we process any sensitive personal information? Some of the information may be considered \"special\" or \"sensitive\" in certain jurisdictions, for example your racial or ethnic origins, sexual orientation, and religious beliefs. We do not process sensitive personal information.\n\n" +
                    "• Do we collect any information from third parties? We do not collect any information from third parties.\n\n" +
                    "• How do we process your information? We process your information to provide, improve, and administer our Services, communicate with you, for security and fraud prevention, and to comply with law. We may also process your information for other purposes with your consent. We process your information only when we have a valid legal reason to do so.\n\n" +
                    "• In what situations and with which parties do we share personal information? We may share information in specific situations and with specific third parties.\n\n" +
                    "• How do we keep your information safe? We have adequate organizational and technical processes and procedures in place to protect your personal information. However, no electronic transmission over the internet or information storage technology can be guaranteed to be 100% secure, so we cannot promise or guarantee that hackers, cybercriminals, or other unauthorized third parties will not be able to defeat our security and improperly collect, access, steal, or modify your information.\n\n" +
                    "• What are your rights? Depending on where you are located geographically, the applicable privacy law may mean you have certain rights regarding your personal information.\n\n" +
                    "• How do you exercise your rights? The easiest way to exercise your rights is by visiting GasTrack Inventory, or by contacting us. We will consider and act upon any request in accordance with applicable data protection laws.\n\n" +
                    "1. WHAT INFORMATION DO WE COLLECT?\n\n" +
                    "Personal information you disclose to us\n" +
                    "In Short: We collect personal information that you provide to us.\n" +
                    "We collect personal information that you voluntarily provide to us when you register on the Services, express an interest in obtaining information about us or our products and Services, when you participate in activities on the Services, or otherwise when you contact us.\n\n" +
                    "Personal Information Provided by You. The personal information that we collect depends on the context of your interactions with us and the Services, the choices you make, and the products and features you use. The personal information we collect may include the following:\n" +
                    "• names\n" +
                    "• phone numbers\n" +
                    "• email addresses\n" +
                    "• usernames\n" +
                    "• passwords\n" +
                    "• contact or authentication data\n" +
                    "• billing addresses\n" +
                    "• debit/credit card numbers\n" +
                    "• address\n\n" +
                    "Sensitive Information. We do not process sensitive information.\n\n" +
                    "Application Data. If you use our application(s), we also may collect the following information if you choose to provide us with access or permission:\n" +
                    "• Geolocation Information: We may request access or permission to track location-based information from your mobile device, either continuously or while you are using our mobile application(s), to provide certain location-based services. If you wish to change our access or permissions, you may do so in your device's settings.\n" +
                    "• Mobile Device Access: We may request access or permission to certain features from your mobile device, including your mobile device's calendar, camera, reminders, storage, and other features. If you wish to change our access or permissions, you may do so in your device's settings.\n" +
                    "• Mobile Device Data: We automatically collect device information (such as your mobile device ID, model, and manufacturer), operating system, version information and system configuration information, device and application identification numbers, browser type and version, hardware model, Internet service provider and/or mobile carrier, and Internet Protocol (IP) address (or proxy server). If you are using our application(s), we may also collect information about the phone network associated with your mobile device, your mobile device's operating system or platform, the type of mobile device you use, your mobile device's unique device ID, and information about the features of our application(s) you accessed.\n" +
                    "• Push Notifications: We may request to send you push notifications regarding your account or certain features of the application(s). If you wish to opt out from receiving these types of communications, you may turn them off in your device's settings.\n\n" +
                    "This information is primarily needed to maintain the security and operation of our application(s), for troubleshooting, and for our internal analytics and reporting purposes.\n\n" +
                    "All personal information that you provide to us must be true, complete, and accurate, and you must notify us of any changes to such personal information.\n\n" +
                    "Google API\n" +
                    "Our use of information received from Google APIs will adhere to Google API Services User Data Policy, including the Limited Use requirements.\n\n" +
                    "2. HOW DO WE PROCESS YOUR INFORMATION?\n\n" +
                    "In Short: We process your information to provide, improve, and administer our Services, communicate with you, for security and fraud prevention, and to comply with law. We may also process your information for other purposes with your consent.\n\n" +
                    "We process your personal information for a variety of reasons, depending on how you interact with our Services, including:\n" +
                    "• To facilitate account creation and authentication and otherwise manage user accounts: We may process your information so you can create and log in to your account, as well as keep your account in working order.\n" +
                    "• To deliver and facilitate delivery of services to the user: We may process your information to provide you with the requested service.\n" +
                    "• To respond to user inquiries/offer support to users: We may process your information to respond to your inquiries and solve any potential issues you might have with the requested service.\n" +
                    "• To send administrative information to you: We may process your information to send you details about our products and services, changes to our terms and policies, and other similar information.\n" +
                    "• To fulfill and manage your orders: We may process your information to fulfill and manage your orders, payments, returns, and exchanges made through the Services.\n" +
                    "• To request feedback: We may process your information when necessary to request feedback and to contact you about your use of our Services.\n" +
                    "• To evaluate and improve our Services, products, marketing, and your experience: We may process your information when we believe it is necessary to identify usage trends, determine the effectiveness of our promotional campaigns, and to evaluate and improve our Services, products, marketing, and your experience.\n\n" +
                    "3. WHEN AND WITH WHOM DO WE SHARE YOUR PERSONAL INFORMATION?\n\n" +
                    "In Short: We may share information in specific situations described in this section and/or with the following third parties.\n\n" +
                    "We may need to share your personal information in the following situations:\n" +
                    "• Business Transfers: We may share or transfer your information in connection with, or during negotiations of, any merger, sale of company assets, financing, or acquisition of all or a portion of our business to another company.\n" +
                    "• When we use Google Maps Platform APIs: We may share your information with certain Google Maps Platform APIs (e.g., Google Maps API, Places API). Google Maps uses GPS, Wi-Fi, and cell towers to estimate your location. GPS is accurate to about 20 meters, while Wi-Fi and cell towers help improve accuracy when GPS signals are weak, like indoors. This data helps Google Maps provide directions, but it is not always perfectly precise. We obtain and store on your device (\"cache\") your location for six (6) months. You may revoke your consent anytime by contacting us at the contact details provided at the end of this document.\n" +
                    "• Business Partners: We may share your information with our business partners to offer you certain products, services, or promotions.\n\n" +
                    "4. DO WE USE COOKIES AND OTHER TRACKING TECHNOLOGIES?\n\n" +
                    "In Short: We may use cookies and other tracking technologies to collect and store your information.\n\n" +
                    "We may use cookies and similar tracking technologies (like web beacons and pixels) to gather information when you interact with our Services. Some online tracking technologies help us maintain the security of our Services and your account, prevent crashes, fix bugs, save your preferences, and assist with basic site functions.\n\n" +
                    "We also permit third parties and service providers to use online tracking technologies on our Services for analytics and advertising, including to help manage and display advertisements or to tailor advertisements to your interests. The third parties and service providers use their technology to provide advertising about products and services tailored to your interests which may appear either on our Services or on other websites.\n\n" +
                    "Specific information about how we use such technologies and how you can refuse certain cookies is set out in our Cookie Notice.\n\n" +
                    "5. HOW LONG DO WE KEEP YOUR INFORMATION?\n\n" +
                    "In Short: We keep your information for as long as necessary to fulfill the purposes outlined in this Privacy Notice unless otherwise required by law.\n\n" +
                    "We will only keep your personal information for as long as it is necessary for the purposes set out in this Privacy Notice, unless a longer retention period is required or permitted by law (such as tax, accounting, or other legal requirements). No purpose in this notice will require us keeping your personal information for longer than twelve (12) months past the start of the idle period of the user's account.\n\n" +
                    "When we have no ongoing legitimate business need to process your personal information, we will either delete or anonymize such information, or, if this is not possible (for example, because your personal information has been stored in backup archives), then we will securely store your personal information and isolate it from any further processing until deletion is possible.\n\n" +
                    "6. HOW DO WE KEEP YOUR INFORMATION SAFE?\n\n" +
                    "In Short: We aim to protect your personal information through a system of organizational and technical security measures.\n\n" +
                    "We have implemented appropriate and reasonable technical and organizational security measures designed to protect the security of any personal information we process. However, despite our safeguards and efforts to secure your information, no electronic transmission over the Internet or information storage technology can be guaranteed to be 100% secure, so we cannot promise or guarantee that hackers, cybercriminals, or other unauthorized third parties will not be able to defeat our security and improperly collect, access, steal, or modify your information. Although we will do our best to protect your personal information, transmission of personal information to and from our Services is at your own risk. You should only access the Services within a secure environment.\n\n" +
                    "7. DO WE COLLECT INFORMATION FROM MINORS?\n\n" +
                    "In Short: We do not knowingly collect data from or market to children under 18 years of age.\n\n" +
                    "We do not knowingly collect, solicit data from, or market to children under 18 years of age, nor do we knowingly sell such personal information. By using the Services, you represent that you are at least 18 or that you are the parent or guardian of such a minor and consent to such minor dependent's use of the Services. If we learn that personal information from users less than 18 years of age has been collected, we will deactivate the account and take reasonable measures to promptly delete such data from our records. If you become aware of any data we may have collected from children under age 18, please contact us at gastrack.inventory@gmail.com.\n\n" +
                    "8. WHAT ARE YOUR PRIVACY RIGHTS?\n\n" +
                    "In Short: You may review, change, or terminate your account at any time, depending on your country, province, or state of residence.\n\n" +
                    "Withdrawing your consent: If we are relying on your consent to process your personal information, which may be express and/or implied consent depending on the applicable law, you have the right to withdraw your consent at any time. You can withdraw your consent at any time by contacting us by using the contact details provided in the section \"HOW CAN YOU CONTACT US ABOUT THIS NOTICE?\" below.\n\n" +
                    "However, please note that this will not affect the lawfulness of the processing before its withdrawal nor, when applicable law allows, will it affect the processing of your personal information conducted in reliance on lawful processing grounds other than consent.\n\n" +
                    "Account Information\n\n" +
                    "If you would at any time like to review or change the information in your account or terminate your account, you can:\n" +
                    "• Log in to your account settings and update your user account.\n" +
                    "• Contact us using the contact information provided: gastrack.inventory@gmail.com\n\n" +
                    "Upon your request to terminate your account, we will deactivate or delete your account and information from our active databases. However, we may retain some information in our files to prevent fraud, troubleshoot problems, assist with any investigations, enforce our legal terms and/or comply with applicable legal requirements.\n\n" +
                    "If you have questions or comments about your privacy rights, you may email us at gastrack.inventory@gmail.com.\n\n" +
                    "9. CONTROLS FOR DO-NOT-TRACK FEATURES\n\n" +
                    "Most web browsers and some mobile operating systems and mobile applications include a Do-Not-Track (\"DNT\") feature or setting you can activate to signal your privacy preference not to have data about your online browsing activities monitored and collected. At this stage, no uniform technology standard for recognizing and implementing DNT signals has been finalized. As such, we do not currently respond to DNT browser signals or any other mechanism that automatically communicates your choice not to be tracked online. If a standard for online tracking is adopted that we must follow in the future, we will inform you about that practice in a revised version of this Privacy Notice.\n\n" +
                    "10. DO WE MAKE UPDATES TO THIS NOTICE?\n\n" +
                    "In Short: Yes, we will update this notice as necessary to stay compliant with relevant laws.\n\n" +
                    "We may update this Privacy Notice from time to time. The updated version will be indicated by an updated \"Revised\" date at the top of this Privacy Notice. If we make material changes to this Privacy Notice, we may notify you either by prominently posting a notice of such changes or by directly sending you a notification. We encourage you to review this Privacy Notice frequently to be informed of how we are protecting your information.\n\n" +
                    "11. HOW CAN YOU CONTACT US ABOUT THIS NOTICE?\n\n" +
                    "If you have questions or comments about this notice, you may email us at gastrack.inventory@gmail.com or contact us by post at:\n\n" +
                    "GasTrack\n" +
                    "Philippines\n\n" +
                    "12. HOW CAN YOU REVIEW, UPDATE, OR DELETE THE DATA WE COLLECT FROM YOU?\n\n" +
                    "You have the right to request access to the personal information we collect from you, details about how we have processed it, correct inaccuracies, or delete your personal information. You may also have the right to withdraw your consent to our processing of your personal information. These rights may be limited in some circumstances by applicable law. To request to review, update, or delete your personal information, please visit: GasTrack Inventory.",
            fontSize = 13.sp,
            color = TextDark,
            lineHeight = 18.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StartUpTermsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "TERMS OF SERVICE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = GasTrackBlue
        )
        Text(
            text = "Last updated October 08, 2026",
            fontSize = 12.sp,
            color = TextGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Text(
            text = "AGREEMENT TO OUR LEGAL TERMS\n" +
                    "We are GasTrack (\"Company,\" \"we,\" \"us,\" \"our\"). We operate the website gastrack-inventory.vercel.app (the \"Site\"), the mobile application GasTrack (the \"App\"), as well as any other related products and services that refer or link to these legal terms (the \"Legal Terms\") (collectively, the \"Services\").\n\n" +
                    "GasTrack is a purpose-built inventory management system for LPG companies in the Philippines. We help LPG dealers track cylinder movements, deliveries, and stock levels across branches in real time.\n\n" +
                    "You can contact us by phone at 09926219700 or email at gastrack.inventory@gmail.com.\n\n" +
                    "These Legal Terms constitute a legally binding agreement made between you, whether personally or on behalf of an entity (\"you\"), and GasTrack, concerning your access to and use of the Services. You agree that by accessing the Services, you have read, understood, and agreed to be bound by all of these Legal Terms. IF YOU DO NOT AGREE WITH ALL OF THESE LEGAL TERMS, THEN YOU ARE EXPRESSLY PROHIBITED FROM USING THE SERVICES AND YOU MUST DISCONTINUE USE IMMEDIATELY.\n\n" +
                    "We will provide you with prior notice of any scheduled changes to the Services you are using. The modified Legal Terms will become effective upon posting or notifying you by gastrack.inventory@gmail.com , as stated in the email message. By continuing to use the Services after the effective date of any changes, you agree to be bound by the modified terms.\n\n" +
                    "The Services are intended for users who are at least 18 years old. Persons under the age of 18 are not permitted to use or register for the Services.\n\n" +
                    "We recommend that you print a copy of these Legal Terms for your records.\n\n" +
                    "TABLE OF CONTENTS\n" +
                    "1. OUR SERVICES\n" +
                    "2. INTELLECTUAL PROPERTY RIGHTS\n" +
                    "3. USER REPRESENTATIONS\n" +
                    "4. USER REGISTRATION\n" +
                    "5. PURCHASES AND PAYMENT\n" +
                    "6. POLICY\n" +
                    "7. PROHIBITED ACTIVITIES\n" +
                    "8. USER GENERATED CONTRIBUTIONS\n" +
                    "9. CONTRIBUTION LICENSE\n" +
                    "10. MOBILE APPLICATION LICENSE\n" +
                    "11. SERVICES MANAGEMENT\n" +
                    "12. PRIVACY POLICY\n" +
                    "13. TERM AND TERMINATION\n" +
                    "14. MODIFICATIONS AND INTERRUPTIONS\n" +
                    "15. GOVERNING LAW\n" +
                    "16. DISPUTE RESOLUTION\n" +
                    "17. CORRECTIONS\n" +
                    "18. DISCLAIMER\n" +
                    "19. LIMITATIONS OF LIABILITY\n" +
                    "20. INDEMNIFICATION\n" +
                    "21. USER DATA\n" +
                    "22. ELECTRONIC COMMUNICATIONS, TRANSACTIONS, AND SIGNATURES\n" +
                    "23. MISCELLANEOUS\n" +
                    "24. CONTACT US\n\n" +
                    "1. OUR SERVICES\n" +
                    "The information provided when using the Services is not intended for distribution to or use by any person or entity in any jurisdiction or country where such distribution or use would be contrary to law or regulation or which would subject us to any registration requirement within such jurisdiction or country. Accordingly, those persons who choose to access the Services from other locations do so on their own initiative and are solely responsible for compliance with local laws, if and to the extent local laws are applicable.\n\n" +
                    "2. INTELLECTUAL PROPERTY RIGHTS\n" +
                    "Our intellectual property\n" +
                    "We are the owner or the licensee of all intellectual property rights in our Services, including all source code, databases, functionality, software, website designs, audio, video, text, photographs, and graphics in the Services (collectively, the \"Content\"), as well as the trademarks, service marks, and logos contained therein (the \"Marks\").\n\n" +
                    "Our Content and Marks are protected by copyright and trademark laws (and various other intellectual property rights and unfair competition laws) and treaties around the world.\n\n" +
                    "The Content and Marks are provided in or through the Services \"AS IS\" for your personal, non-commercial use or internal business purpose only.\n\n" +
                    "Your use of our Services\n" +
                    "Subject to your compliance with these Legal Terms, including the \"PROHIBITED ACTIVITIES\" section below, we grant you a non-exclusive, non-transferable, revocable license to:\n" +
                    "• access the Services; and\n" +
                    "• download or print a copy of any portion of the Content to which you have properly gained access, solely for your personal, non-commercial use or internal business purpose.\n\n" +
                    "Except as set out in this section or elsewhere in our Legal Terms, no part of the Services and no Content or Marks may be copied, reproduced, aggregated, republished, uploaded, posted, publicly displayed, encoded, translated, transmitted, distributed, sold, licensed, or otherwise exploited for any commercial purpose whatsoever, without our express prior written permission.\n\n" +
                    "If you wish to make any use of the Services, Content, or Marks other than as set out in this section or elsewhere in our Legal Terms, please address your request to: gastrack.inventory@gmail.com . If we ever grant you the permission to post, reproduce, or publicly display any part of our Services or Content, you must identify us as the owners or licensors of the Services, Content, or Marks and ensure that any copyright or proprietary notice appears or is visible on posting, reproducing, or displaying our Content.\n\n" +
                    "We reserve all rights not expressly granted to you in and to the Services, Content, and Marks.\n\n" +
                    "Any breach of these Intellectual Property Rights will constitute a material breach of our Legal Terms and your right to use our Services will terminate immediately.\n\n" +
                    "Your submissions and contributions\n" +
                    "Please review this section and the \"PROHIBITED ACTIVITIES\" section carefully prior to using our Services to understand the (a) rights you give us and (b) obligations you have when you post or upload any content through the Services.\n\n" +
                    "Submissions: By directly sending us any question, comment, suggestion, idea, feedback, or other information about the Services (\"Submissions\"), you agree to assign to us all intellectual property rights in such Submission. You agree that we shall own this Submission and be entitled to its unrestricted use and dissemination for any lawful purpose, commercial or otherwise, without acknowledgment or compensation to you.\n\n" +
                    "Contributions: The Services may invite you to chat, contribute to, or participate in blogs, message boards, online forums, and other functionality during which you may create, submit, post, display, transmit, publish, distribute, or broadcast content and materials to us or through the Services, including but not limited to text, writings, video, audio, photographs, music, graphics, comments, reviews, rating suggestions, personal information, or other material (\"Contributions\"). Any Submission that is publicly posted shall also be treated as a Contribution. You understand that Contributions may be viewable by other users of the Services.\n\n" +
                    "When you post Contributions, you grant us a license (including use of your name, trademarks, and logos): By posting any Contributions, you grant us an unrestricted, unlimited, irrevocable, perpetual, non-exclusive, transferable, royalty-free, fully-paid, worldwide right, and license to: use, copy, reproduce, distribute, sell, resell, publish, broadcast, retitle, store, publicly perform, publicly display, reformat, translate, excerpt (in whole or in part), and exploit your Contributions (including, without limitation, your image, name, and voice) for any purpose, commercial, advertising, or otherwise, to prepare derivative works of, or incorporate into other works, your Contributions, and to sublicense the licenses granted in this section. Our use and distribution may occur in any media formats and through any media channels. This license includes our use of your name, company name, and franchise name, as applicable, and any of the trademarks, service marks, trade names, logos, and personal and commercial images you provide.\n\n" +
                    "You are responsible for what you post or upload: By sending us Submissions and/or posting Contributions through any part of the Services or making Contributions accessible through the Services by linking your account through the Services to any of your social networking accounts, you:\n" +
                    "• confirm that you have read and agree with our \"PROHIBITED ACTIVITIES\" and will not post, send, publish, upload, or transmit through the Services any Submission nor post any Contribution that is illegal, harassing, hateful, harmful, defamatory, obscene, bullying, abusive, discriminatory, threatening to any person or group, sexually explicit, false, inaccurate, deceitful, or misleading;\n" +
                    "• to the extent permissible by applicable law, waive any and all moral rights to any such Submission and/or Contribution;\n" +
                    "• warrant that any such Submission and/or Contributions are original to you or that you have the necessary rights and licenses to submit such Submissions and/or Contributions and that you have full authority to grant us the above-mentioned rights in relation to your Submissions and/or Contributions; and\n" +
                    "• warrant and represent that your Submissions and/or Contributions do not constitute confidential information.\n\n" +
                    "You are solely responsible for your Submissions and/or Contributions and you expressly agree to reimburse us for any and all losses that we may suffer because of your breach of (a) this section, (b) any third party's intellectual property rights, or (c) applicable law.\n\n" +
                    "We may remove or edit your Content: Although we have no obligation to monitor any Contributions, we shall have the right to remove or edit any Contributions at any time without notice if in our reasonable opinion we consider such Contributions harmful or in breach of these Legal Terms. If we remove or edit any such Contributions, we may also suspend or disable your account and report you to the authorities.\n\n" +
                    "3. USER REPRESENTATIONS\n" +
                    "By using the Services, you represent and warrant that:\n" +
                    "1. all registration information you submit will be true, accurate, current, and complete;\n" +
                    "2. you will maintain the accuracy of such information and promptly update such registration information as necessary;\n" +
                    "3. you have the legal capacity and you agree to comply with these Legal Terms;\n" +
                    "4. you are not a minor in the jurisdiction in which you reside;\n" +
                    "5. you will not access the Services through automated or non-human means, whether through a bot, script or otherwise;\n" +
                    "6. you will not use the Services for any illegal or unauthorized purpose; and\n" +
                    "7. your use of the Services will not violate any applicable law or regulation.\n\n" +
                    "If you provide any information that is untrue, inaccurate, not current, or incomplete, we have the right to suspend or terminate your account and refuse any and all current or future use of the Services (or any portion thereof).\n\n" +
                    "4. USER REGISTRATION\n" +
                    "You may be required to register to use the Services. You agree to keep your password confidential and will be responsible for all use of your account and password. We reserve the right to remove, reclaim, or change a username you select if we determine, in our sole discretion, that such username is inappropriate, obscene, or otherwise objectionable.\n\n" +
                    "5. PURCHASES AND PAYMENT\n" +
                    "We accept the following forms of payment:\n" +
                    "• Cash\n" +
                    "• Bank Transfer\n" +
                    "• GCash\n" +
                    "• Card\n\n" +
                    "You agree to provide current, complete, and accurate purchase and account information for all purchases made via the Services. You further agree to promptly update account and payment information, including email address, payment method, and payment card expiration date, so that we can complete your transactions and contact you as needed. Sales tax will be added to the price of purchases as deemed required by us. We may change prices at any time. All payments shall be in PHP.\n\n" +
                    "You agree to pay all charges at the prices then in effect for your purchases and any applicable shipping fees, and you authorize us to charge your chosen payment provider for any such amounts upon placing your order. We reserve the right to correct any errors or mistakes in pricing, even if we have already requested or received payment.\n\n" +
                    "We reserve the right to refuse any order placed through the Services. We may, in our sole discretion, limit or cancel quantities purchased per person, per household, or per order. These restrictions may include orders placed by or under the same customer account, the same payment method, and/or orders that use the same billing or shipping address. We reserve the right to limit or prohibit orders that, in our sole judgment, appear to be placed by dealers, resellers, or distributors.\n\n" +
                    "6. POLICY\n" +
                    "All sales are final and no refund will be issued.\n\n" +
                    "7. PROHIBITED ACTIVITIES\n" +
                    "You may not access or use the Services for any purpose other than that for which we make the Services available. The Services may not be used in connection with any commercial endeavors except those that are specifically endorsed or approved by us.\n\n" +
                    "As a user of the Services, you agree not to:\n" +
                    "• Systematically retrieve data or other content from the Services to create or compile, directly or indirectly, a collection, compilation, database, or directory without written permission from us.\n" +
                    "• Trick, defraud, or mislead us and other users, especially in any attempt to learn sensitive account information such as user passwords.\n" +
                    "• Circumvent, disable, or otherwise interfere with security-related features of the Services, including features that prevent or restrict the use or copying of any Content or enforce limitations on the use of the Services and/or the Content contained therein.\n" +
                    "• Disparage, tarnish, or otherwise harm, in our opinion, us and/or the Services.\n" +
                    "• Use any information obtained from the Services in order to harass, abuse, or harm another person.\n" +
                    "• Make improper use of our support services or submit false reports of abuse or misconduct.\n" +
                    "• Use the Services in a manner inconsistent with any applicable laws or regulations.\n" +
                    "• Engage in unauthorized framing of or linking to the Services.\n" +
                    "• Upload or transmit (or attempt to upload or to transmit) viruses, Trojan horses, or other material, including excessive use of capital letters and spamming (continuous posting of repetitive text), that interferes with any party's uninterrupted use and enjoyment of the Services or modifies, impairs, disrupts, alters, or interferes with the use, features, functions, operation, or maintenance of the Services.\n" +
                    "• Engage in any automated use of the system, such as using scripts to send comments or messages, or using any data mining, robots, or similar data gathering and extraction tools.\n" +
                    "• Delete the copyright or other proprietary rights notice from any Content.\n" +
                    "• Attempt to impersonate another user or person or use the username of another user.\n" +
                    "• Upload or transmit (or attempt to upload or to transmit) any material that acts as a passive or active information collection or transmission mechanism, including without limitation, clear graphics interchange formats (\"gifs\"), 1×1 pixels, web bugs, cookies, or other similar devices (sometimes referred to as \"spyware\" or \"passive collection mechanisms\" or \"pcms\").\n" +
                    "• Interfere with, disrupt, or create an undue burden on the Services or the networks or services connected to the Services.\n" +
                    "• Harass, annoy, intimidate, or threaten any of our employees or agents engaged in providing any portion of the Services to you.\n" +
                    "• Attempt to bypass any measures of the Services designed to prevent or restrict access to the Services, or any portion of the Services.\n" +
                    "• Copy or adapt the Services' software, including but not limited to Flash, PHP, HTML, JavaScript, or other code.\n" +
                    "• Except as permitted by applicable law, decipher, decompile, disassemble, or reverse engineer any of the software comprising or in any way making up a part of the Services.\n" +
                    "• Except as may be the result of standard search engine or Internet browser usage, use, launch, develop, or distribute any automated system, including without limitation, any spider, robot, cheat utility, scraper, or offline reader that accesses the Services, or use or launch any unauthorized script or other software.\n" +
                    "• Use a buying agent or purchasing agent to make purchases on the Services.\n" +
                    "• Make any unauthorized use of the Services, including collecting usernames and/or email addresses of users by electronic or other means for the purpose of sending unsolicited email, or creating user accounts by automated means or under false pretenses.\n" +
                    "• Use the Services as part of any effort to compete with us or otherwise use the Services and/or the Content for any revenue-generating endeavor or commercial enterprise.\n" +
                    "• Use the Services to advertise or offer to sell goods and services.\n\n" +
                    "8. USER GENERATED CONTRIBUTIONS\n" +
                    "The Services may invite you to chat, contribute to, or participate in blogs, message boards, online forums, and other functionality, and may provide you with the opportunity to create, submit, post, display, transmit, perform, publish, distribute, or broadcast content and materials to us or on the Services, including but not limited to text, writings, video, audio, photographs, graphics, comments, suggestions, or personal information or other material (collectively, \"Contributions\"). Contributions may be viewable by other users of the Services and through third-party websites. As such, any Contributions you transmit may be treated as non-confidential and non-proprietary. When you create or make available any Contributions, you thereby represent and warrant that:\n" +
                    "• The creation, distribution, transmission, public display, or performance, and the accessing, downloading, or copying of your Contributions do not and will not infringe the proprietary rights, including but not limited to the copyright, patent, trademark, trade secret, or moral rights of any third party.\n" +
                    "• You are the creator and owner of or have the necessary licenses, rights, consents, releases, and permissions to use and to authorize us, the Services, and other users of the Services to use your Contributions in any manner contemplated by the Services and these Legal Terms.\n" +
                    "• You have the written consent, release, and/or permission of each and every identifiable individual person in your Contributions to use the name or likeness of each and every such identifiable individual person to enable inclusion and use of your Contributions in any manner contemplated by the Services and these Legal Terms.\n" +
                    "• Your Contributions are not false, inaccurate, or misleading.\n" +
                    "• Your Contributions are not unsolicited or unauthorized advertising, promotional materials, pyramid schemes, chain letters, spam, mass mailings, or other forms of solicitation.\n" +
                    "• Your Contributions are not obscene, lewd, lascivious, filthy, violent, harassing, libelous, slanderous, or otherwise objectionable (as determined by us).\n" +
                    "• Your Contributions do not ridicule, mock, disparage, intimidate, or abuse anyone.\n" +
                    "• Your Contributions are not used to harass or threaten (in the legal sense of those terms) any other person and to promote violence against a specific person or class of people.\n" +
                    "• Your Contributions do not violate any applicable law, regulation, or rule.\n" +
                    "• Your Contributions do not violate the privacy or publicity rights of any third party.\n" +
                    "• Your Contributions do not violate any applicable law concerning child pornography, or otherwise intended to protect the health or well-being of minors.\n" +
                    "• Your Contributions do not include any offensive comments that are connected to race, national origin, gender, sexual preference, or physical handicap.\n" +
                    "• Your Contributions do not otherwise violate, or link to material that violates, any provision of these Legal Terms, or any applicable law or regulation.\n\n" +
                    "Any use of the Services in violation of the foregoing violates these Legal Terms and may result in, among other things, termination or suspension of your rights to use the Services.\n\n" +
                    "9. CONTRIBUTION LICENSE\n" +
                    "By posting your Contributions to any part of the Services, you automatically grant, and you represent and warrant that you have the right to grant, to us an unrestricted, unlimited, irrevocable, perpetual, non-exclusive, transferable, royalty-free, fully-paid, worldwide right, and license to host, use, copy, reproduce, disclose, sell, resell, publish, broadcast, retitle, archive, store, cache, publicly perform, publicly display, reformat, translate, transmit, excerpt (in whole or in part), and distribute such Contributions (including, without limitation, your image and voice) for any purpose, commercial, advertising, or otherwise, and to prepare derivative works of, or incorporate into other works, such Contributions, and grant and authorize sublicenses of the foregoing. The use and distribution may occur in any media formats and through any media channels. This license will apply to any form, media, or technology now known or hereafter developed, and includes our use of your name, company name, and franchise name, as applicable, and any of the trademarks, service marks, trade names, logos, and personal and commercial images you provide. You waive all moral rights in your Contributions, and you warrant that moral rights have not otherwise been asserted in your Contributions.\n\n" +
                    "We do not assert any ownership over your Contributions. You retain full ownership of all of your Contributions and any intellectual property rights or other proprietary rights associated with your Contributions. We are not liable for any statements or representations in your Contributions provided by you in any area on the Services. You are solely responsible for your Contributions to the Services and you expressly agree to exonerate us from any and all responsibility and to refrain from any legal action against us regarding your Contributions.\n\n" +
                    "We have the right, in our sole and absolute discretion, (1) to edit, redact, or otherwise change any Contributions; (2) to re-categorize any Contributions to place them in more appropriate locations on the Services; and (3) to pre-screen or delete any Contributions at any time and for any reason, without notice. We have no obligation to monitor your Contributions.\n\n" +
                    "10. MOBILE APPLICATION LICENSE\n" +
                    "Use License\n" +
                    "If you access the Services via the App, then we grant you a revocable, non-exclusive, non-transferable, limited right to install and use the App on wireless electronic devices owned or controlled by you, and to access and use the App on such devices strictly in accordance with the terms and conditions of this mobile application license contained in these Legal Terms.\n\n" +
                    "You shall not:\n" +
                    "1. except as permitted by applicable law, decompile, reverse engineer, disassemble, attempt to derive the source code of, or decrypt the App;\n" +
                    "2. make any modification, adaptation, improvement, enhancement, translation, or derivative work from the App;\n" +
                    "3. violate any applicable laws, rules, or regulations in connection with your access or use of the App;\n" +
                    "4. remove, alter, or obscure any proprietary notice (including any notice of copyright or trademark) posted by us or the licensors of the App;\n" +
                    "5. use the App for any revenue-generating endeavor, commercial enterprise, or other purpose for which it is not designed or intended;\n" +
                    "6. make the App available over a network or other environment permitting access or use by multiple devices or users at the same time;\n" +
                    "7. use the App for creating a product, service, or software that is, directly or indirectly, competitive with or in any way a substitute for the App;\n" +
                    "8. use the App to send automated queries to any website or to send any unsolicited commercial email; or\n" +
                    "9. use any proprietary information or any of our interfaces or our other intellectual property in the design, development, manufacture, licensing, or distribution of any applications, accessories, or devices for use with the App.\n\n" +
                    "Apple and Android Devices\n" +
                    "The following terms apply when you use the App obtained from either the Apple Store or Google Play (each an \"App Distributor\") to access the Services:\n" +
                    "1. the license granted to you for our App is limited to a non-transferable license to use the application on a device that utilizes the Apple iOS or Android operating systems, as applicable, and in accordance with the usage rules set forth in the applicable App Distributor's terms of service;\n" +
                    "2. we are responsible for providing any maintenance and support services with respect to the App as specified in the terms and conditions of this mobile application license contained in these Legal Terms or as otherwise required under applicable law, and you acknowledge that each App Distributor has no obligation whatsoever to furnish any maintenance and support services with respect to the App;\n" +
                    "3. in the event of any failure of the App to conform to any applicable warranty, you may notify the applicable App Distributor, and the App Distributor, in accordance with its terms and policies, may refund the purchase price, if any, paid for the App, and to the maximum extent permitted by applicable law, the App Distributor will have no other warranty obligation whatsoever with respect to the App;\n" +
                    "4. you represent and warrant that (i) you are not located in a country that is subject to a US government embargo, or that has been designated by the US government as a \"terrorist supporting\" country and (ii) you are not listed on any US government list of prohibited or restricted parties;\n" +
                    "5. you must comply with applicable third-party terms of agreement when using the App, e.g., if you have a VoIP application, then you must not be in violation of their wireless data service agreement when using the App; and\n" +
                    "6. you acknowledge and agree that the App Distributors are third-party beneficiaries of the terms and conditions in this mobile application license contained in these Legal Terms, and that each App Distributor will have the right (and will be deemed to have accepted the right) to enforce the terms and conditions in this mobile application license contained in these Legal Terms against you as a third-party beneficiary thereof.\n\n" +
                    "11. SERVICES MANAGEMENT\n" +
                    "We reserve the right, but not the obligation, to:\n" +
                    "1. monitor the Services for violations of these Legal Terms;\n" +
                    "2. take appropriate legal action against anyone who, in our sole discretion, violates the law or these Legal Terms, including without limitation, reporting such user to law enforcement authorities;\n" +
                    "3. in our sole discretion and without limitation, refuse, restrict access to, limit the availability of, or disable (to the extent technologically feasible) any of your Contributions or any portion thereof;\n" +
                    "4. in our sole discretion and without limitation, notice, or liability, to remove from the Services or otherwise disable all files and content that are excessive in size or are in any way burdensome to our systems; and\n" +
                    "5. otherwise manage the Services in a manner designed to protect our rights and property and to facilitate the proper functioning of the Services.\n\n" +
                    "12. PRIVACY POLICY\n" +
                    "We care about data privacy and security. By using the Services, you agree to be bound by our Privacy Policy, which is incorporated into these Legal Terms. Please be advised the Services are hosted in the Philippines. If you access the Services from any other region of the world with laws or other requirements governing personal data collection, use, or disclosure that differ from applicable laws in the Philippines, then through your continued use of the Services, you are transferring your data to the Philippines, and you expressly consent to have your data transferred to and processed in the Philippines.\n\n" +
                    "13. TERM AND TERMINATION\n" +
                    "These Legal Terms shall remain in full force and effect while you use the Services. WITHOUT LIMITING ANY OTHER PROVISION OF THESE LEGAL TERMS, WE RESERVE THE RIGHT TO, IN OUR SOLE DISCRETION AND WITHOUT NOTICE OR LIABILITY, DENY ACCESS TO AND USE OF THE SERVICES (INCLUDING BLOCKING CERTAIN IP ADDRESSES), TO ANY PERSON FOR ANY REASON OR FOR NO REASON, INCLUDING WITHOUT LIMITATION FOR BREACH OF ANY REPRESENTATION, WARRANTY, OR COVENANT CONTAINED IN THESE LEGAL TERMS OR OF ANY APPLICABLE LAW OR REGULATION. WE MAY TERMINATE YOUR USE OR PARTICIPATION IN THE SERVICES OR DELETE YOUR ACCOUNT AND ANY CONTENT OR INFORMATION THAT YOU POSTED AT ANY TIME, WITHOUT WARNING, IN OUR SOLE DISCRETION.\n\n" +
                    "If we terminate or suspend your account for any reason, you are prohibited from registering and creating a new account under your name, a fake or borrowed name, or the name of any third party, even if you may be acting on behalf of the third party. In addition to terminating or suspending your account, we reserve the right to take appropriate legal action, including without limitation pursuing civil, criminal, and injunctive redress.\n\n" +
                    "14. MODIFICATIONS AND INTERRUPTIONS\n" +
                    "We reserve the right to change, modify, or remove the contents of the Services at any time or for any reason at our sole discretion without notice. However, we have no obligation to update any information on our Services. We will not be liable to you or any third party for any modification, price change, suspension, or discontinuance of the Services.\n\n" +
                    "We cannot guarantee the Services will be available at all times. We may experience hardware, software, or other problems or need to perform maintenance related to the Services, resulting in interruptions, delays, or errors. We reserve the right to change, revise, update, suspend, discontinue, or otherwise modify the Services at any time or for any reason without notice to you. You agree that we have no liability whatsoever for any loss, damage, or inconvenience caused by your inability to access or use the Services during any downtime or discontinuance of the Services. Nothing in these Legal Terms will be construed to obligate us to maintain and support the Services or to supply any corrections, updates, or releases in connection therewith.\n\n" +
                    "15. GOVERNING LAW\n" +
                    "These Legal Terms shall be governed by and defined following the laws of the Philippines. GasTrack and yourself irrevocably consent that the courts of the Philippines shall have exclusive jurisdiction to resolve any dispute which may arise in connection with these Legal Terms.\n\n" +
                    "16. DISPUTE RESOLUTION\n" +
                    "Informal Negotiations\n" +
                    "To expedite resolution and control the cost of any dispute, controversy, or claim related to these Legal Terms (each a \"Dispute\" and collectively, the \"Disputes\") brought by either you or us (individually, a \"Party\" and collectively, the \"Parties\"), the Parties agree to first attempt to negotiate any Dispute (except those Disputes expressly provided below) informally for at least 30 days before initiating arbitration. Such informal negotiations commence upon written notice from one Party to the other Party.\n\n" +
                    "Binding Arbitration\n" +
                    "If the parties are unable to resolve the dispute through informal negotiation, the dispute shall be finally resolved by arbitration in accordance with the United Nations Commission on International Trade Law Arbitration Rules in force at the time of commencement of the arbitration. The seat, or legal place, or arbitration shall be the Philippines. The governing law of these Legal Terms shall be substantive law of the Philippines.\n\n" +
                    "Restrictions\n" +
                    "The Parties agree that any arbitration shall be limited to the Dispute between the Parties individually. To the full extent permitted by law:\n" +
                    "• no arbitration shall be joined with any other proceeding;\n" +
                    "• there is no right or authority for any Dispute to be arbitrated on a class-action basis or to utilize class action procedures; and\n" +
                    "• there is no right or authority for any Dispute to be brought in a purported representative capacity on behalf of the general public or any other persons.\n\n" +
                    "Exceptions to Informal Negotiations and Arbitration\n" +
                    "The Parties agree that the following Disputes are not subject to the above provisions concerning informal negotiations binding arbitration:\n" +
                    "• any Disputes seeking to enforce or protect, or concerning the validity of, any of the intellectual property rights of a Party;\n" +
                    "• any Dispute related to, or arising from, allegations of theft, piracy, invasion of privacy, or unauthorized use; and\n" +
                    "• any claim for injunctive relief.\n\n" +
                    "If this provision is found to be illegal or unenforceable, then neither Party will elect to arbitrate any Dispute falling within that portion of this provision found to be illegal or unenforceable and such Dispute shall be decided by a court of competent jurisdiction within the courts listed for jurisdiction above, and the Parties agree to submit to the personal jurisdiction of that court.\n\n" +
                    "17. CORRECTIONS\n" +
                    "There may be information on the Services that contains typographical errors, inaccuracies, or omissions, including descriptions, pricing, availability, and various other information. We reserve the right to correct any errors, inaccuracies, or omissions and to change or update the information on the Services at any time, without prior notice.\n\n" +
                    "18. DISCLAIMER\n" +
                    "THE SERVICES ARE PROVIDED ON AN AS-IS AND AS-AVAILABLE BASIS. YOU AGREE THAT YOUR USE OF THE SERVICES WILL BE AT YOUR SOLE RISK. TO THE FULLEST EXTENT PERMITTED BY LAW, WE DISCLAIM ALL WARRANTIES, EXPRESS OR IMPLIED, IN CONNECTION WITH THE SERVICES AND YOUR USE THEREOF, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. WE MAKE NO WARRANTIES OR REPRESENTATIONS ABOUT THE ACCURACY OR COMPLETENESS OF THE SERVICES' CONTENT OR THE CONTENT OF ANY WEBSITES OR MOBILE APPLICATIONS LINKED TO THE SERVICES AND WE WILL ASSUME NO LIABILITY OR RESPONSIBILITY FOR ANY (1) ERRORS, MISTAKES, OR INACCURACIES OF CONTENT AND MATERIALS, (2) PERSONAL INJURY OR PROPERTY DAMAGE, OF ANY NATURE WHATSOEVER, RESULTING FROM YOUR ACCESS TO AND USE OF THE SERVICES, (3) ANY UNAUTHORIZED ACCESS TO OR USE OF OUR SECURE SERVERS AND/OR ANY AND ALL PERSONAL INFORMATION AND/OR FINANCIAL INFORMATION STORED THEREIN, (4) ANY INTERRUPTION OR CESSATION OF TRANSMISSION TO OR FROM THE SERVICES, (5) ANY BUGS, VIRUSES, TROJAN HORSES, OR THE LIKE WHICH MAY BE TRANSMITTED TO OR THROUGH THE SERVICES BY ANY THIRD PARTY, AND/OR (6) ANY ERRORS OR OMISSIONS IN ANY CONTENT AND MATERIALS OR FOR ANY LOSS OR DAMAGE OF ANY KIND INCURRED AS A RESULT OF THE USE OF ANY CONTENT POSTED, TRANSMITTED, OR OTHERWISE MADE AVAILABLE VIA THE SERVICES. WE DO NOT WARRANT, ENDORSE, GUARANTEE, OR ASSUME RESPONSIBILITY FOR ANY PRODUCT OR SERVICE ADVERTISED OR OFFERED BY A THIRD PARTY THROUGH THE SERVICES, ANY HYPERLINKED WEBSITE, OR ANY WEBSITE OR MOBILE APPLICATION FEATURED IN ANY BANNER OR OTHER ADVERTISING, AND WE WILL NOT BE A PARTY TO OR IN ANY WAY BE RESPONSIBLE FOR MONITORING ANY TRANSACTION BETWEEN YOU AND ANY THIRD-PARTY PROVIDERS OF PRODUCTS OR SERVICES. AS WITH THE PURCHASE OF A PRODUCT OR SERVICE THROUGH ANY MEDIUM OR IN ANY ENVIRONMENT, YOU SHOULD USE YOUR BEST JUDGMENT AND EXERCISE CAUTION WHERE APPROPRIATE.\n\n" +
                    "19. LIMITATIONS OF LIABILITY\n" +
                    "IN NO EVENT WILL WE OR OUR DIRECTORS, EMPLOYEES, OR AGENTS BE LIABLE TO YOU OR ANY THIRD PARTY FOR ANY DIRECT, INDIRECT, CONSEQUENTIAL, EXEMPLARY, INCIDENTAL, SPECIAL, OR PUNITIVE DAMAGES, INCLUDING LOST PROFIT, LOST REVENUE, LOSS OF DATA, OR OTHER DAMAGES ARISING FROM YOUR USE OF THE SERVICES, EVEN IF WE HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.\n\n" +
                    "20. INDEMNIFICATION\n" +
                    "You agree to defend, indemnify, and hold us harmless, including our subsidiaries, affiliates, and all of our respective officers, agents, partners, and employees, from and against any loss, damage, liability, claim, or demand, including reasonable attorneys' fees and expenses, made by any third party due to or arising out of:\n" +
                    "1. your Contributions;\n" +
                    "2. use of the Services;\n" +
                    "3. breach of these Legal Terms;\n" +
                    "4. any breach of your representations and warranties set forth in these Legal Terms;\n" +
                    "5. your violation of the rights of a third party, including but not limited to intellectual property rights; or\n" +
                    "6. any overt harmful act toward any other user of the Services with whom you connected via the Services.\n\n" +
                    "Notwithstanding the foregoing, we reserve the right, at your expense, to assume the exclusive defense and control of any matter for which you are required to indemnify us, and you agree to cooperate, at your expense, with our defense of such claims. We will use reasonable efforts to notify you of any such claim, action, or proceeding which is subject to this indemnification upon becoming aware of it.\n\n" +
                    "21. USER DATA\n" +
                    "We will maintain certain data that you transmit to the Services for the purpose of managing the performance of the Services, as well as data relating to your use of the Services. Although we perform regular routine backups of data, you are solely responsible for all data that you transmit or that relates to any activity you have undertaken using the Services. You agree that we shall have no liability to you for any loss or corruption of any such data, and you hereby waive any right of action against us arising from any such loss or corruption of such data.\n\n" +
                    "22. ELECTRONIC COMMUNICATIONS, TRANSACTIONS, AND SIGNATURES\n" +
                    "Visiting the Services, sending us emails, and completing online forms constitute electronic communications. You consent to receive electronic communications, and you agree that all agreements, notices, disclosures, and other communications we provide to you electronically, via email and on the Services, satisfy any legal requirement that such communication be in writing. YOU HEREBY AGREE TO THE USE OF ELECTRONIC SIGNATURES, CONTRACTS, ORDERS, AND OTHER RECORDS, AND TO ELECTRONIC DELIVERY OF NOTICES, POLICIES, AND RECORDS OF TRANSACTIONS INITIATED OR COMPLETED BY US OR VIA THE SERVICES. You hereby waive any rights or requirements under any statutes, regulations, rules, ordinances, or other laws in any jurisdiction which require an original signature or delivery or retention of non-electronic records, or to payments or the granting of credits by any means other than electronic means.\n\n" +
                    "23. MISCELLANEOUS\n" +
                    "These Legal Terms and any policies or operating rules posted by us on the Services or in respect to the Services constitute the entire agreement and understanding between you and us. Our failure to exercise or enforce any right or provision of these Legal Terms shall not operate as a waiver of such right or provision. These Legal Terms operate to the fullest extent permissible by law. We may assign any or all of our rights and obligations to others at any time. We shall not be responsible or liable for any loss, damage, delay, or failure to act caused by any cause beyond our reasonable control.\n\n" +
                    "If any provision or part of a provision of these Legal Terms is determined to be unlawful, void, or unenforceable, that provision or part of the provision is deemed severable from these Legal Terms and does not affect the validity and enforceability of any remaining provisions. There is no joint venture, partnership, employment or agency relationship created between you and us as a result of these Legal Terms or use of the Services. You agree that these Legal Terms will not be construed against us by virtue of having drafted them. You hereby waive any and all defenses you may have based on the electronic form of these Legal Terms and the lack of signing by the parties hereto to execute these Legal Terms.\n\n" +
                    "24. CONTACT US\n" +
                    "In order to resolve a complaint regarding the Services or to receive further information regarding use of the Services, please contact us at:\n\n" +
                    "GasTrack\n" +
                    "Philippines\n" +
                    "Phone: 09926219700\n" +
                    "Email: gastrack.inventory@gmail.com",
            fontSize = 13.sp,
            color = TextDark,
            lineHeight = 18.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

