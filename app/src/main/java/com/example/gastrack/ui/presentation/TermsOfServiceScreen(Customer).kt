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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreenCustomer(
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
                        text = "TERMS OF SERVICE",
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
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TermsOfServiceScreenCustomerPreview() {
    TermsOfServiceScreenCustomer(onBack = {})
}