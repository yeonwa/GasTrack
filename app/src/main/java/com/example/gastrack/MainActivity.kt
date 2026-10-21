package com.example.gastrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gastrack.ui.presentation.*
import com.example.gastrack.ui.theme.GasTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GasTrackTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "startup",
    ) {
        composable("startup") {
            StartUpScreen(
                onNavigateToCustomer = {
                    navController.navigate("login_customer")
                },
                onNavigateToEmployee = {
                    navController.navigate("login_employee")
                },
                onNavigateToRegisterCompany = {
                    navController.navigate("register_company")
                }
            )
        }
        composable("login_employee") {
            LogInEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate("register_company")
                },
                onNavigateToHome = {
                    navController.navigate("employee_menu") {
                        popUpTo("startup") { inclusive = true }
                    }
                }
            )
        }
        composable("register_company") {
            RegisterLPGCompanyScreen(
                onBack = {
                    navController.popBackStack()
                },
            ) {
                navController.navigate("register_account")
            }
        }
        composable("register_account") {
            RegisterAccountEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRegisterComplete = {
                    navController.navigate("employee_menu") {
                        popUpTo("startup") { inclusive = true }
                    }
                }
            )
        }
        composable("employee_dashboard") {
            DashboardEmployeeScreen(
                onNavigateToMenu = {
                    navController.navigate("employee_menu")
                }
            )
        }
        composable("employee_inventory") {
            InventoryEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_pos") {
            POSEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_sales") {
            SalesEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_products") {
            ProductsEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_suppliers") {
            SuppliersEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_data") {
            DataModuleEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_report_compliance") {
            ReportComplianceScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_users") {
            UsersEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_user_modify") {
            UserModifyEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                },
                onModifyComplete = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_restocking") {
            RestockingEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_orders") {
            OrdersEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_menu") {
            MenuEmployeeScreen(
                onNavigateToDashboard = {
                    navController.navigate("employee_dashboard") {
                        popUpTo("employee_dashboard") { inclusive = true }
                    }
                },
                onNavigateToOrders = {
                    navController.navigate("employee_orders")
                },
                onNavigateToPOS = {
                    navController.navigate("employee_pos")
                },
                onNavigateToSales = {
                    navController.navigate("employee_sales")
                },
                onNavigateToInventory = {
                    navController.navigate("employee_inventory")
                },
                onNavigateToProducts = {
                    navController.navigate("employee_products")
                },
                onNavigateToSuppliers = {
                    navController.navigate("employee_suppliers")
                },
                onNavigateToRestocking = {
                    navController.navigate("employee_restocking")
                },
                onNavigateToUsers = {
                    navController.navigate("employee_users")
                },
                onNavigateToReports = {
                    navController.navigate("employee_data")
                },
                onNavigateToReportCompliance = {
                    navController.navigate("employee_report_compliance")
                },
                onNavigateToSettings = {
                    navController.navigate("employee_settings")
                },
                onNavigateToLogout = {
                    navController.navigate("startup") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToNotifications = {
                    navController.navigate("employee_notifications")
                }
            )
        }
        composable("employee_settings") {
            SettingsEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("employee_notifications") {
            NotificationEmployeeScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToReport = {
                    navController.navigate("employee_report_compliance")
                }
            )
        }
        composable("login_customer") {
            LogInCustomerScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate("create_account_customer")
                },
                onNavigateToHome = {
                    navController.navigate("customer_home") {
                        popUpTo("startup") { inclusive = true }
                    }
                }
            )
        }
        composable("create_account_customer") {
            CreateAccountCustomerScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAccountCreated = {
                    navController.navigate("customer_home") {
                        popUpTo("startup") { inclusive = true }
                    }
                }
            )
        }
        composable("customer_home") {
            CustomerHomeScreen(
                onNavigateToOrder = { targetTab ->
                    navController.navigate("order_tab/$targetTab")
                },
                onNavigateToAR = {
                    navController.navigate("ar_loading")
                },
                onNavigateToMenu = {
                    navController.navigate("menu")
                },
                onNavigateToCart = {
                    navController.navigate("cart")
                },
                onNavigateToAddresses = {
                    navController.navigate("addresses")
                },
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                }
            )
        }
        composable("notifications") {
            NotificationScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("menu") {
            MenuScreen(
                onNavigateToHome = {
                    navController.navigate("customer_home") {
                        popUpTo("customer_home") { inclusive = true }
                    }
                },
                onNavigateToOrder = {
                    navController.navigate("order_tab/New Tank")
                },
                onNavigateToLogout = {
                    navController.navigate("logout_customer")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToHistory = {
                    navController.navigate("order_history")
                },
                onNavigateToAddresses = {
                    navController.navigate("addresses")
                },
                onNavigateToPaymentMethods = {
                    navController.navigate("payment_methods")
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate("privacy_policy")
                },
                onNavigateToTermsOfService = {
                    navController.navigate("terms_of_service")
                }
            )
        }
        composable("logout_customer") {
            LogOutCustomerScreen(
                onLogout = {
                    navController.navigate("startup") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable("ar_loading") {
            ARLoadingScreen(
                onLoadingComplete = {
                    navController.navigate("ar_choose") {
                        popUpTo("ar_loading") { inclusive = true }
                    }
                }
            )
        }
        composable("ar_choose") {
            ARChooseScreen(
                onContinue = {
                    navController.navigate("ar_scan")
                }
            )
        }
        composable("ar_scan") {
            ARScanScreen(
                onScanComplete = {
                    navController.navigate("order_tab/New Tank") {
                        popUpTo("customer_home")
                    }
                }
            )
        }
        composable(
            route = "order_tab/{initialTab}",
            arguments = listOf(navArgument("initialTab") { type = NavType.StringType })
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("initialTab") ?: "New Tank"
            OrderTabScreen(
                initialTab = initialTab,
                onNavigateToHome = {
                    navController.navigate("customer_home") {
                        popUpTo("customer_home") { inclusive = true }
                    }
                },
                onNavigateToCheckout = {
                    navController.navigate("cart")
                },
                onNavigateToMenu = {
                    navController.navigate("menu")
                }
            )
        }
        composable("cart") {
            CartScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate("checkout")
                },
                onContinueShopping = {
                    navController.popBackStack()
                }
            )
        }
        composable("checkout") {
            CheckoutScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPlaceOrder = {
                    // Handle Order Placement
                },
                onNavigateToTracking = {
                    navController.navigate("customer_home") {
                        popUpTo("customer_home") { inclusive = true }
                    }
                },
                onNavigateToAddresses = {
                    navController.navigate("addresses")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("order_history") {
            OrderHistoryScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("addresses") {
            AddressesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("payment_methods") {
            PaymentMethodsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("about") {
            AboutScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("privacy_policy") {
            PrivacyPolicyScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("terms_of_service") {
            TermsOfServiceScreenCustomer(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
