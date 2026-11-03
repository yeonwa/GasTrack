package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    onBack: () -> Unit
) {
    // Current selection mirrors what's configured on the user checkout tab
    var selectedPaymentMethod by remember { mutableStateOf("GCash") }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFBFBFE),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Payment Methods",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "DELIVERY PAYMENT OPTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextGray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cash Option Row
                CheckoutPaymentRow(
                    title = "Cash",
                    isSelected = selectedPaymentMethod == "Cash",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = StaffPortalBlue,
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    onClick = { selectedPaymentMethod = "Cash" }
                )

                // GCash Option Row
                CheckoutPaymentRow(
                    title = "GCash",
                    isSelected = selectedPaymentMethod == "GCash",
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.gcash_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize()
                        )
                    },
                    onClick = { selectedPaymentMethod = "GCash" }
                )

                // Card Option Row
                CheckoutPaymentRow(
                    title = "Card",
                    isSelected = selectedPaymentMethod == "Card",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = StaffPortalBlue,
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    onClick = { selectedPaymentMethod = "Card" }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Add Payment Method Row
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = GasTrackBlue.copy(alpha = 0.08f)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Payment Method",
                                    tint = GasTrackBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Add Payment Method",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextDark
                            )
                            Text(
                                text = "Link a new credit card or e-wallet",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.NavigateNext,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Primary operation button with GasTrack primary brand blue
            Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GasTrackBlue)
            ) {
                Text("Done", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.White)
            }
        }

        // Add Payment Method Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Payment Method", fontWeight = FontWeight.Bold) },
                text = { Text("Select e-wallet or credit card details to link to your GasTrack account.") },
                confirmButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = GasTrackBlue)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodsScreenPreview() {
    PaymentMethodsScreen(onBack = {})
}
