package com.example.smart_medicine_android.navigation

/**
 * 应用页面路由定义
 */
sealed class Screen(val route: String) {
    // 启动页
    object Splash : Screen("splash")

    // 登录/注册
    object Login : Screen("login")

    // 首页（底部导航）
    object Home : Screen("home")

    // 视频（底部导航）
    object Video : Screen("video")

    // AI 咨询（底部导航）
    object Consultation : Screen("consultation")

    // 我的（底部导航）
    object Profile : Screen("profile")

    // 疾病详情
    object IllnessDetail : Screen("illness_detail/{illnessId}") {
        fun createRoute(illnessId: Int) = "illness_detail/$illnessId"
    }

    // 药品详情
    object MedicineDetail : Screen("medicine_detail/{medicineId}") {
        fun createRoute(medicineId: Int) = "medicine_detail/$medicineId"
    }

    // 资讯详情
    object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: Int) = "news_detail/$newsId"
    }

    // 设置
    object Settings : Screen("settings")

    // 视频详情
    object VideoDetail : Screen("video_detail/{videoId}") {
        fun createRoute(videoId: Int) = "video_detail/$videoId"
    }

    // 浏览历史
    object History : Screen("history")

    // 用户反馈
    object Feedback : Screen("feedback")

    // 提交反馈
    object FeedbackSubmit : Screen("feedback_submit")

    // 编辑个人资料
    object EditProfile : Screen("edit_profile")

    // 修改密码
    object ChangePassword : Screen("change_password")

    // 关于
    object About : Screen("about")
}
