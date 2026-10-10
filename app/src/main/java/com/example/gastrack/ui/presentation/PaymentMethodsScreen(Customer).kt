package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                text = "SAVED PAYMENT METHODS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextGray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                // Cash Option Row (Mirrored perfectly from Checkout Tab specifications)
                CheckoutPaymentRow(
                    title = "Cash",
                    isSelected = selectedPaymentMethod == "Cash",
                    icon = { Icon(Icons.Default.Payments, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.padding(8.dp)) },
                    onClick = { selectedPaymentMethod = "Cash" }
                )

                // GCash Option Row (Mirrored perfectly from Checkout Tab specifications)
                CheckoutPaymentRow(
                    title = "GCash",
                    isSelected = selectedPaymentMethod == "GCash",
                    icon = { 
                        Image(
                            painter = painterResource(id = R.drawable.gcash_logo), 
                            contentDescription = null, 
                            modifier = Modifier.padding(6.dp).fillMaxSize()
                        ) 
                    },
                    onClick = { selectedPaymentMethod = "GCash" }
                )

                // Card Option Row (Mirrored perfectly from Checkout Tab specifications)
                CheckoutPaymentRow(
                    title = "Card",
                    isSelected = selectedPaymentMethod == "Card",
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = StaffPortalBlue, modifier = Modifier.padding(8.dp)) },
                    onClick = { selectedPaymentMethod = "Card" }
                )
            }

            // Primary operation button to lock in state configurations
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange)
            ) {
                Text("Save Selection", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodsScreenPreview() {
    PaymentMethodsScreen(onBack = {})
}
