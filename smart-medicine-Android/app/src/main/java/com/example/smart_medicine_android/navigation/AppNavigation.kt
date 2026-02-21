package com.example.smart_medicine_android.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.example.smart_medicine_android.ui.screen.splash.SplashScreen
import com.example.smart_medicine_android.ui.screen.auth.LoginScreen
import com.example.smart_medicine_android.ui.screen.about.AboutScreen
import com.example.smart_medicine_android.ui.screen.home.HomeScreen
import com.example.smart_medicine_android.ui.screen.illness.IllnessDetailScreen
import com.example.smart_medicine_android.ui.screen.consultation.ConsultationScreen
import com.example.smart_medicine_android.ui.screen.profile.ProfileScreen
import com.example.smart_medicine_android.ui.screen.medicine.MedicineDetailScreen
import com.example.smart_medicine_android.ui.screen.news.NewsDetailScreen
import com.example.smart_medicine_android.ui.screen.settings.SettingsScreen
import com.example.smart_medicine_android.ui.screen.video.VideoScreen
import com.example.smart_medicine_android.ui.screen.video.VideoDetailScreen
import com.example.smart_medicine_android.ui.screen.history.HistoryScreen
import com.example.smart_medicine_android.ui.screen.feedback.FeedbackListScreen
import com.example.smart_medicine_android.ui.screen.feedback.FeedbackSubmitScreen
import com.example.smart_medicine_android.ui.screen.profile.EditProfileScreen
import com.example.smart_medicine_android.ui.screen.profile.ChangePasswordScreen
import com.example.smart_medicine_android.ui.theme.*
import com.example.smart_medicine_android.ui.navigation.ModernBottomNavigation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_medicine_android.ui.screen.home.HomeViewModel

/**
 * 应用导航主机（带底部导航栏）
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    val homeViewModel: HomeViewModel = viewModel()

    // 当前选中的底部导航项
    var selectedRoute by remember { mutableStateOf(startDestination) }

    // 监听导航变化，更新选中的导航项
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .collect { backStackEntry ->
                val route = backStackEntry.destination.route
                if (route != null && route in listOf("home", "video", "consultation", "profile")) {
                    selectedRoute = route
                }
            }
    }

    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: startDestination
    val currentBottomNavRoute = selectedRoute.takeIf { it in listOf("home", "video", "consultation", "profile") } ?: "home"

    androidx.compose.material3.Scaffold(
        modifier = modifier,
        bottomBar = {
            // 启动页和登录页不显示底部导航栏
            if (currentRoute !in listOf("splash", "login") &&
                (currentRoute in listOf("home", "video", "consultation", "profile") || currentRoute.contains("illness_detail"))) {
                ModernBottomNavigation(
                    currentRoute = currentBottomNavRoute,
                    onNavigate = { route ->
                        selectedRoute = route
                        navController.navigate(route) {
                            popUpTo(route) { inclusive = true }
                        }
                    },
                    onHomeDoubleTap = {
                        // 双击首页图标触发数据同步
                        homeViewModel.syncData()
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 启动页
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 登录页面
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 首页（疾病列表）
            composable(Screen.Home.route) {
                HomeScreen(
                    onIllnessClick = { illnessId ->
                        navController.navigate(Screen.IllnessDetail.createRoute(illnessId))
                    },
                    onConsultationClick = {
                        navController.navigate(Screen.Consultation.route)
                    },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onMedicineClick = { medicineId ->
                        navController.navigate(Screen.MedicineDetail.createRoute(medicineId))
                    },
                    onNewsClick = { newsId ->
                        navController.navigate(Screen.NewsDetail.createRoute(newsId))
                    },
                    viewModel = homeViewModel
                )
            }

            // 视频页面
            composable(Screen.Video.route) {
                VideoScreen(
                    onVideoClick = { videoId ->
                        navController.navigate(Screen.VideoDetail.createRoute(videoId))
                    }
                )
            }

            // 视频详情页面
            composable(
                route = Screen.VideoDetail.route,
                arguments = listOf(navArgument("videoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getInt("videoId") ?: 0
                VideoDetailScreen(
                    videoId = videoId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 疾病详情页面
            composable(
                route = Screen.IllnessDetail.route,
                arguments = listOf(navArgument("illnessId") { type = NavType.IntType })
            ) { backStackEntry ->
                val illnessId = backStackEntry.arguments?.getInt("illnessId") ?: 0
                IllnessDetailScreen(
                    illnessId = illnessId,
                    onBackClick = { navController.popBackStack() },
                    onMedicineClick = { medicineId ->
                        navController.navigate(Screen.MedicineDetail.createRoute(medicineId))
                    }
                )
            }

            // 药品详情页面
            composable(
                route = Screen.MedicineDetail.route,
                arguments = listOf(navArgument("medicineId") { type = NavType.IntType })
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
                MedicineDetailScreen(
                    medicineId = medicineId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 资讯详情页面
            composable(
                route = Screen.NewsDetail.route,
                arguments = listOf(navArgument("newsId") { type = NavType.IntType })
            ) { backStackEntry ->
                val newsId = backStackEntry.arguments?.getInt("newsId") ?: 0
                NewsDetailScreen(
                    newsId = newsId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // AI 咨询页面
            composable(Screen.Consultation.route) {
                ConsultationScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 用户中心页面
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onHistoryClick = { navController.navigate(Screen.History.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onEditClick = { navController.navigate(Screen.EditProfile.route) }
                )
            }

            // 设置页面
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onChangePasswordClick = { navController.navigate(Screen.ChangePassword.route) },
                    onFeedbackClick = { navController.navigate(Screen.Feedback.route) },
                    onAboutClick = { navController.navigate(Screen.About.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 浏览历史页面
            composable(Screen.History.route) {
                HistoryScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 用户反馈列表页面
            composable(Screen.Feedback.route) {
                FeedbackListScreen(
                    onSubmitClick = { navController.navigate(Screen.FeedbackSubmit.route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 提交反馈页面
            composable(Screen.FeedbackSubmit.route) {
                FeedbackSubmitScreen(
                    onSubmitSuccess = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 编辑个人资料页面
            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    onSaveSuccess = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 修改密码页面
            composable(Screen.ChangePassword.route) {
                ChangePasswordScreen(
                    onChangeSuccess = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 关于页面
            composable(Screen.About.route) {
                AboutScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
