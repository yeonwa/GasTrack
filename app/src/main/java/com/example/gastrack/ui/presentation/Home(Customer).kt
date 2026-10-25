package com.example.gastrack.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastrack.R
import com.example.gastrack.ui.theme.*

// Modern Home Colors
val HomeBackground = Color(0xFFFBFBFE)
val CardDark = Color(0xFF161D27)
val RefillGradient = listOf(Color(0xFFFFB87A), Color(0xFFFF8E3C))
val BuyTankGradient = listOf(Color(0xFF8D99D5), Color(0xFF5C6BC0))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    onNavigateToOrder: (String) -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    onNavigateToAddresses: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    Scaffold(
        containerColor = HomeBackground,
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onOrderClick = { onNavigateToOrder("New Tank") },
                onMenuClick = onNavigateToMenu
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Modern Header ---
            HomeHeader(
                onAddressClick = onNavigateToAddresses,
                onNotificationClick = onNavigateToNotifications,
                onCartClick = onNavigateToCart
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Smart Refill Card ---
            SmartRefillCard(onReorderClick = onNavigateToCart)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "What do you need?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Modern Action Tiles ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionTile(
                    text = "Refill\nLPG",
                    subText = "Quick order",
                    gradient = RefillGradient,
                    icon = Icons.Default.Refresh,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToOrder("Refill") }
                )
                ActionTile(
                    text = "New\nTank",
                    subText = "Buy cylinder",
                    gradient = BuyTankGradient,
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToOrder("New Tank") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    onAddressClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    // Reference shared state for the badge count
    val totalItems = SharedCartState.cart.values.sum()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ... (existing Deliver To section) ...
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onAddressClick() }
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.favicon),
                    contentDescription = "Mini Logo",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "DELIVER TO",
                    fontSize = 11.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Malabon City",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = GasTrackRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Cart Icon Button
            IconButton(
                onClick = onCartClick,
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = TextDark,
                        modifier = Modifier.size(26.dp)
                    )
                    if (totalItems > 0) {
                        Surface(
                            color = GasTrackRed,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                        ) {}
                    }
                }
            }

            // Notifications Icon Button
            IconButton(
                onClick = onNotificationClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = TextDark,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun SmartRefillCard(onReorderClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .shadow(16.dp, RoundedCornerShape(28.dp))
            .background(CardDark, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
    ) {
        // Aesthetic Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(GasTrackBlue.copy(alpha = 0.3f), Color.Transparent),
                        radius = 400f
                    )
                )
        )

        // Subtle Icon Background
        Icon(
            imageVector = Icons.Default.ViewInAr,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.05f),
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
        )

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Last order: 42 days ago",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Column {
                Text(
                    text = "Time for a refill?",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp
                )
                Text(
                    text = "Based on your previous usage",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onReorderClick,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "1-Click Reorder",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ActionTile(text: String, subText: String, gradient: List<Color>, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .height(140.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(gradient), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .clip(RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subText,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Small decorative icon at bottom right
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-8).dp)
        )
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationBarItem(
                isSelected = currentRoute == "home",
                icon = Icons.Default.Home,
                label = "Home",
                onClick = onHomeClick
            )
            NavigationBarItem(
                isSelected = currentRoute == "order",
                icon = Icons.Default.ShoppingCart,
                label = "Order",
                onClick = onOrderClick
            )
            NavigationBarItem(
                isSelected = currentRoute == "menu",
                icon = Icons.Default.Menu,
                label = "Menu",
                onClick = onMenuClick
            )
        }
    }
}

@Composable
fun NavigationBarItem(isSelected: Boolean, icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GasTrackRed else TextGray,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GasTrackRed else TextGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerHomeScreenPreview() {
    CustomerHomeScreen(onNavigateToOrder = {}, onNavigateToMenu = {})
}
