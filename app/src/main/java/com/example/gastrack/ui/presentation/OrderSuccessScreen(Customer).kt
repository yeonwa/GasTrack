package com.example.gastrack.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.ui.theme.*

@Composable
fun OrderSuccessScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFE))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Success Icon
        Surface(
            shape = CircleShape,
            color = Color(0xFFE8F5E9),
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.padding(16.dp).fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Order Placed Successfully!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextDark, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Your LPG order has been confirmed and is being processed for delivery.", fontSize = 14.sp, color = TextGray, textAlign = TextAlign.Center, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Receipt Card
        Surface(
            modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
            color = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("E-RECEIPT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                ReceiptRow("Order Number", "#GT-10294", isBold = true)
                ReceiptRow("Date", "Oct 24, 2026")
                ReceiptRow("Payment Method", "Cash on Delivery")
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))
                
                ReceiptRow("11kg Standard Refill (x1)", "₱1,150")
                ReceiptRow("Delivery Fee", "₱50")
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Total", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
                    Text("₱1,200", fontSize = 22.sp, fontWeight = FontWeight.Black, color = ButtonOrange)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNavigateToOrders,
            modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange)
        ) {
            Text("View Order Details", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToHome,
            modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.5.dp, GasTrackBlue)
        ) {
            Text("Return to Home", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GasTrackBlue)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ReceiptRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextGray)
        Text(value, fontSize = 14.sp, fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold, color = TextDark)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderSuccessScreenPreview() {
    OrderSuccessScreen(onNavigateToHome = {}, onNavigateToOrders = {})
}
