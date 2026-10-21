package com.example.gastrack.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GasTrackBlue, StaffPortalBlue)
                        )
                    )
                    .padding(top = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Privacy Policy",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Last Updated: October 08, 2026",
                fontSize = 12.sp,
                color = TextGray,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = TextGray,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    PrivacyPolicyScreen(onBack = {})
}
